package assignment06;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import components.list.DoublyLinkedList;

/**
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <E>
 */

public class A6DoublyLinkedList<E> extends DoublyLinkedList<E>
{
	/**
	 * 
	 * @author david
	 *
	 */
	private class A6DoublyLinkedListIterator implements Iterator<E>
	{
		/** Current node. */
		private Node<E> current;
		/**
		 * Expected count of modifications , set to modCount in the constructor.
		 */
		private int expectedModCount;

		public A6DoublyLinkedListIterator( )
		{
			//sets current to start.next
			current = start.next;
			
			//sets the expected modCount to modCount
			expectedModCount = modCount;
		}

		@Override
		public boolean hasNext()
		{
			//checks if the list has been modified 
			//while the iterator is being used
			if (expectedModCount != modCount)
				throw new ConcurrentModificationException();

			//checks if the next item is null or not
			return current.next != null;
		}

		@Override
		public E next()
		{
			if (!hasNext())
				throw new NoSuchElementException();

			E hold = current.data;
			current = current.next;

			return hold;
		}

	}

	/**
	 * 
	 * @author     David Harrington && Garrett Keefe
	 * 
	 * @param  <E> The type of data to be stored.
	 */
	@SuppressWarnings("hiding")
	private class Node<E>
	{
		/** Reference to the previous node in the chain. */
		public Node<E> previous;

		/** The data stored in this node. */
		public E data;

		/** Reference to the next node in the chain. */
		public Node<E> next;

		/**
		 * Default no-argument constructor.
		 */
		public Node( )
		{
			// sets each instance variable to null
			this.previous = null;

			this.next = null;

			this.data = null;
		}
	}

	/** A constant for when an item is not found in the list. */
	private static final int NOT_FOUND = -1;

	/** The head of this linked list. */
	private Node<E> start;

	/** The tail of this linked list. */
	private Node<E> end;

	/** The size of this linked list. */
	private int size;

	/**
	 * Counts the number of modifications made to this linked list
	 */
	private int modCount;

	public A6DoublyLinkedList( )
	{
		// sets start and end to new nodes
		this.start = new Node<E>();
		this.end = new Node<E>();

		// sets start's next to end
		// and end's previous to start
		this.start.next = this.end;
		this.end.previous = this.start;

		this.size = 0;
		this.modCount = 0;
	}

	@Override
	public void clear()
	{
		// sets start and end to new nodes
		this.start = new Node<E>();
		this.end = new Node<E>();

		// see constructor
		this.start.next = this.end;
		this.end.previous = this.start;

		this.size = 0;
		this.modCount++;
	}

	@Override
	public void add(int index, E x)
	{
		assert index <= this.size : "Violation of : index > size";
		assert x != null : "Violation of : x is not null";
		assert 0 <= index : "Violation of : index < 0";

		// creates a reference to start
		Node<E> node = this.start;

		// iterates through the nodes
		// until it hits the node before the index
		for (int i = 0; i < index; i++)
		{
			node = node.next;
		}

		// creates a new node containing
		// the data (x) to be inserted
		Node<E> newNode = new Node<E>();
		newNode.data = x;

		// creates a reference to the current node at the index
		Node<E> nextNode = node.next;

		// inserts newNode between node and previous node
		node.next = newNode;
		newNode.previous = node;
		newNode.next = nextNode;
		nextNode.previous = newNode;

		// increments size and modcount
		this.size++;
		this.modCount++;
	}

	@Override
	public void add(E x)
	{
		assert x != null : "Violation of : x is not null";

		// creates a new node
		// containing the data to be inserted
		Node<E> node = new Node<E>();
		node.data = x;

		// creates a reference to end's previous node.
		Node<E> prev = this.end.previous;

		// inserts the new node between
		// end's previous and end
		prev.next = node;
		node.previous = prev;
		node.next = this.end;
		this.end.previous = node;

		// incremets size and modcount
		this.size++;
		this.modCount++;
	}

	@Override
	public E get(int index)
	{
		assert index >= 0 : "Violation of : index < 0";
		assert index < this.size : "Violation of : index > size";

		E x = null;

		// creates a reference to start's next
		Node<E> node = this.start.next;

		// iterates through the list
		for (int i = 0; i < index; i++)
		{
			node = node.next;
		}

		// creates a variable to return x
		x = node.data;

		return x;
	}

	@Override
	public void set(int index, E x)
	{
		assert x != null : "Violation of : x is null";
		assert index >= 0 : "Violation of : index < 0";
		assert index < this.size : "Violation of index > size";

		// creates a reference to start's next
		Node<E> node = this.start.next;

		// iterates through the list
		for (int i = 0; i < index; i++)
		{
			node = node.next;
		}

		// sets node's data to x
		node.data = x;

	}

	@Override
	public int indexOf(E x)
	{
		assert x != null : "Violation of : x is not null";
		assert this.size > 0 : "Violation of : size is less than 0";

		// creates a reference to start's next
		Node<E> node = this.start.next;

		// iterates through the list
		for (int i = 0; i < this.size; i++)
		{
			// if the current node's data == x, return i
			if (node.data.equals(x))
				return i;

			node = node.next;
		}

		// returns -1 if the item is not found
		return NOT_FOUND;
	}

	@Override
	public E remove(int index)
	{
		assert index >= 0 : "Violation of : index < 0";
		assert index < this.size : "Violation of : index > size";
		assert this.size > 0 : "Violation of : size < 0";

		// creates a reference to start's next
		Node<E> node = this.start.next;

		// iterates throught the list
		for (int i = 0; i < index; i++)
		{
			node = node.next;
		}

		// set's x to node's data
		E x = node.data;

		// creates a reference to node's previous and next
		Node<E> prev = node.previous;
		Node<E> next = node.next;

		// remove's node
		prev.next = next;
		next.previous = prev;

		// decrements size and increments modCount
		this.size--;
		this.modCount++;

		return x;
	}

	@Override
	public int size()
	{
		// self explanitory
		return this.size;
	}

	@Override
	public String toString()
	{
		// if list is empty, return "[]"
		if (this.size == 0)
		{
			return "[]";
		}

		else
		{
			// creates an iterator to iterate through the list
			Iterator<E> iterator = new A6DoublyLinkedListIterator();

			StringBuilder string = new StringBuilder();

			// appends "[" and "iterator.next()"
			string.append("[");
			string.append(iterator.next());

			// while iterator has a next item
			// append "," and "iterator.next"
			while (iterator.hasNext())
				string.append("," + iterator.next());

			// closes the string with "]"
			string.append("]");

			return string.toString();
		}

	}

	@Override
	public Iterator<E> iterator()
	{
		return new A6DoublyLinkedListIterator();
	}

	@Override
	public void reverse()
	{
		// starts at the end of the list
		// removes the nodes and adds them
		// back to the end of the list.
		for (int i = this.size - 1; i >= 0; i--)
		{
			this.add(this.remove(i));
		}

	}

}
