/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014,2016-2018,2020 Roger L. Whitcomb.
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
 * Class / reflection-related utility methods.
 *
 *  History:
 *	30-Aug-2012 (rlwhitcomb)
 *	    Created.
 *	04-Sep-2012 (rlwhitcomb)
 *	    Do a better job of coercing values to the field types.
 *	04-Sep-2012 (rlwhitcomb)
 *	    Allow single values to pass as 1-length arrays.
 *	05-Sep-2012 (rlwhitcomb)
 *	    Add double value and enum support.
 *	26-Sep-2013 (rlwhitcomb)
 *	    Add EnumSet support (for ExtendOptions at least).
 *	28-Oct-2013 (rlwhitcomb)
 *	    Add "long" support to "createAndSetValues"; add support
 *	    for setting int and long values via hex strings.
 *	07-May-2014 (rlwhitcomb)
 *	    Add a convenience method to get stack elements (in order
 *	    to pinpoint the callers of functions for identification).
 *	08-Sep-2014 (rlwhitcomb)
 *	    Add methods to initialize class loader strategy and to get
 *	    a suitable class loader for use.  For now this uses TCCL
 *	    but could be changed if need be.  This gives us a single
 *	    place to implement a different strategy if needed.
 *	30-Sep-2014 (rlwhitcomb)
 *	    Add utility methods to convert objects and longs to byte arrays
 *	    (using serialization).
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings found by Java 8.
 *	17-Feb-2016 (rlwhitcomb)
 *	    Add a new method to log the stack trace of the method's caller.
 *	18-Mar-2016 (rlwhitcomb)
 *	    Two new utility methods to throw an IllegalStateException if the input
 *	    value is true or set (meaning not the default value).  Used for the data
 *	    transfer functions of the various utility options classes.
 *	    Use a different method to match option keys to field names so that we can
 *	    do a case-insensitive match.
 *	06-Mar-2017 (rlwhitcomb)
 *	    Always return some kind of string from "getCallingMethod" ("<unknown caller>"
 *	    if not known).
 *	18-Apr-2018 (rlwhitcomb)
 *	    Add method to compare objects with null checking.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	14-Apr-2020 (rlwhitcomb)
 *	    Rework code to avoid deprecated "Class.newInstance" method.
 *	15-Apr-2020 (rlwhitcomb)
 *	    New method to turn a class into a resource path.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 */

package info.rlwhitcomb.util;

import java.text.*;
import java.lang.reflect.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.regex.*;
import info.rlwhitcomb.annotations.*;


/**
 * A collection of static methods mostly to deal with issues around Java classes (but actually
 * just things having to do with Java reflection).
 */
public class ClassUtil
{
	/** Pattern used to parse out the generic type class name from a field. */
	private static Pattern GENERIC_PATTERN = Pattern.compile("[^<]+<([\\.\\w\\$]+)>.*");

	/** A string to describe the situation where caller information is not available. */
	public static final String UNKNOWN_CALLER = "<unknown caller>";

	private static Field findField(Field[] fields, String fieldName)
		throws NoSuchFieldException
	{
	    for (Field field : fields) {
		if (field.getName().equalsIgnoreCase(fieldName))
		    return field;
	    }
	    throw new NoSuchFieldException(fieldName);
	}

	private static Method findMethod(Method[] methods, String methodName)
		throws NoSuchMethodException
	{
	    for (Method method : methods) {
		if (method.getName().equalsIgnoreCase(methodName))
		    return method;
	    }
	    throw new NoSuchMethodException(methodName);
	}

	private static Method findMethod(Method[] methods, String methodName, Class<?> paramType)
		throws NoSuchMethodException
	{
	    for (Method method : methods) {
		if (method.getName().equalsIgnoreCase(methodName)) {
		    Class<?>[] parameterTypes = method.getParameterTypes();
		    if (parameterTypes.length == 1 && parameterTypes[0] == paramType)
			return method;
		}
	    }
	    throw new NoSuchMethodException(methodName);
	}

	/**
	 * Lookup the given property name in the given class
	 * and see if it exists and has the {@link Scriptable} annotation.
	 *
	 * @param clazz	The class to investigate.
	 * @param name	Name of the property to find (if possible) in that class.
	 * @return	Whether or not the property exists there, and has the
	 *		{@link Scriptable} annotation.
	 */
	public static boolean findScriptable(Class<?> clazz, String name) {
	    Scriptable annotation = null;
	    try {
		Field[] fields = clazz.getDeclaredFields();
		Field field = findField(fields, name);
		annotation = field.getAnnotation(Scriptable.class);
	    }
	    catch (NoSuchFieldException nsfe) {
		try {
		    Method[] methods = clazz.getDeclaredMethods();
		    String getMethodName = String.format("get%1$c%2$s",
			Character.toUpperCase(name.charAt(0)),
			name.substring(1));
		    Method method = findMethod(methods, getMethodName);
		    annotation = method.getAnnotation(Scriptable.class);
		}
		catch (NoSuchMethodException nsme) {
		}
	    }
	    return (annotation != null);
	}


	/**
	 * Construct a given object (from its class) and set the key,value
	 * pairs into the object.
	 * <p> Throws a whole host of possible exceptions if the values
	 * don't have the right type as the fields.
	 *
	 * @param	clazz	The object class we want to construct.
	 * @param	keys	The list of property names we are going to set.
	 * @param	values	The object values to set these properties to.
	 * @return		The constructed object with all the given
	 *			properties set to their values.
	 * @throws	Throwable for whatever might go wrong.
	 */
	public static Object createAndSetValues(Class<?> clazz, List<String> keys, List<Object> values)
		throws Throwable
	{
	    Object obj = null;
	    try {
		Constructor<?> constructor = clazz.getConstructor();
		obj = constructor.newInstance();
		if (obj != null) {
		    Field[] fields = clazz.getDeclaredFields();
		    Method[] methods = clazz.getDeclaredMethods();

		    for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = values.get(i);
			try {
			    Field field = findField(fields, key);
			    Class<?> fieldClass = field.getType();
			    Class<?> valueClass = value.getClass();
			    field.setAccessible(true);
			    if (fieldClass == String.class) {
				field.set(obj, value == null ? null : value.toString());
			    }
			    else if (fieldClass == boolean.class) {
				if (value instanceof Boolean)
				    field.setBoolean(obj, ((Boolean)value).booleanValue());
				else if (value instanceof Number)
				    field.setBoolean(obj, ((Number)value).intValue() != 0);
				else if (value instanceof String)
				    field.setBoolean(obj, Boolean.valueOf((String)value));
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass == int.class) {
				if (value instanceof Number)
				    field.setInt(obj, ((Number)value).intValue());
				else if (value instanceof String)
				    try {
					field.setInt(obj, Integer.valueOf((String)value));
				    }
				    catch (NumberFormatException nfe) {
					field.setInt(obj, Integer.valueOf((String)value, 16));
				    }
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass == long.class) {
				if (value instanceof Number)
				    field.setLong(obj, ((Number)value).longValue());
				else if (value instanceof String)
				    try {
					field.setLong(obj, Long.valueOf((String)value));
				    }
				    catch (NumberFormatException nfe) {
					field.setLong(obj, Long.valueOf((String)value, 16));
				    }
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass == double.class) {
				if (value instanceof Number)
				    field.setDouble(obj, ((Number)value).doubleValue());
				else if (value instanceof String)
				    field.setDouble(obj, Double.valueOf((String)value));
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass.isEnum()) {
				if (value instanceof String) {
				    Method valueMethod = fieldClass.getDeclaredMethod("valueOf", String.class);
				    field.set(obj, valueMethod.invoke(null, ((String)value).toUpperCase()));
				}
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass.isArray()) {
				if (valueClass.isArray())
				    field.set(obj, value);
				else {
				    // Allow single values of the right type to pass as
				    // 1-length arrays
				    Class<?> elementType = fieldClass.getComponentType();
				    if (elementType == String.class) {
					String[] array = new String[1];
					array[0] = value == null ? null : value.toString();
					field.set(obj, array);
				    }
				    // TODO: more types here (we don't need them yet)?
				}
			    }
			    else if (fieldClass.isAssignableFrom(EnumSet.class)) {
				Matcher m = GENERIC_PATTERN.matcher(field.toGenericString());
				Class<?> enumClass = null;
				if (m.matches()) {
				    enumClass = Class.forName(m.group(1));
				}
				// TODO: else what??  should be impossible
				// Create a list of all the enum values specified by invoking the 'valueOf'
				// method on each string
				List<Object> newList = new ArrayList<Object>();
				Method valueMethod = enumClass.getDeclaredMethod("valueOf", String.class);
				if (valueClass.isArray()) {
				    Class<?> elementType = valueClass.getComponentType();
				    if (elementType == String.class) {
					int len = Array.getLength(value);
					for (int j = 0; j < len; j++) {
					    Object enumValue =
						valueMethod.invoke(null, ((String)(Array.get(value, j))).toUpperCase());
					    newList.add(enumValue);
					}
				    }
				    // else what to do??
				}
				else if (valueClass == String.class) {
				    // string value should be [a, b, c]
				    String[] setValues = CharUtil.getArrayFromSetString((String)value);
				    for (String oneValue : setValues) {
					Object enumValue = valueMethod.invoke(null, oneValue.toUpperCase());
					newList.add(enumValue);
				    }
				}
				// Now create the new set by first invoking the "noneOf" method to create
				// an empty set, then adding all the list values from above using the
				// "addAll(Collection)" method
				EnumSet<? extends Enum<?>> currentSet = null;
				Method[] fieldMethods = fieldClass.getMethods();
				Method method = findMethod(fieldMethods, "noneOf");
				currentSet = (EnumSet<? extends Enum<?>>)method.invoke(null, enumClass);
				method = findMethod(fieldMethods, "addAll");
				method.invoke(currentSet, newList);
				field.set(obj, currentSet);
			    }
			    else {
				field.set(obj, value);
			    }
			}
			catch (NoSuchFieldException nsfe) {
			    try {
				String setMethodName = String.format("set%1$c%2$s",
					Character.toUpperCase(key.charAt(0)),
					key.substring(1));
				Method method = findMethod(methods, setMethodName, value.getClass());
				method.invoke(obj, value);
			    }
			    catch (NoSuchMethodException nsme) { }
			    catch (InvocationTargetException ite) {
				throw ite.getTargetException();
			    }
			}
		    }
		}
	    }
	    catch (NoSuchMethodException nsme) { }
	    catch (InstantiationException ie) { }
	    catch (IllegalAccessException iae) { }
	    return obj;
	}


	/**
	 * Return the stack element of the given level above us.
	 * Used to identify the calling method of the caller of this method.
	 * @param	level	0 = the caller of this method, 1 = its caller
	 * @return	The {@link #UNKNOWN_CALLER} string if the level is
	 *		out of range or a formatted string that describes the
	 *		caller at the given level.
	 */
	public static String getCallingMethod(int level) {
	    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
	    if (elements == null || level + 2 >= elements.length)
		return UNKNOWN_CALLER;
	    return elements[level + 2].toString();
	}


	/**
	 * Log the stack trace of the caller of this method, using the {@link Logging#DEBUG}
	 * level.
	 * @param	logger	The logging instance to use for displaying the stack trace.
	 * @param	methodName	Optional identification of the method that is calling us,
	 *				which if supplied starts logging the call stack up one
	 *				further level (3 instead of 2).
	 */
	public static void logCallers(Logging logger, String methodName) {
	    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
	    int startLevel = CharUtil.isNullOrEmpty(methodName) ? 2 : 3;
	    if (elements == null || elements.length < startLevel)
		return;
	    logger.debug(startLevel == 2 ? "Stack Trace:" : "Callers of " + methodName + ":");
	    for (int level = startLevel; level < elements.length; level++) {
		logger.debug("\t%1$s", elements[level].toString());
	    }
	}


	/**
	 * Initialize the class-loading strategy.
	 */
	public static void initClassLoaderStrategy() {
	}


	/**
	 * @return An appropriate {@link ClassLoader} to use (currently
	 *	the TCCL).
	 */
	public static ClassLoader getClassLoader() {
	    return Thread.currentThread().getContextClassLoader();
	}


	/**
	 * Convert a serializable object to a byte array.
	 *
	 * @param object	Any object that implements the {@link Serializable} interface.
	 * @return		The serialized bytes of that object, or {@code null} if there
	 *			was a problem (such as, the object isn't serializable).
	 */
	public static byte[] toByteArray(Object object) {
	    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 ObjectOutputStream oos = new ObjectOutputStream(bos))
	    {
		oos.writeObject(object);
		return bos.toByteArray();
	    }
	    catch (IOException ioe) {
		return null;
	    }
	}


	/**
	 * Convert a long value to a byte array, according to the contract of
	 * {@link DataOutputStream#writeLong}.
	 *
	 * @param value	The value to convert.
	 * @return	The bytes of the value (but could be {@code null}
	 *		if something weird happened).
	 */
	public static byte[] toByteArray(long value) {
	    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 DataOutputStream os = new DataOutputStream(bos))
	    {
		os.writeLong(value);
		return bos.toByteArray();
	    }
	    catch (IOException ioe) {
		return null;
	    }
	}


	/**
	 * Check the given option value and throw an exception with
	 * the given message if the value is {@code true} (meaning
	 * not the default value).
	 *
	 * @param value	The option value to test.
	 * @param field	The field name to put into the exception message.
	 * @throws IllegalStateException if the option value is {@code true},
	 * otherwise just return.
	 */
	public static void checkOptionTrue(boolean value, String field) {
	    if (value) {
		throw new IllegalStateException(Intl.formatString("util#class.optionFieldSet", field));
	    }
	}


	/**
	 * Check the given option value and throw an exception with
	 * the given message if the value is not {@code null} (meaning
	 * not the default value), or if it is a {@code String} and is
	 * non-empty.
	 *
	 * @param value	The option value to test.
	 * @param field	The field name to put into the exception message.
	 * @throws IllegalStateException if the option value is not {@code null},
	 * or non empty if the value is a {@code String}.
	 */
	public static void checkOptionSet(Object value, String field) {
	    if (value != null || (value instanceof String && !((String)value).isEmpty())) {
		throw new IllegalStateException(Intl.formatString("util#class.optionFieldSet", field));
	    }
	}


	/**
	 * Check that the given objects are either both null or if they meet the <code>equals()</code>
	 * criteria.
	 *
	 * @param o1	The first object (or null).
	 * @param o2	The second object (or null).
	 * @return	If both are null or both non-null and "equal" then true, else false.
	 */
	public static boolean objectsEqual(Object o1, Object o2) {
	    if (o1 != null && o2 != null) {
		return o1.equals(o2);
	    }
	    else if (o1 == null && o2 == null) {
		return true;
	    }
	    return false;
	}


	/**
	 * Get the package name of the given object's class and turn it into
	 * a resource path (for the TCCL).
	 *
	 * @param obj	The object whose class is to be used as a reference for resources.
	 * @return	The package name with "." changed to "/", and with leading and
	 *		trailing "/" (suitable for adding specific resource names).
	 */
	public static String getResourcePath(Object obj) {
	    if (obj == null)
		throw new IllegalArgumentException(Intl.getString("util#class.nullObject"));
	    String path = obj.getClass().getPackage().getName().replace('.', '/');
	    return String.format("/%1$s/", path);
	}


}
