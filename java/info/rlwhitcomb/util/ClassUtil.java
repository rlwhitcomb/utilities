/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2012-2014,2016-2018,2020-2023 Roger L. Whitcomb.
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
 *	Class / reflection-related utility methods.
 *
 * History:
 *  30-Aug-12 rlw  ---	Created.
 *  04-Sep-12 rlw  ---	Do a better job of coercing values to the field types.
 *  04-Sep-12 rlw  ---	Allow single values to pass as 1-length arrays.
 *  05-Sep-12 rlw  ---	Add double value and enum support.
 *  26-Sep-13 rlw  ---	Add EnumSet support (for ExtendOptions at least).
 *  28-Oct-13 rlw  ---	Add "long" support to "createAndSetValues"; add support
 *			for setting int and long values via hex strings.
 *  07-May-14 rlw  ---	Add a convenience method to get stack elements (in order
 *			to pinpoint the callers of functions for identification).
 *  08-Sep-14 rlw  ---	Add methods to initialize class loader strategy and to get
 *			a suitable class loader for use.  For now this uses TCCL
 *			but could be changed if need be.  This gives us a single
 *			place to implement a different strategy if needed.
 *  30-Sep-14 rlw  ---	Add utility methods to convert objects and longs to byte arrays
 *			(using serialization).
 *  07-Jan-16 rlw  ---	Fix Javadoc warnings found by Java 8.
 *  17-Feb-16 rlw  ---	Add a new method to log the stack trace of the method's caller.
 *  18-Mar-16 rlw  ---	Two new utility methods to throw an IllegalStateException if the input
 *			value is true or set (meaning not the default value).  Used for the data
 *			transfer functions of the various utility options classes.
 *			Use a different method to match option keys to field names so that we can
 *			do a case-insensitive match.
 *  06-Mar-17 rlw  ---	Always return some kind of string from "getCallingMethod" ("<unknown caller>"
 *			if not known).
 *  18-Apr-18 rlw  ---	Add method to compare objects with null checking.
 *  10-Mar-20 rlw  ---	Prepare for GitHub.
 *  14-Apr-20 rlw  ---	Rework code to avoid deprecated "Class.newInstance" method.
 *  15-Apr-20 rlw  ---	New method to turn a class into a resource path.
 *  21-Dec-20 rlw  ---	Update obsolete Javadoc constructs.
 *  27-Jan-21 rlw  ---	New method to get a directory name from a class (package).
 *  29-Jan-21 rlw  ---	Use new Intl Exception variants for convenience.
 *  07-Jul-21 rlw  ---	Make the class final and the constructor private.
 *  17-Nov-21 rlw  ---	Add "defaultToString". Make all parameters final.
 *  18-Dec-21 rlw #148:	Method to construct map from Scriptable fields. Cleanup.
 *  24-Jan-22 rlw #79:	New method to convert an object to a byte array.
 *  02-Feb-22 rlw #115:	In "getMapFromObject" for the general case put the String value,
 *			not the object itself.
 *		  #115:	Add methods into the Scriptable processing in "getMapFromObject".
 *  05-Feb-22 rlw #233:	Add "getField" method to support the "system value" idea in Calc.
 *			And remove "getMapFromObject" which is not being used now.
 *  16-Feb-22 rlw  ---	Add two flavors of "getResourceAsString" to load text from the .jar file.
 *  12-Apr-22 rlw #269:	Method to parse full module/class.name into parts as well as class
 *			to parse and hold the information.
 *  14-Apr-22 rlw #273:	Move math-related classes to "math" package.
 *  09-Jul-22 rlw #393:	Cleanup imports.
 *  24-Aug-22 rlw  ---	Handle null input to "defaultToString()".
 *  25-Aug-22 rlw  ---	Another tweak to "defaultToString()" for null input.
 *  29-Aug-22 rlw #453:	Add back "getMapFromObject" method.
 *  12-Oct-22 rlw #513:	Move Logging to a new package.
 *  09-Apr-23 rlw #601:	New method to determine if an object is an integer object.
 */
package info.rlwhitcomb.util;

import info.rlwhitcomb.annotations.*;
import info.rlwhitcomb.logging.Logging;
import info.rlwhitcomb.math.NumericUtil;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static info.rlwhitcomb.util.Constants.*;


/**
 * A collection of static methods mostly to deal with issues around Java classes (but actually
 * just things having to do with Java reflection).
 */
public final class ClassUtil
{
	/** Pattern used to parse out the generic type class name from a field. */
	private static Pattern GENERIC_PATTERN = Pattern.compile("[^<]+<([\\.\\w\\$]+)>.*");

	/** A string to describe the situation where caller information is not available. */
	public static final String UNKNOWN_CALLER = "<unknown caller>";


	/**
	 * Object to contain module and class names.
	 */
	public final static class ModuleClass
	{
		/** A pattern string to parse module / class names into parts. */
		private static final String PATH_SPLIT = "\\.";


		/**
		 * The parts of the module name, such as {@code "jdk", "jcmd"}, or an empty
		 * array if a module name is not given.
		 */
		public String[] moduleParts;
		/**
		 * The parts of the class name within the module, with the last
		 * element of the array being the simple name of the class, such as:
		 * {@code "sun", "tools", "jps", "Jps"}.
		 */
		public String[] classParts;


		/**
		 * Construct and fill with empty values.
		 */
		public ModuleClass() {
		    moduleParts = new String[0];
		    classParts = new String[0];
		}

		/**
		 * Construct and parse from an input string.
		 *
		 * @param input The input module / class name.
		 */
		public ModuleClass(final String input) {
		    String[] parts = input.split("/");

		    if (parts.length == 2) {
			moduleParts = parts[0].split(PATH_SPLIT);
			classParts = parts[1].split(PATH_SPLIT);
		    }
		    else {
			classParts = parts[0].split(PATH_SPLIT);
		    }
		}

		/**
		 * Get the class simple name (last part of the class name).
		 *
		 * @return The simple name of the class.
		 */
		public String getSimpleClassName() {
		    return classParts[classParts.length - 1];
		}

		/**
		 * Reconstruct the input string from the parsed parts.
		 *
		 * @return {@code module/class}.
		 */
		@Override
		public String toString() {
		    if (moduleParts.length == 0) {
			return String.join(".", classParts);
		    }
		    else {
			return String.join("/", String.join(".", moduleParts), String.join(".", classParts));
		    }
		}
	}


	/**
	 * Private constructor since this is a utility class.
	 */
	private ClassUtil() {
	}


	private static Field findField(final Field[] fields, final String fieldName)
		throws NoSuchFieldException
	{
	    for (Field field : fields) {
		if (field.getName().equalsIgnoreCase(fieldName))
		    return field;
	    }
	    throw new NoSuchFieldException(fieldName);
	}

	private static Method findMethod(final Method[] methods, final String methodName)
		throws NoSuchMethodException
	{
	    for (Method method : methods) {
		if (method.getName().equalsIgnoreCase(methodName))
		    return method;
	    }
	    throw new NoSuchMethodException(methodName);
	}

	private static Method findMethod(final Method[] methods, final String methodName, final Class<?> paramType)
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
	public static boolean findScriptable(final Class<?> clazz, final String name) {
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
	public static Object createAndSetValues(final Class<?> clazz, final List<String> keys, final List<Object> values)
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
				    field.setBoolean(obj, ((Boolean) value).booleanValue());
				else if (value instanceof Number)
				    field.setBoolean(obj, ((Number) value).intValue() != 0);
				else if (value instanceof String)
				    field.setBoolean(obj, Boolean.valueOf((String)value));
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass == int.class) {
				if (value instanceof Number)
				    field.setInt(obj, ((Number) value).intValue());
				else if (value instanceof String)
				    try {
					field.setInt(obj, Integer.valueOf((String) value));
				    }
				    catch (NumberFormatException nfe) {
					field.setInt(obj, Integer.valueOf((String) value, 16));
				    }
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass == long.class) {
				if (value instanceof Number)
				    field.setLong(obj, ((Number) value).longValue());
				else if (value instanceof String)
				    try {
					field.setLong(obj, Long.valueOf((String) value));
				    }
				    catch (NumberFormatException nfe) {
					field.setLong(obj, Long.valueOf((String) value, 16));
				    }
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass == double.class) {
				if (value instanceof Number)
				    field.setDouble(obj, ((Number) value).doubleValue());
				else if (value instanceof String)
				    field.setDouble(obj, Double.valueOf((String) value));
				else
				    field.set(obj, value);
			    }
			    else if (fieldClass.isEnum()) {
				if (value instanceof String) {
				    Method valueMethod = fieldClass.getDeclaredMethod("valueOf", String.class);
				    field.set(obj, valueMethod.invoke(null, ((String) value).toUpperCase()));
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
						valueMethod.invoke(null, ((String) (Array.get(value, j))).toUpperCase());
					    newList.add(enumValue);
					}
				    }
				    // else what to do??
				}
				else if (valueClass == String.class) {
				    // string value should be [a, b, c]
				    String[] setValues = CharUtil.getArrayFromSetString((String) value);
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
				currentSet = (EnumSet<? extends Enum<?>>) method.invoke(null, enumClass);
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
	    catch (NoSuchMethodException | InstantiationException | IllegalAccessException ex) { }
	    return obj;
	}


	/**
	 * Determine if the given object is an "integer" type, which includes the built-in
	 * integers, as well as the {@link BigInteger} type and {@link BigDecimal} values
	 * with no decimal part.
	 *
	 * @param obj The object to check.
	 * @return    Whether or not this object is an integer type.
	 */
	public static boolean isInteger(final Object obj) {
	    if (obj instanceof Number) {
		Class<?> clz = obj.getClass();
		if (clz == BigInteger.class)
		    return true;
		if (clz == Integer.class || clz == Integer.TYPE)
		    return true;
		if (clz == Long.class || clz == Long.TYPE)
		    return true;
		if (clz == Short.class || clz == Short.TYPE)
		    return true;
		if (clz == Byte.class || clz == Byte.TYPE)
		    return true;
		if (clz == BigDecimal.class) {
		    BigDecimal bd = ((BigDecimal) obj).stripTrailingZeros();
		    if (bd.scale() <= 0)
			return true;
		}
	    }

	    return false;
	}


	private static String getFieldFromMethodName(final String methodName) {
	    String fieldName;

	    if (methodName.startsWith("get")) {
		fieldName = methodName.substring(3);
	    }
	    else if (methodName.startsWith("is")) {
		fieldName = methodName.substring(2);
	    }
	    else {
		return null;
	    }

	    return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
	}


	/**
	 * Tuple object to sort {@link Scriptable} fields and methods by their declared order.
	 */
	private static class ScriptableObject implements Comparable<ScriptableObject>
	{
		int order;
		Object fieldOrMethod;
		String name;

		ScriptableObject(int ord, Object fOrM, String nm) {
		    order = ord;
		    fieldOrMethod = fOrM;
		    name = nm;
		}

		@Override
		public int compareTo(ScriptableObject o) {
		    if (order >= 0 && o.order >= 0) {
			return Integer.signum(order - o.order);
		    }

		    return name.compareTo(o.name);
		}
	}


	/**
	 * Traverse the given object, finding the {@link Scriptable} elements, creating a map
	 * of the name/value pairs.
	 *
	 * @param	obj	The input object to be mapped.
	 * @return	A map of the scriptable fields and their values.
	 */
	public static Map<String, Object> getMapFromObject(final Object obj) {
	    Map<String, Object> map = new LinkedHashMap<>();
	    Field[] fields = obj.getClass().getDeclaredFields();
	    Method[] methods = obj.getClass().getDeclaredMethods();
	    Scriptable annotation;
	    int order;
	    String key;
	    Class<?> cls;

	    List<ScriptableObject> objs = new ArrayList<>();

	    for (Field f : fields) {
		annotation = f.getAnnotation(Scriptable.class);
		if (annotation != null) {
		    order = annotation.order();
		    key = f.getName();
		    objs.add(new ScriptableObject(order, f, key));
		}
	    }

	    for (Method m : methods) {
		annotation = m.getAnnotation(Scriptable.class);
		if (annotation != null) {
		    key = getFieldFromMethodName(m.getName());
		    if (key == null)
			continue;

		    order = annotation.order();
		    objs.add(new ScriptableObject(order, m, key));
		}
	    }

	    Collections.sort(objs);

	    for (ScriptableObject so : objs) {
		key = so.name;

		if (so.fieldOrMethod instanceof Field) {
		    Field f = (Field) so.fieldOrMethod;
		    cls = f.getType();

		    try {
			f.setAccessible(true);
			if (cls == Long.class || cls == Long.TYPE) {
			    map.put(key, f.getLong(obj));
			}
			else if (cls == Integer.class || cls == Integer.TYPE) {
			    map.put(key, f.getInt(obj));
			}
			else if (cls == Short.class || cls == Short.TYPE) {
			    map.put(key, f.getShort(obj));
			}
			else if (cls == Byte.class || cls == Byte.TYPE) {
			    map.put(key, f.getByte(obj));
			}
			else if (cls == Character.class || cls == Character.TYPE) {
			    map.put(key, f.getChar(obj));
			}
			else if (cls == Boolean.class || cls == Boolean.TYPE) {
			    map.put(key, f.getBoolean(obj));
			}
			else {
			    map.put(key, f.get(obj));
			}
		    }
		    catch (IllegalAccessException ex) {
			map.put(key, null);
		    }
		}
		else {
		    Method m = (Method) so.fieldOrMethod;

		    try {
			Object value = m.invoke(obj);
			map.put(key, value);
		    }
		    catch (IllegalAccessException | InvocationTargetException ex) {
			map.put(key, null);
		    }
		}
	    }

	    return map;
	}


	/**
	 * Do the nasty bits, including trapping exceptions that would never happen,
	 * around getting a field descriptor given the object it lives in.
	 * <p> The returned field is guaranteed to be accessible when used.
	 *
	 * @param obj	The object containing the field.
	 * @param name	Name of the field to query.
	 * @return	A field descriptor for it, or {@code null} if it wasn't found
	 *		or some other error occurred (security violation).
	 */
	public static Field getField(final Object obj, final String name) {
	    try {
		Class<?> cls = obj.getClass();
		Field f = cls.getDeclaredField(name);
		f.setAccessible(true);
		return f;
	    }
	    catch (NoSuchFieldException | SecurityException ex) {
		return null;
	    }
	}


	/**
	 * Return the stack element of the given level above us.
	 * Used to identify the calling method of the caller of this method.
	 * @param	level	0 = the caller of this method, 1 = its caller
	 * @return	The {@link #UNKNOWN_CALLER} string if the level is
	 *		out of range or a formatted string that describes the
	 *		caller at the given level.
	 */
	public static String getCallingMethod(final int level) {
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
	public static void logCallers(final Logging logger, final String methodName) {
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
	public static byte[] toByteArray(final Object object) {
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
	public static byte[] toByteArray(final long value) {
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
	public static void checkOptionTrue(final boolean value, final String field) {
	    if (value) {
		throw new Intl.IllegalStateException("util#class.optionFieldSet", field);
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
	public static void checkOptionSet(final Object value, final String field) {
	    if (value != null || (value instanceof String && !((String) value).isEmpty())) {
		throw new Intl.IllegalStateException("util#class.optionFieldSet", field);
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
	public static boolean objectsEqual(final Object o1, final Object o2) {
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
	public static String getResourcePath(final Object obj) {
	    if (obj == null)
		throw new Intl.IllegalArgumentException("util#class.nullObject");
	    String path = getClassDirectory(obj.getClass());
	    return String.format("/%1$s/", path);
	}


	/**
	 * Get a relative directory name given a class, which is in a package.
	 *
	 * @param cls	The class we're dealing with.
	 * @return	The class' package expressed as a directory entry ("/" separators).
	 */
	public static String getClassDirectory(final Class<?> cls) {
	    Package pkg = cls.getPackage();
	    return pkg.getName().replace('.', '/');
	}


	/**
	 * Get the default value of {@link Object#toString} for the given object.
	 *
	 * @param obj	The object in question, which can be {@code null}.
	 * @return	What {@link Object#toString} would return if there were no intervening
	 *		superclass implementations of the <code>toString()</code> method, or
	 *		"null@0" for a null input.
	 */
	public static String defaultToString(final Object obj) {
	    return String.format("%1$s@%2$s",
		obj == null ? "null" : obj.getClass().getSimpleName(),
		Integer.toHexString(System.identityHashCode(obj)));
	}


	/**
	 * Get the bytes of the given object.
	 *
	 * @param obj Any arbitrary object.
	 * @return    The bytes of the object's value, as best we can determine.
	 */
	public static byte[] getBytes(final Object obj) {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();	// default size
	    try {
		DataOutputStream dos = new DataOutputStream(baos);
		NumericUtil.writeRawBinaryValue(obj, dos, Charset.defaultCharset(),
			NumericUtil.ByteOrder.LSB, NumericUtil.StringLength.EOS, 0);
	    }
	    catch (IOException ioe) {
		;
	    }
	    // will be empty in case of I/O error
	    return baos.toByteArray();
	}


	/**
	 * Load a resource from this classloader and convert to a String (UTF-8 charset).
	 *
	 * @param resourcePath Should be an absolute path (beginning with "/") of the resource
	 *                     within our .jar file or CLASSPATH.
	 * @return The contents of the resource converting the bytes to a String using the UTF-8 charset.
	 */
	public static String getResourceAsString(final String resourcePath) {
	    return getResourceAsString(resourcePath, Constants.UTF_8_CHARSET);
	}


	/**
	 * Load a resource from this classloader and convert to a String using the given charset.
	 *
	 * @param resourcePath Should be an absolute path (beginning with "/") of the resource
	 *                     within our .jar file or CLASSPATH.
	 * @param charset      The character set to use to convert the bytes to a String.
	 * @return The contents of the resource converting the bytes to a String using the given charset.
	 */
	public static String getResourceAsString(final String resourcePath, final Charset charset) {
	    StringBuilder builder = new StringBuilder(CHAR_BUFFER_SIZE);
	    char[] chars = new char[CHAR_BUFFER_SIZE];

	    try (InputStreamReader isr = new InputStreamReader(ClassUtil.class.getResourceAsStream(resourcePath), charset)) {
		int read = -1;
		while ((read = isr.read(chars)) > 0) {
		    builder.append(chars, 0, read);
		}
	    }
	    catch (IOException ioe) {
		// Ignore for now
	    }

	    return builder.toString();
	}


	/**
	 * Parse a module / class name into parts.
	 *
	 * @param input The module / class name string.
	 * @return The parts of the input string, separated.
	 */
	public static ModuleClass parseModuleClassName(final String input) {
	    return new ModuleClass(input);
	}

}
