package assignment08;

import components.set.Set;
import components.set.SetOnArrayList;

/**
 * Hash Table based implementation of a Set.
 * 
 * @author     David Harrington && Ethan Pippin
 *
 * @param  <E> type of the elements of this set
 */
public class HashTableSet<E>
{

	/*
	 * ========================= Private members =========================
	 */
	private static final int DEFAULT_NUM_BUCKETS = 101;
	private Set<E>[] hashTable;
	private int size;

	/*
	 * ========================= Helper methods ==========================
	 */

	/**
	 * Computes {@code a} mod {@code b} as % should have been defined to work.
	 *
	 * @param    a the number being reduced
	 * @param    b the modulus
	 * @return     the result of a mod b, which satisfies {@code 0 <=  mod < b}
	 *             smallest r such that a = bq + r, 0<=r<a r = a - bq
	 * @requires   b > 0
	 */
	public static int mod(int a, int b)
	{
		assert b > 0 : "Violation of: b > 0";
		int r = a % b;
		return r < 0 ? r + b : r;
	}

	/*
	 * ========================= Constructors =========================
	 */

	/**
	 * No-argument constructor, resulting in a hash table of default size.
	 */
	@SuppressWarnings("unchecked")
	public HashTableSet( )
	{
		this.hashTable = (Set<E>[]) new Set[DEFAULT_NUM_BUCKETS];

		for (int i = 0; i < hashTable.length; i++)
		{
			hashTable[i] = new SetOnArrayList<E>();
		}

		this.size = 0;
	}

	/**
	 * Constructor resulting in a hash table of size {@code hashTableSize}.
	 *
	 * @param hashTableSize size of hash table
	 */
	@SuppressWarnings("unchecked")
	public HashTableSet(int hashTableSize)
	{
		this.hashTable = (Set<E>[]) new Set[hashTableSize];

		for (int i = 0; i < hashTable.length; i++)
		{
			hashTable[i] = new SetOnArrayList<E>();
		}

		this.size = 0;
	}

	/*
	 * ========================= Set methods =========================
	 */

	/**
	 * Adds {@code x} to {@code this}.
	 *
	 * @param    x the element to be added
	 * 
	 * @modifies   {@code this}
	 * 
	 * @requires   {@code x} is not in {@code this}
	 * 
	 */
	public void add(E x)
	{
		assert x != null : "Violation of: x is not null";
		assert !this.contains(x) : "Violation of: x is not in this";

		// finds the index of where to insert
		int index = mod(x.hashCode(), this.hashTable.length);

		// removes the item from the given index of the hashtable
		this.hashTable[index].add(x);

		// increment size
		this.size++;
	}

	/**
	 * Removes {@code x} from this.
	 *
	 * @param    x the element to be removed
	 *
	 * @modifies   {@code this}
	 * 
	 * @requires   {@code x} is in {@code this}
	 * 
	 */
	public void remove(E x)
	{
		assert x != null : "Violation of: x is not null";
		assert this.contains(x) : "Violation of: x is in this";

		// calculates index of where to remove
		int index = mod(x.hashCode(), this.hashTable.length);

		// removes the item from the given index of the hashtable
		this.hashTable[index].remove(x);

		// deincrement size
		this.size--;
	}

	/**
	 * Reports whether {@code x} is in {@code this}.
	 *
	 * @param  x the element to be checked
	 * @return   true iff element is in {@code this}
	 * 
	 */
	public boolean contains(E x)
	{
		assert x != null : "Violation of: x is not null";

		// calculates index of where to check
		int index = mod(x.hashCode(), this.hashTable.length);

		// reports whether hashTable[index] contains x
		return this.hashTable[index].contains(x);
	}

	/**
	 * Reports the number of elements in {@code this}.
	 * 
	 * @return size of this set
	 */
	public int size()
	{
		return this.size;
	}

	/**
	 * Removes all the elements in {@code this}.
	 */
	@SuppressWarnings("unchecked")
	public void clear()
	{
		this.hashTable = (Set<E>[]) new SetOnArrayList[DEFAULT_NUM_BUCKETS];
		for (int i = 0; i < hashTable.length; i++)
		{
			hashTable[i] = new SetOnArrayList<E>();
		}
		size = 0;
	}

	/*
	 * Methods inherited from Object
	 */
	/**
	 * String representation of the hash table. Elements in bucket 0, followed by
	 * those in bucket 1, and so on, separated by commas and enclosed in {..}.
	 */
	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder("{");

		for (int i = 0; i < this.hashTable.length; i++)
		{
			for (E item : hashTable[i])
			{
				string.append(String.format("%s,", item));
			}
		}

		if (string.length() != 1)
		{
			string.deleteCharAt(string.length() - 1);
		}

		string.append("}");

		return string.toString();
	}

	/*
	 * Other methods specific to hash tables; for testing/performance purposes only
	 */

	/**
	 * Returns the number of elements in the specified bucket.
	 * 
	 * @param    bucketIndex index of the bucket requested
	 * @requires             0 <= bucketIndex < numBuckets()
	 */
	public int bucketSize(int bucketIndex)
	{
		return this.hashTable[bucketIndex].size();
	}

	/**
	 * Reports the number of buckets in this hashtable.
	 * 
	 * @return number of buckets
	 */
	public int numBuckets()
	{
		return this.hashTable.length;
	}
}
