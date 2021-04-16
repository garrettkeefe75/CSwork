package assignment09;

import java.util.Comparator;

import components.list.List;
import components.list.DoublyLinkedList;

/**
 * Implementation of a Binary Heap on array.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <E> type of the element of the heap
 */
public class Heap<E>
{
	private static final int INITIAL_CAPACITY = 10;

	/**
	 * The backing array of this heap.
	 */
	private E[] rep;

	/**
	 * The comparator used for ordering this heap.
	 */
	private Comparator<E> order;

	/**
	 * The last element in the heap.
	 */
	private int end;

	/**
	 * The size of this heap.
	 */
	private int size;

	/**
	 * No argument constructor, builds an empty heap.
	 * 
	 * @ensures this is a heap
	 */
	@SuppressWarnings("unchecked")
	public Heap( )
	{
		this.inferOrder();
		this.rep = (E[]) new Object[INITIAL_CAPACITY];
		this.size = 0;
		this.end = -1;
	}

	/**
	 * Constructor from a comparator, builds an empty heap with the given ordering.
	 * 
	 * @param   cmp comparator for the new heap
	 * @ensures     this is a heap
	 */
	@SuppressWarnings("unchecked")
	public Heap(Comparator<E> cmp)
	{
		this.order = cmp;
		this.rep = (E[]) new Object[INITIAL_CAPACITY]; // change size?
		this.size = 0;
		this.end = -1;
	}

	/**
	 * Constructor from an array, builds a heap from the given array elements.
	 * 
	 * @param   args elements to put in this heap
	 * @ensures      this is a heap
	 */
	public Heap(E[] args)
	{
		this.inferOrder();
		this.rep = args;
		this.size = args.length;
		this.end = args.length - 1;
		this.heapify();

	}

	/**
	 * Infers the order based on the type of the elements, {@code E}. If
	 * {@code E extends Comparable}, it gets the order from that relation,
	 * otherwise, it tries to infer it by comparing the hashcodes of the two
	 * elements.
	 */
	private void inferOrder()
	{
		this.order = new Comparator<E>()
		{
			// sets up a viable comparator for generic type E, returning hashcode
			// if the type used doesn't implement comparable.
			@SuppressWarnings("unchecked")
			@Override
			public int compare(E x, E y)
			{
				try
				{
					return ((Comparable<E>) x).compareTo(y);
				}
				catch (ClassCastException e)
				{
					return Integer.compare(x.hashCode(), y.hashCode());
				}
			}
		};
	}

	/**
	 * Converts the given complete binary tree into a heap.
	 * 
	 * @param    start index of the root in the underlying array representation
	 * @ensures        the rep is a heap
	 * @requires       the rep is a complete binary tree
	 * @modifies       this
	 */
	private void heapify()
	{
		// sifts down evey value halfway through the tree, ensuring every parent 
		// and child is checked
		for (int i = this.rep.length / 2; i >= 0; i--)
		{
			this.siftDown(i);
		}
	}

	/**
	 * Adds {@code x} to this maintaining the heap property.
	 * 
	 * @param    x element to be added
	 * @modifies   this
	 */
	@SuppressWarnings("unchecked")
	public void add(E x)
	{
		// if the array is full, recopy with double the size
		if (this.rep.length - 1 == this.end)
		{
			E[] new_ = (E[]) new Object[this.rep.length * 2];
			for (int i = 0; i < this.rep.length; i++)
			{
				new_[i] = rep[i];
			}

			this.rep = new_;
		}
		// adds element to array and floats the value to correct position
		this.rep[this.end + 1] = x;

		this.end++;
		this.size++;
		this.floatUp(end);
	}

	/**
	 * Removes and returns the first element from the heap.
	 * 
	 * @return   the first element of this
	 * @requires this is not empty
	 * @modifies this
	 */
	public E removeFirst()
	{
		E copy = this.rep[0];
		// takes copy of element and places the value at end of the array to replace
		// the first element.
		this.rep[0] = this.rep[this.end];
		this.rep[end] = null;
		this.end--;
		this.size--;
		// sifts down the first element to correct position.
		this.siftDown(0);

		return copy;
	}

	/**
	 * Reports the number of elements in this heap.
	 * 
	 * @return number of elements in this
	 */
	public int size()
	{
		return this.size;
	}

	/**
	 * Change the order of this heap to the given one.
	 * 
	 * @param    cmp new ordering relation
	 * @modifies     this
	 */
	public void changeOrder(Comparator<E> cmp)
	{
		this.order = cmp;
		this.heapify();
	}

	/**
	 * Empties all of the elements in this heap.
	 * 
	 * @modifies this
	 */
	@SuppressWarnings("unchecked")
	public void clear()
	{
		this.rep = (E[]) new Object[INITIAL_CAPACITY];
		this.size = 0;
	}

	/**
	 * Puts the contents of this heap in a list in a sorted order and returns it,
	 * emptying the heap in the process.
	 * 
	 * @return   contents of the heap in a sorted order
	 * @modifies this
	 */
	public List<E> sort()
	{
		List<E> sortedRep = new DoublyLinkedList<E>();
		// adds every element in the heap to the list, with remove first implicitly
		// sorting every element it output
		while(this.size > 0)
			sortedRep.add(this.removeFirst());


		return sortedRep;
	}

	/*
	 * ==========Methods from Object==========
	 */

	@Override
	public String toString()
	{
		StringBuilder string = new StringBuilder();

		string.append("[");

		if (this.size == 0)
		{
			string.append("]");
			return string.toString();
		}

		string.append(this.rep[0]);

		for (int i = 1; i < this.size; i++)
		{
			string.append("," + rep[i]);
		}

		string.append("]");

		return string.toString();
	}

	/*
	 * ==========Helper Methods==========
	 */

	/**
	 * Sorts an item at start upward if possible.
	 * 
	 * @param    start
	 * @modifies       this
	 */
	private void floatUp(int start)
	{
		int parentIndex = (start - 1) / 2;

		int compare = this.order.compare(this.rep[parentIndex], this.rep[start]);

		if (start == 0)
			return;
		// checks if value at start is lesser than its parent and swaps if so.
		else if (compare > 0)
		{
			this.swapElements(start, parentIndex);
			this.floatUp(parentIndex);
		}

		return;
	}

	/**
	 * Determines the smallest of start's two children
	 * @param start the parent index. 
	 * @return	the index of the smallest of the two children. 
	 * @requires The children of start exist. 
	 */
	private int smallestOfTwo(int start)
	{
		int left = (start * 2) + 1;
		int right = (start * 2) + 2;

		//compares the left and right children
		int compare = this.order.compare(this.rep[left], this.rep[right]);

		if (compare < 0)
			return left;

		else
		{
			return right;
		}
	}

	/**
	 * Swaps elements at given indeces
	 * 
	 * @param    parent  The index of the parent "node".
	 * @param    biggest The index of the largest child of parent.
	 * @modifies         this.rep
	 */
	private void swapElements(int parent, int biggest)
	{
		E copy = this.rep[parent];
		this.rep[parent] = this.rep[biggest];
		this.rep[biggest] = copy;
	}

	/**
	 * Sifts the root of the tree down appropriately so that the resulting tree is a
	 * heap.
	 * 
	 * @param    start index of the root in the underlying array representation
	 * @requires       both left and right subtrees are heaps
	 * @ensures        the rep is a heap
	 * @modifies       this
	 */
	private void siftDown(int start)
	{
		// check relation to end
		int left = (start * 2) + 1;
		int right = (start * 2) + 2;

		if (left > this.end)
			return;

		else if (right > this.end)
		{
			// if the left child is smaller, swap it
			if (order.compare(this.rep[start], this.rep[left]) > 0)
			{
				this.swapElements(start, left);

				return;
			}

			return;
		}

		else
		{
			// find smallest of two
			int smallest = this.smallestOfTwo(start);

			int compare = this.order.compare(this.rep[start], this.rep[smallest]);

			if (compare > 0)
			{
				// swap
				this.swapElements(start, smallest);

				this.siftDown(smallest);

				return;
			}

			else
			{
				return;
			}
			// check
		}
	}

}
