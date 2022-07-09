/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2018,2020-2022 Roger L. Whitcomb.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *	Network utility methods.
 *
 *  History:
 *	24-Oct-2012 (rlwhitcomb)
 *	    Initial coding; taken from existing code in other places.
 *	01-Feb-2013 (rlwhitcomb)
 *	    Moved some of the socket connect code into here.
 *	03-Dec-2013 (rlwhitcomb)
 *	    Set SO_KEEPALIVE on the socket to help long-running but
 *	    idle connections detect server failure sooner.
 *	09-Dec-2013 (rlwhitcomb)
 *	    Add method to get the "best" name (unqualified).
 *	28-Jan-2014 (rlwhitcomb)
 *	    Add methods to extract user and domain names from a "DOMAIN\USER"
 *	    or "USER@DOMAIN" user name string.
 *	04-Mar-2014 (rlwhitcomb)
 *	    Moved small routines to recognize IPv4 and IPv6 addresses into here.
 *	21-May-2014 (rlwhitcomb)
 *	    Int "getBestHostName" also check for empty FQ name.
 *	12-Jun-2014 (rlwhitcomb)
 *	    Added two methods to deal with host names that could/should be equals
 *	    but have different domains:  one to extract just the host name, and
 *	    the other to do that and compare two of them.
 *	18-Jun-2015 (rlwhitcomb)
 *	    Provide a version of "isLocalMachine" that doesn't do network searches
 *	    for the host name.  This is for cases when we want to readily tell if
 *	    we're dealing with the local machine while typing.
 *	31-Aug-2015 (rlwhitcomb)
 *	    Cleanup Javadoc (found by Java 8).
 *	07-Jan-2016 (rlwhitcomb)
 *	    More Javadoc fixes.
 *	28-Mar-2016 (rlwhitcomb)
 *	    Use our methods for determining literal IP addresses so that we don't
 *	    mistakenly use the FQ name as the host.domain name if it is a literal
 *	    address, in "getBestHostName()".
 *	14-Apr-2016 (rlwhitcomb)
 *	    Use Apache Commons Validator methods to validate IP addresses, instead
 *	    of rolling our own, or using undocumented Java internal routines.
 *	05-Sep-2017 (rlwhitcomb)
 *	    Use new method in CharUtil to compare if possibly null strings are equal.
 *	10-Nov-2017 (rlwhitcomb)
 *	    Separate out the calls to set socket parameters so we can set those after the
 *	    connect happens (helps with hangs trying to reconnect).
 *	23-Mar-2018 (rlwhitcomb)
 *	    "isLocalMachine" needs help to deal with FQ names vs. the non-FQ form.
 *	06-Aug-2018 (rlwhitcomb)
 *	    Don't use a "localhost..." FQ name if the plain name is better.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	07-Jul-2021 (rlwhitcomb)
 *	    Make the class final and the constructor private.
 *	09-Jul-2022 (rlwhitcomb)
 *	    #393: Cleanup imports.
 */
package info.rlwhitcomb.util;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.io.IOException;
import java.net.*;


/**
 * A package of static utility functions for various network things.
 */
public final class NetworkUtil
{
	/** The local address according to IPv4. */
	private static InetAddress localv4;

	/** The local address according to IPv6. */
	private static InetAddress localv6;

	/** The singleton instance of the {@link InetAddressValidator} we can use. */
	private static InetAddressValidator addressValidator = InetAddressValidator.getInstance();

	static
	{
		try {
		    localv4 = InetAddress.getByName("127.0.0.1");
		    localv6 = InetAddress.getByName("::1");
		} catch (UnknownHostException uhe) {
		    throw new RuntimeException(uhe);
		}
	}

	public static final String LOCAL_PREFIX = "local";

	public static final String LOCALHOST = "localhost";

	/**
	 * Private constructor since this is a utility class.
	 */
	private NetworkUtil() {
	}

	/**
	** Is the given socket connection from a local client?
	**
	** @param	socket	The currently connected socket object.
	** @return	True if local connection, false if remote or error occurs.
	*/
	public static boolean isLocalConnection(Socket socket)
	{
	    InetAddress addr = socket.getInetAddress();
	    if ( addr.equals( localv4 ) || addr.equals( localv6 ) )
		return( true );

	    try {
		// Test against the current local IP address
		// to see if we're a local connection
		// (this could change over time)
		if ( addr.equals( InetAddress.getLocalHost() ) )
		    return( true );
	    } catch (UnknownHostException e) { }

	    // TODO: some other checks may be needed

	    return( false );
	} // isLocalConnection()


	/**
	 * Return the local host name as known to the network.
	 * @return  The local machine name or <code>"local"</code> if it can't
	 *          be determined (that is {@link #LOCAL_PREFIX}).
	 */
	public static String getLocalHostName() {
	    String localMachineName;
	    try {
		localMachineName = InetAddress.getLocalHost().getHostName();
	    }
	    catch(Exception ex) {
		localMachineName = LOCAL_PREFIX;
	    }
	    return localMachineName;
	}


	/**
	 * Determine if the given server name refers to the local machine or not.
	 * <p> A null or empty name returns false.
	 * <p> Note: needs better checking for local IP address esp. for IPv6
	 * <p> Noticed that machine name changes going in and out of some wireless
	 * networks also, which could be a problem if a server is started on
	 * one network, and stopped under another one.  The machine name might
	 * not match.
	 * <p> Note: this method could be time consuming on some networks also.
	 *
	 * @param	serverName	The candidate server name to test.
	 * @return			The result from {@code isLocalMachine(serverName, true)}.
	 */
	public static boolean isLocalMachine(String serverName) {
	    return isLocalMachine(serverName, true);
	}


	/**
	 * Determine if the given server name refers to the local machine or not.
	 * <p> A null or empty name returns false.
	 * <p> Note: needs better checking for local IP address esp. for IPv6
	 * <p> Noticed that machine name changes going in and out of some wireless
	 * networks also, which could be a problem if a server is started on
	 * one network, and stopped under another one.  The machine name might
	 * not match.
	 * <p> Note: this method could be time consuming on some networks also.
	 *
	 * @param	serverName	The candidate server name to test.
	 * @param	tryNetwork	{@code true} to try resolving the host
	 *				name via DNS, or {@code false} to just
	 *				do simple checks that don't go out on
	 *				the network.
	 * @return			If the name is {@code null} or empty
	 *				then {@code false}, or if we match
	 *				the {@link #LOCALHOST} value return {@code true},
	 *				or else {@code true} if the name
	 *				matches the local host name.
	 */
	public static boolean isLocalMachine(String serverName, boolean tryNetwork) {
	    if (serverName == null || serverName.isEmpty())
		return false;

	    if (serverName.equalsIgnoreCase(LOCALHOST) ||
		serverName.equalsIgnoreCase(LOCAL_PREFIX)) {
		return true;
	    }

	    // Compare to the local machine name.
	    String localHostName = getLocalHostName();
	    if (localHostName.equalsIgnoreCase(serverName))
		return true;
	    // Try without domain name if either has one
	    int ix1 = localHostName.indexOf('.');
	    int ix2 = serverName.indexOf('.');
	    if ((ix1 >= 0 && ix2 < 0) || (ix1 < 0 && ix2 >= 0)) {
		String bareLocalHost = (ix1 < 0) ? localHostName : localHostName.substring(0, ix1);
		String bareServer = (ix2 < 0) ? serverName : serverName.substring(0, ix2);
		if (bareLocalHost.equalsIgnoreCase(bareServer))
		    return true;
	    }

	    // If we're trying to get a fast resolution of the "is this referring
	    // to the local machine" question, then don't bother looking up over
	    // the network, because it takes too much time.
	    if (!tryNetwork) {
		return false;
	    }

	    try {
		InetAddress iname = InetAddress.getByName(serverName);
		if (iname.equals(localv4) ||
		    iname.equals(localv6))
		    return true;
	    }
	    catch (UnknownHostException uhe) {
		;
	    }
	    return false;
	}


	/**
	 * Formulate the "best" name for the given host.  Selects
	 * either the hostname or the unqualified part of the fully-qualified
	 * name (if any) depending on whether they match.  Returns the
	 * case of the hostname (for display consistency).
	 * <p> The rationale for this comes from Windows where the COMPUTERNAME
	 * field is limited to 15 characters, but Active Directory has a 24
	 * character limit, so you can get host names that are truncated and
	 * thus can't be used to find the host via directory services.
	 * <p> We also need to be careful because sometimes the FQ name comes
	 * back as a straight IP address (as when a machine isn't part of a domain),
	 * so recognize that and don't use a {@code "10.2.1.100"} address as a
	 * host.domain name.
	 *
	 * @param	hostName	The (maybe) unqualified host name.
	 * @param	fullyQualifiedName	The (probably) fully-qualified name.
	 * @return	The best of both worlds.
	 */
	public static String getBestHostName(String hostName, String fullyQualifiedName) {
	    if (!CharUtil.isNullOrEmpty(fullyQualifiedName) &&
		!isIPv4Address(fullyQualifiedName) && !isIPv6Address(fullyQualifiedName)) {
		int ix = fullyQualifiedName.indexOf('.');
		String unqualifiedName = (ix < 0) ? fullyQualifiedName : fullyQualifiedName.substring(0, ix);
		// If names are the same (unqualified) then there is no controversy
		if (unqualifiedName.equalsIgnoreCase(hostName))
		    return hostName;
		// If the "qualified" name is just "localhost" then return the other
		if (unqualifiedName.equals(LOCALHOST))
		    return hostName;
		// But, if not, then return the unqualified name in the case of the hostname
		if (hostName.toUpperCase().equals(hostName))
		    return unqualifiedName.toUpperCase();
		else
		    return unqualifiedName.toLowerCase();
	    }
	    else {
		return hostName;
	    }
	}


	/**
	 * Extract just the host name (if there is a domain suffix).
	 * <p> This is needed in (rare) cases where two users login to different
	 * domains, but yet are on the same host, so we need to compare just
	 * the machine name, independent of the domain.
	 *
	 * @param	fullyQualifiedName	The name with (maybe) a domain suffix.
	 * @return	The name with the domain part removed.
	 */
	public static String getHostPart(String fullyQualifiedName) {
	    if (fullyQualifiedName != null && !fullyQualifiedName.trim().isEmpty()) {
		int ix = fullyQualifiedName.indexOf('.');
		if (ix >= 0) {
		    return fullyQualifiedName.substring(0, ix);
		}
	    }
	    // Default to returning whatever we were given
	    return fullyQualifiedName;
	}


	/**
	 * Are the two unqualified host names the same?
	 * <p> Uses the {@link #getHostPart} method to extract the two host
	 * names from the inputs, and then compares them.
	 *
	 * @param fullyQualifiedName1	The first name to test.
	 * @param fullyQualifiedName2	And the second name.
	 * @return	Whether or not these names are equivalent (that is,
	 *		regardless of the domain of either).
	 */
	public static boolean areHostNamesEqual(String fullyQualifiedName1, String fullyQualifiedName2) {
	    String hostName1 = getHostPart(fullyQualifiedName1);
	    String hostName2 = getHostPart(fullyQualifiedName2);
	    return CharUtil.stringsEqual(hostName1, hostName2);
	}


	/**
	 * Get a client socket connection to the local host and the
	 * given port, respecting the timeout value.  Sets common options
	 * used for all our sockets (such as TCP_NODELAY).
	 *
	 * @param	port		The port to connect to on the local machine.
	 * @param	connectTimeout	The timeout value used for the connect (in
	 *				milliseconds, where &lt;= 0 implies infinite).
	 * @param	readTimeout	The timeout value used for subsequent reads.
	 *
	 * @return	The socket object used to communicate to the host.
	 * @throws	IOException if there is a network error.
	 */
	public static Socket localConnect(int port, int connectTimeout, int readTimeout)
		throws IOException
	{
	    // Prefer to connect via IPv4 on the local host
	    // TODO: could this be better, or try IPv6 if there is an error on IPv4?
	    return connect(localv4, port, connectTimeout, readTimeout);
	}


	/**
	 * Get a client socket connection to the given host and port
	 * respecting the timeout value.  Sets common options used
	 * for all our sockets (such as TCP_NODELAY).
	 *
	 * @param	hostName	Host name, which can be the public
	 *				constants {@link #LOCAL_PREFIX} or
	 *				{@link #LOCALHOST}.
	 * @param	port		The port number to connect to.
	 * @param	connectTimeout	Timeout value to wait to complete
	 *				the connection (in milliseconds, &lt;= 0
	 *				implies infinite wait).
	 * @param	readTimeout	Timeout value used for subsequent reads.
	 *
	 * @return	The socket object used to communicate to the host.
	 * @throws	IOException if there is a network error.
	 */
	public static Socket connect(String hostName, int port, int connectTimeout, int readTimeout)
		throws IOException
	{
	    // Prefer to connect via IPv4 to the local machine
	    // TODO: could this be better, or also check IPv6 if v4 is not available?
	    if (isLocalMachine(hostName))
		return connect(localv4, port, connectTimeout, readTimeout);

	    SocketAddress sockAddr = new InetSocketAddress(hostName, port);
	    return connectImpl(sockAddr, connectTimeout, readTimeout);
	}


	/**
	 * Get a client socket connection to the given host and port
	 * respecting the timeout value.  Sets common options used
	 * for all our sockets (such as TCP_NODELAY).
	 *
	 * @param	hostAddr	The host address to connect to.
	 * @param	port		The port value on that host.
	 * @param	connectTimeout	The connection timeout value (in
	 *				milliseconds, &lt;= 0 implies infinite
	 *				wait to connect).
	 * @param	readTimeout	Timeout value for subsequent reads.
	 *
	 * @return	The socket object used to communicate to the host.
	 * @throws	IOException if there is a network error.
	 */
	public static Socket connect(InetAddress hostAddr, int port, int connectTimeout, int readTimeout)
		throws IOException
	{
	    SocketAddress sockAddr = new InetSocketAddress(hostAddr, port);
	    return connectImpl(sockAddr, connectTimeout, readTimeout);
	}


	/**
	 * Internal routine to do the connect and set common options on the socket.
	 *
	 * @param	sockAddr	The address to connect to.
	 * @param	connectTimeout	The connection timeout value (in
	 *				milliseconds, &lt;= 0 implies infinite
	 *				wait to connect).
	 * @param	readTimeout	Timeout value for subsequent reads.
	 *
	 * @return	The socket object used to communicate to the host.
	 * @throws	IOException if there is a network error.
	 */
	private static Socket connectImpl(SocketAddress sockAddr, int connectTimeout, int readTimeout)
		throws IOException
	{
	    if (connectTimeout < 0)
		connectTimeout = 0;

	    Socket sock = new Socket();
	    sock.connect(sockAddr, connectTimeout);

	    setSocketParams(sock, readTimeout);

	    return sock;
	}


	/**
	 * Set our preferred client socket parameters on this socket.
	 *
	 * @param	socket	The alredy established socket to set parameters for.
	 * @param	readTimeout	The desired read timeout value (milliseconds).
	 * @throws	SocketException if something goes wrong, such as the network
	 *		connection drops.
	 */
	public static void setSocketParams(Socket socket, int readTimeout)
		throws SocketException
	{
	    if (readTimeout < 0)
		readTimeout = 0;

	    socket.setTcpNoDelay(true);
	    socket.setSoTimeout(readTimeout);
	    socket.setKeepAlive(true);
	}


	/**
	 * Extract the user name part of a domain / user combination.
	 * <p> The allowed syntaxes are:
	 * <ul>
	 * <li><code>DOMAIN\USER</code>
	 * <li><code>USER@DOMAIN</code>
	 * </ul>
	 * <p> Strange, but if the separator char is present but no user part before/after
	 * then just return the whole string (maybe the user name makes sense, who knows?)
	 * <p>According to (for Windows): http://msdn.microsoft.com/en-us/library/windows/desktop/aa380525(v=vs.85).aspx
	 * @param	domainUser	The candidate string.
	 * @return	Either only the user part of the input if there is a domain specified,
	 *		or the whole input string if not.
	 * @see	#getDomainOnly
	 */
	public static String getUserOnly(String domainUser) {
	    int ix = -1;
	    if ((ix = domainUser.indexOf('\\')) >= 0 &&
		ix + 1 < domainUser.length()) {
		// DOMAIN\USER - return the suffix part
		return domainUser.substring(ix + 1);
	    }
	    else if ((ix = domainUser.indexOf('@')) >= 1) {
		// USER@DOMAIN - return the prefix part
		return domainUser.substring(0, ix);
	    }
	    return domainUser;
	}


	/**
	 * Extract the domain part of a domain / user combination.
	 * <p> The allowed syntaxes are:
	 * <ul>
	 * <li><code>DOMAIN\USER</code>
	 * <li><code>USER@DOMAIN</code>
	 * </ul>
	 * <p> Strange, but if the separator char is present but no domain part before/after
	 * then just return {@code null} (because the whole string is returned as the user part).
	 * <p>According to (for Windows): http://msdn.microsoft.com/en-us/library/windows/desktop/aa380525(v=vs.85).aspx
	 * @param	domainUser	The candidate string.
	 * @return	Either only the domain part of the input if there is a domain specified,
	 *		or {@code null} if not.
	 * @see	#getUserOnly
	 */
	public static String getDomainOnly(String domainUser) {
	    int ix = -1;
	    if ((ix = domainUser.indexOf('\\')) >= 1) {
		// DOMAIN\USER - return the prefix part
		return domainUser.substring(0, ix);
	    }
	    else if ((ix = domainUser.indexOf('@')) >= 0 &&
		     ix + 1 < domainUser.length()) {
		// USER@DOMAIN - return the suffix part
		return domainUser.substring(ix + 1);
	    }
	    return null;
	}


	/**
	 * Helper method for determining if a string appears to be an IPv4 address.
	 *
	 * @param input The string to check.
	 * @return Whether the string appeared to be an IPv4 address.
	 */
	public static boolean isIPv4Address(final String input) {
	    return addressValidator.isValidInet4Address(input);
	}


	/**
	 * Helper method for determining if something is a valid IPv6 address.
	 *
	 * @param address The address.
	 * @return Whether or not the address is valid according to IPv6 rules.
	 */
	public static boolean isIPv6Address(final String address){
	    return addressValidator.isValidInet6Address(address);
	}


}
