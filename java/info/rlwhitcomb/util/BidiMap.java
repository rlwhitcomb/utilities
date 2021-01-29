/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013-2014,2016,2020-2021 Roger L. Whitcomb.
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
 *	Bidirectional map class which allows efficient lookup of entries
 *	in either direction (key->value or value->key).
 *
 *  History:
 *	23-Jul-2013 (rlwhitcomb)
 *	    Created.
 *	15-Mar-2014 (rlwhitcomb)
 *	    Implement "remove" because we need to use this is in a more
 *	    dynamic situation.  Use absolute value as index.
 *	20-Jun-2014 (rlwhitcomb)
 *	    Update Javadoc to reflect the ability to remove elements.
 *	14-Aug-2014 (rlwhitcomb)
 *	    Cleanup "lint" warnings.
 *	06-Nov-2014 (rlwhitcomb)
 *	    Moved error messages to resource bundle.
 *	07-Jan-2016 (rlwhitcomb)
 *	    Fix Javadoc warnings from Java 8.
 *	10-Mar-2020 (rlwhitcomb)
 *	    Prepare for GitHub.
 *	21-Dec-2020 (rlwhitcomb)
 *	    Update obsolete Javadoc constructs.
 *	29-Jan-2021 (rlwhitcomb)
 *	    Use new Intl exception variants for convenience.
 */
package info.rlwhitcomb.util;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Bidirectional map class that allows lookup of either key or value with
 * equal ease.  Does not allow {@code null} keys or {@code null} values.
 * <p> Enforces the restriction that all keys and all values must be unique.
 * <p> Currently the map does not allow replacement of values that already
 * have a key, so it is not quite suitable for any generic map usage.
 * <p> Testing with the standard Java implementations of "hashCode()" for
 * Strings, Integers and Longs shows about 50% utilization of the main tables
 * for 11,400 random values (capacity of 16,384, which is just below the default
 * 70% load factor).
 */
public class BidiMap<K, V> implements Map<K, V>
{
	/**
	 * One entry in the map (linked by both the {@link #keyTable} and
	 * the {@link #valueTable}, so it must have "next" pointers for each).
	 * <p>Both {@code key} and {@code value} are {@code final} because
	 * the map is unmodifiable (for now), and thus only suitable for
	 * static data that is initialized one time.
	 */
	public static class Entry<K, V> implements Map.Entry<K, V>
	{
		final K key;
		final V value;
		Entry<K, V> nextKey;
		Entry<K, V> nextValue;
		final int keyHash;
		final int valueHash;

		public Entry(K key, V value) {
		    if (key == null)
			throw new Intl.IllegalArgumentException("util#bidi.keyNotNull");
		    if (value == null)
			throw new Intl.IllegalArgumentException("util#bidi.valueNotNull");
		    this.key = key;
		    this.value = value;
		    nextKey = null;
		    nextValue = null;
		    keyHash = key.hashCode();
		    valueHash = value.hashCode();
		}

		@Override
		public final K getKey() {
		    return this.key;
		}

		@Override
		public final V getValue() {
		    return this.value;
		}

		@Override
		public V setValue(V value) {
		    throw new Intl.UnsupportedOperationException("util#bidi.entryCannotChange");
		}

		@Override
		public int hashCode() {
		    return key.hashCode() ^ value.hashCode();
		}

		@Override
		public boolean equals(Object o) {
		    if (!(o instanceof Map.Entry))
			return false;
		    @SuppressWarnings({"unchecked"})
		    Map.Entry<K,V> e = (Map.Entry<K,V>)o;
		    K k1 = getKey();
		    K k2 = e.getKey();
		    if (k1 == k2 || (k1 != null && k1.equals(k2))) {
			V v1 = getValue();
			V v2 = e.getValue();
			if (v1 == v2 || (v1 != null && v1.equals(v2)))
			    return true;
		    }
		    return false;
		}

	}

	private static final int DEFAULT_CAPACITY = 16;
	/** Suggested by http://eternallyconfuzzled.com/tuts/datastructures/jsw_tut_hashtable.aspx */
	private static final float DEFAULT_LOAD_FACTOR = 0.70f;

	/** The percentagle of capacity that has to be reached before we will
	 * automatically resize (constant after construction).
	 */
	private float loadFactor;
	/** The actual number of entries that we will accept before resizing.
	 * <p> Computed from the capacity and the load factor.
	 */
	private int threshold;

	private transient Entry<K, V>[] keyTable;
	private transient Entry<K, V>[] valueTable;

	private transient Set<K> keySet = null;
	private transient Set<Map.Entry<K, V>> entrySet = null;
	private transient Collection<V> values = null;

	/** Current number of entries in this map. */
	private transient int size;


	/**
	 * Construct a new bidirectional map using the default
	 * capacity and load factor values.
	 */
	public BidiMap() {
	    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Construct a new bidirectional map using the given
	 * capacity and default load factor value.
	 *
	 * @param initialCapacity	A non-default value for the initial
	 *				map capacity.
	 */
	public BidiMap(int initialCapacity) {
	    this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Construct a new bidirectional map using the default
	 * capacity and the given load factor.
	 *
	 * @param loadFactor	Something other than the default load factor.
	 */
	public BidiMap(float loadFactor) {
	    this(DEFAULT_CAPACITY, loadFactor);
	}

	/**
	 * Construct a new bidirectional map using the given values
	 * for capacity and load factor.
	 *
	 * @param initialCapacity	A non-default capacity for this map.
	 * @param loadFactor		A non-default load factor.
	 */
	public BidiMap(int initialCapacity, float loadFactor) {
	    allocate(initialCapacity, loadFactor);
	}

	@SuppressWarnings({"unchecked"})
	private void allocate(int newCapacity, float loadFactor) {
	    if (newCapacity < 0)
		throw new Intl.IllegalArgumentException("util#bidi.illegalCapacity", newCapacity);
	    if (loadFactor <= 0 || Float.isNaN(loadFactor))
		throw new Intl.IllegalArgumentException("util#bidi.illegalLoadFactor", loadFactor);

	    // Find the next highest power of two for the real capacity
	    int oneBit = Integer.highestOneBit(newCapacity);
	    int capacity = (oneBit == newCapacity && oneBit > 1) ? oneBit : (oneBit << 1);

	    this.loadFactor = loadFactor;
	    this.threshold = (int)(capacity * loadFactor);

	    keyTable = (Entry<K, V>[])new Entry<?, ?>[capacity];
	    valueTable = (Entry<K, V>[])new Entry<?, ?>[capacity];
	}


	@Override
	public V put(K key, V value) {
	    Entry<K, V> entry = new Entry<K, V>(key, value);
	    int keyIndex = Math.abs(entry.keyHash % (keyTable.length - 1));
	    int valueIndex = Math.abs(entry.valueHash % (valueTable.length - 1));

	    for (Entry<K, V> e = keyTable[keyIndex]; e != null; e = e.nextKey) {
		if (e.key.equals(key))
		    throw new Intl.IllegalArgumentException("util#bidi.keyNotUnique", key);
	    }
	    entry.nextKey = keyTable[keyIndex];
	    keyTable[keyIndex] = entry;

	    for (Entry<K, V> e = valueTable[valueIndex]; e != null; e = e.nextValue) {
		if (e.value.equals(value))
		    throw new Intl.IllegalArgumentException("util#bidi.valueNotUnique", value);
	    }
	    entry.nextValue = valueTable[valueIndex];
	    valueTable[valueIndex] = entry;

	    if (size++ > threshold) {
		resize(keyTable.length * 2);
	    }
	    return null;
	}

	@Override
	public int size() {
	    return size;
	}

	@Override
	public boolean isEmpty() {
	    return (size == 0);
	}

	@Override
	public void clear() {
	    allocate(keyTable.length, loadFactor);
	    size = 0;
	}

	private void resize(int newCapacity) {
	    Entry<K, V>[] oldKeyTable = keyTable;

	    allocate(newCapacity, loadFactor);
	    size = 0;

	    // Traverse only the old key table since all entries will
	    // be in both arrays
	    for (int i = 0; i < oldKeyTable.length; i++) {
		for (Entry<K, V> e = oldKeyTable[i]; e != null; e = e.nextKey) {
		    put(e.key, e.value);
		}
	    }
	}

	private Entry<K, V> findKey(Object key) {
	    int keyHash = key.hashCode();
	    int keyIndex = Math.abs(keyHash % (keyTable.length - 1));
	    for (Entry<K, V> e = keyTable[keyIndex]; e != null; e = e.nextKey) {
		if (e.key.equals(key))
		    return e;
	    }
	    return null;
	}

	private Entry<K, V> findValue(Object value) {
	    int valueHash = value.hashCode();
	    int valueIndex = Math.abs(valueHash % (valueTable.length - 1));
	    for (Entry<K, V> e = valueTable[valueIndex]; e != null; e = e.nextValue) {
		if (e.value.equals(value))
		    return e;
	    }
	    return null;
	}

	/**
	 * @return The value associated with the given key (if any).
	 * @param key The key for which we want the value.
	 */
	@Override
	public V get(Object key) {
	    Entry<K, V> entry = findKey(key);
	    return entry == null ? null : entry.value;
	}

	/**
	 * @return The key that maps to the given value (if any).
	 * @param value The value to find the key for.
	 */
	public K getKey(Object value) {
	    Entry<K, V> entry = findValue(value);
	    return entry ==  null ? null : entry.key;
	}

	@Override
	public boolean containsKey(Object key) {
	    return findKey(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
	    return findValue(value) != null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
	    // TODO: expand size beforehand to avoid multiple resizes?
	    for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
		Map.Entry<? extends K, ? extends V> e = i.next();
		put(e.getKey(), e.getValue());
	    }
	}

	@Override
	public V remove(Object key) {
	    // Search for the key in the key hash table
	    int keyHash = key.hashCode();
	    int keyIndex = Math.abs(keyHash % (keyTable.length - 1));
	    Entry<K, V> prev = null;
	    for (Entry<K, V> e = keyTable[keyIndex]; e != null; prev = e, e = e.nextKey) {
		if (e.key.equals(key)) {
		    if (prev == null) {
			keyTable[keyIndex] = e.nextKey;
		    }
		    else {
			prev.nextKey = e.nextKey;
		    }
		    V value = e.value;
		    // Now search for the value in the value hash table
		    int valueHash = value.hashCode();
		    int valueIndex = Math.abs(valueHash % (valueTable.length - 1));
		    Entry<K, V> prev2 = null;
		    for (Entry<K, V> e2 = valueTable[valueIndex]; e2 != null; prev2 = e2, e2 = e2.nextValue) {
			if (e2.value.equals(value)) {
			    if (prev2 == null) {
				valueTable[valueIndex] = e2.nextValue;
			    }
			    else {
				prev2.nextValue = e2.nextValue;
			    }
			    break;
			}
		    }
		    size--;
		    return value;
		}
	    }
	    return null;
	}

	public K removeValue(Object value) {
	    int valueHash = value.hashCode();
	    int valueIndex = Math.abs(valueHash % (valueTable.length - 1));
	    Entry<K, V> prev = null;
	    for (Entry<K, V> e = valueTable[valueIndex]; e != null; prev = e, e = e.nextValue) {
		if (e.value.equals(value)) {
		    if (prev == null) {
			valueTable[valueIndex] = e.nextValue;
		    }
		    else {
			prev.nextValue = e.nextValue;
		    }
		    K key = e.key;
		    // Now search for the key in the key hash table
		    int keyHash = key.hashCode();
		    int keyIndex = Math.abs(keyHash % (keyTable.length - 1));
		    Entry<K, V> prev2 = null;
		    for (Entry<K, V> e2 = keyTable[keyIndex]; e2 != null; prev2 = e2, e2 = e2.nextKey) {
			if (e2.key.equals(key)) {
			    if (prev2 == null) {
				keyTable[keyIndex] = e2.nextKey;
			    }
			    else {
				prev2.nextKey = e2.nextKey;
			    }
			    break;
			}
		    }
		    size--;
		    return key;
		}
	    }
	    return null;
	}


	/**
	 * A general purpose iterator that can be used to iterate over
	 * keys, values, or map entries.
	 */
	private abstract class BidiIterator<E> implements Iterator<E> {
	    private boolean onKey;
	    private Entry<K, V>[] table;
	    private Entry<K, V> next;
	    private int index;
	    private Entry<K, V> current;

	    /**
	     * Construct the iterator, given one of the two tables (keys or
	     * values) plus a flag to say which "next" pointer to follow
	     * during the iteration.
	     *
	     * @param table	The entry table to iterate over.
	     * @param onKey	Whether this is the key table or the value table.
	     */
	    public BidiIterator(Entry<K, V>[] table, boolean onKey) {
		this.table = table;
		this.onKey = onKey;

		// Move to the first non-null table entry (if any)
		if (size > 0) {
		    while (index < table.length &&
			(next = table[index++]) == null)
			;
		}
	    }

	    public final boolean hasNext() {
		return next != null;
	    }

	    public final Entry<K,V> nextEntry() {
		Entry<K, V> e = next;
		if (e == null)
		    throw new NoSuchElementException();

		if ((next = (onKey ? e.nextKey : e.nextValue)) == null) {
		    while (index < table.length &&
			(next = table[index++]) == null)
			;
		}
		current = e;
		return e;
	    }

	    public void remove() {
		if (current == null)
		    throw new Intl.IllegalStateException("util#bidi.iteratorNotInit");
		throw new Intl.UnsupportedOperationException("util#bidi.removeNotSupported");
	    }
	}

	/**
	 * An iterator over the entries in the map.
	 */
	public class EntryIterator extends BidiIterator<Map.Entry<K,V>>
	{
		public EntryIterator() {
		    super(keyTable, true);
		}
		@Override
		public Map.Entry<K,V> next() {
		    return nextEntry();
		}
	}

	/**
	 * An iterator over the keys in the map.
	 */
	public class KeyIterator extends BidiIterator<K>
	{
		public KeyIterator() {
		    super(keyTable, true);
		}
		@Override
		public K next() {
		    return nextEntry().getKey();
		}
	}

	/**
	 * An iterator over the values in the map.
	 */
	public class ValueIterator extends BidiIterator<V>
	{
		public ValueIterator() {
		    super(valueTable, false);
		}
		@Override
		public V next() {
		    return nextEntry().getValue();
		}
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
	    if (entrySet == null) {
		entrySet = new EntrySet();
	    }
	    return entrySet;
	}

	/**
	 * Implementation class to provide a {@link Set} view of all the
	 * entries in this map.
	 */
	private final class EntrySet extends AbstractSet<Map.Entry<K,V>>
	{
	    public Iterator<Map.Entry<K,V>> iterator() {
		return new EntryIterator();
	    }
	    public boolean contains(Object o) {
		if (!(o instanceof Map.Entry))
		    return false;
		@SuppressWarnings({"unchecked"})
		Map.Entry<K,V> e = (Map.Entry<K,V>) o;
		Entry<K,V> entry = findKey(e.getKey());
		return entry != null && entry.equals(e);
	    }

	    public boolean remove(Object o) {
		throw new Intl.UnsupportedOperationException("util#bidi.removeNotSupported");
	    }

	    public int size() {
		return size;
	    }

	    public void clear() {
		BidiMap.this.clear();
	    }
	}


	@Override
	public Set<K> keySet() {
	    if (keySet == null) {
		keySet = new KeySet();
	    }
	    return keySet;
	}

	/**
	 * Implementation class to provide a {@link Set} view of the
	 * keys contained in this map.
	 */
	private final class KeySet extends AbstractSet<K>
	{
	    public Iterator<K> iterator() {
		return new KeyIterator();
	    }

	    public int size() {
		return size;
	    }

	    public boolean contains(Object o) {
		return containsKey(o);
	    }

	    public boolean remove(Object o) {
		throw new Intl.UnsupportedOperationException("util#bidi.removeNotSupported");
	    }

	    public void clear() {
		BidiMap.this.clear();
	    }
	}

	@Override
	public Collection<V> values() {
	    if (values == null) {
		values = new Values();
	    }
	    return values;
	}

	/**
	 * Implementation class to provide a {@link Collection} view
	 * of the values contained in this map.
	 */
	private final class Values extends AbstractCollection<V>
	{
	    public Iterator<V> iterator() {
		return new ValueIterator();
	    }

	    public int size() {
		return size;
	    }

	    public boolean contains(Object o) {
		return containsValue(o);
	    }

	    public void clear() {
		BidiMap.this.clear();
	    }
	}


	/**
	 * Dump diagnostics to stdout, mainly to evaluate the hashing efficiencies.
	 */
	public void dumpState() {
	    System.out.format("BidiMap statistics%n------------------%n");
	    System.out.format("Size: %1$d, Capacity: %2$d, Load Factor: %3$6.3f%n", size, keyTable.length, loadFactor);
	    int longestChain = 0;
	    int numFilled = 0;
	    int numKeys = 0, numValues = 0;
	    System.out.format("Key Table:%n----------%n");
	    for (int index = 0; index < keyTable.length; index++) {
		Entry<K, V> entry = keyTable[index];
		if (entry == null)
		    System.out.format("%1$d. null%n", index);
		else {
		    numFilled++;
		    System.out.format("%1$d. ", index);
		    int chainLen = 0;
		    while (entry != null) {
			System.out.format("%1$s", entry.key.toString());
			if (entry.nextKey != null)
			    System.out.print(" -> ");
			entry = entry.nextKey;
			chainLen++;
			numKeys++;
		    }
		    System.out.println();
		    longestChain = Math.max(longestChain, chainLen);
		}
	    }
	    System.out.println("----------------");
	    System.out.format("Longest key chain: %1$d, %% filled = %2$6.1f%n", longestChain, (float)numFilled / (float)keyTable.length * 100.0f);
	    System.out.format("Size = %1$d, number of keys = %2$d%n", size, numKeys);
	    System.out.println("================");
	    longestChain = 0;
	    numFilled = 0;
	    System.out.format("Value Table:%n------------%n");
	    for (int index = 0; index < valueTable.length; index++) {
		Entry<K, V> entry = valueTable[index];
		if (entry == null)
		    System.out.format("%1$d. null%n", index);
		else {
		    numFilled++;
		    System.out.format("%1$d. ", index);
		    int chainLen = 0;
		    while (entry != null) {
			System.out.format("%1$s", entry.value.toString());
			if (entry.nextValue != null)
			    System.out.print(" -> ");
			entry = entry.nextValue;
			chainLen++;
			numValues++;
		    }
		    System.out.println();
		    longestChain = Math.max(longestChain, chainLen);
		}
	    }
	    System.out.println("----------------");
	    System.out.format("Longest value chain: %1$d, %% filled = %2$6.1f%n", longestChain, (float)numFilled / (float)keyTable.length * 100.0f);
	    System.out.format("Size = %1$d, number of values = %2$d%n", size, numValues);
	    System.out.println("================");
	}

}
