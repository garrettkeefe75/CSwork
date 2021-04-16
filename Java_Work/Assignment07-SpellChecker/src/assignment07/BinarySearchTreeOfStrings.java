package assignment07;

/**
 * A binary search tree implementation from scratch.
 * 
 * @author David Harrinton && Garrett Keefe
 *
 */
public class BinarySearchTreeOfStrings
{
	/**
	 * The root of the tree.
	 */
	private Node root;

	/**
	 * The size of the tree.
	 */
	private int size;

	/**
	 * 
	 * @author David Harrington && Garrett Keefe
	 *
	 */
	private class Node
	{

		/**
		 * The data in the node.
		 */
		public String data;

		/**
		 * A reference to the left child of this node.
		 */
		public Node leftChild;

		/**
		 * A reference to the right child of this node.
		 */
		public Node rightChild;

		/**
		 * Default constructor for Node.
		 */
		public Node( )
		{
			// sets all data fields to empty/null
			this.data = null;
			this.leftChild = null;
			this.rightChild = null;
		}
	}

	/**
	 * No argument constructor.
	 */
	public BinarySearchTreeOfStrings( )
	{
		this.root = new Node();
	}

	/**
	 * Removes all the nodes in this tree.
	 * 
	 * @modifies this tree
	 */
	public void clear()
	{
		this.root = new Node();
		this.size = 0;
	}

	/**
	 * Inserts the element {@code x} at the appropriate position in this tree.
	 * 
	 * @param    x element to be inserted
	 * @modifies   this tree
	 */
	public void insert(String x)
	{
		// if the tree is empty, set root's data to x
		if (this.size == 0)
		{
			this.root = new Node();
			this.root.data = x;
		}

		// run the recursive
		else
		{
			insertRecurse(this.root, x);
		}

		// increment size accordingly
		this.size++;
	}

	/**
	 * Removes the element {@code x} from this tree.
	 * 
	 * @param    x element to be removed
	 * @modifies   this tree
	 * @requires   {@code x} is in {@code this}
	 */
	public void remove(String x)
	{
		if (x.compareTo(this.root.data) == 0)
		{
			if (this.root.leftChild == null && this.root.rightChild == null)
			{
				this.root = new Node();
			}

			else if (this.root.leftChild == null)
			{
				this.root = this.root.rightChild;
			}

			else if (this.root.rightChild == null)
			{
				this.root = this.root.leftChild;
			}

			else
			{
				String data = this.smallestNode(this.root.rightChild).data;
				this.removeRecurse(this.root, data);
				this.root.data = data;
			}
			
			size--;

		}

		else
		{
			removeRecurse(root, x);
			this.size--;
			return;
		}
	}

	/**
	 * Reports the number of nodes in this tree.
	 * 
	 * @return size of {@code this}
	 */
	public int size()
	{
		return this.size;
	}

	/**
	 * Reports whether this tree contains {@code x}.
	 * 
	 * @param  x element to search
	 * @return   true iff this contains x
	 */
	public boolean contains(String x)
	{
		if (this.size == 0)
			return false;

		return containsRecursive(this.root, x);
	}

	/**
	 * Returns the root of this tree.
	 * 
	 * @return   root of this tree
	 * @requires this is not empty
	 */
	public String root()
	{
		return this.root.data;
	}

	@Override
	public String toString()
	{
		StringBuilder stringRep = new StringBuilder();

		stringRep.append("[");
		inOrderRecursive(this.root, stringRep);
		stringRep.append("]");

		return stringRep.toString();
	}

	// ----------------------------------------------------------------------------
	// Recursive helper methods
	// ----------------------------------------------------------------------------

	/**
	 * Determines whether the tree contains {@code y}, recurses through {@code x}.
	 * 
	 * @param  x The node being recursed through.
	 * @param  y The element to locate in the tree.
	 * @return
	 */
	private boolean containsRecursive(Node x, String y)
	{
		// determines which direction y is located in
		int compare = y.compareTo(x.data);

		// if x's data is equal to y, returns true
		if (compare == 0)
			return true;

		else if (compare > 0)
		{
			// if the recursion reaches the end of the tree, returns false
			if (x.rightChild == null)
				return false;

			else
			{
				// else, recurses through the right child.
				return containsRecursive(x.rightChild, y);
			}
		}

		else
		{
			// if the recursion reaches the end of the tree, returns false
			if (x.leftChild == null)
				return false;

			else
			{
				// else, recurses through the left child.
				return containsRecursive(x.leftChild, y);

			}
		}

	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	private void inOrderRecursive(Node x, StringBuilder y)
	{
		if (x == null || x.data == null)
		{
			return;
		}

		// recurses through the left child
		inOrderRecursive(x.leftChild, y);

		// appends the node
		if (x.data.compareTo(this.biggestNode(this.root).data) == 0)
			y.append(x.data);

		else
		{
			y.append(x.data + ",");

		}

		// recurses through the right child
		inOrderRecursive(x.rightChild, y);
	}

	/**
	 * Finds and returns the smallest(leftmost) node from {@code x}.
	 * 
	 * @param  x the node to search from.
	 * @return   The smallest(leftmost) node from {@code x}.
	 */
	private Node smallestNode(Node x)
	{
		if (x.leftChild == null)
			return x;

		return smallestNode(x.leftChild);
	}

	/**
	 * Finds and returns the largest(rightmost) node from {@code x}.
	 * 
	 * @param  x the node to search from.
	 * @return   The largest(rightmost) node from {@code x}.
	 */
	private Node biggestNode(Node x)
	{
		if (x.rightChild == null)
			return x;

		return biggestNode(x.rightChild);
	}

	/**
	 * Recursive helper method for the insert method, finds where {@code y} belongs
	 * in the tree, recursing through {@code x}.
	 * 
	 * @param x The node to recurse through.
	 * @param y The string to insert.
	 */
	private void insertRecurse(Node x, String y)
	{
		// comparison between x's data and y
		int compare = y.compareTo(x.data);

		// if y is smaller than x, move it to the left
		if (compare < 0)
		{
			// if the left child is null,
			// create a child and place y there
			if (x.leftChild == null)
			{
				Node z = new Node();
				z.data = y;
				x.leftChild = z;
			}

			// else recurse throught the left child
			else
			{
				insertRecurse(x.leftChild, y);
			}

		}

		// if y is bigger than x, move it to the right
		if (compare > 0)
		{
			// if the right child is null
			// create a child and place y there
			if (x.rightChild == null)
			{
				Node z = new Node();
				z.data = y;
				x.rightChild = z;
			}

			// else recurse through the right child
			else
			{
				insertRecurse(x.rightChild, y);
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 */
	private void removeRecurse(Node x, String y)
	{
		int compare = y.compareTo(x.data);

		// determine direction
		//
		if (compare < 0)
		{
			if (y.compareTo(x.leftChild.data) == 0)
			{
				if (x.leftChild.leftChild == null && x.leftChild.rightChild == null)
				{
					x.leftChild = null;
					return;
				}
				else if (x.leftChild.leftChild == null)
				{
					x.leftChild = x.leftChild.rightChild;
					return;

				}
				else if (x.leftChild.rightChild == null)
				{
					x.leftChild = x.leftChild.leftChild;
					return;
				}
				else
				{
					String data = this.smallestNode(x.leftChild.rightChild).data;
					this.removeRecurse(x.leftChild.rightChild, data);
					x.leftChild.data = data;
				}

			}

			else
			{
				removeRecurse(x.leftChild, y);
			}
		}

		else if (compare > 0)
		{
			if (y.compareTo(x.rightChild.data) == 0)
			{
				if (x.rightChild.leftChild == null && x.rightChild.rightChild == null)
				{
					x.rightChild = null;
					return;
				}
				else if (x.rightChild.leftChild == null)
				{
					x.rightChild = x.rightChild.rightChild;
					return;

				}
				else if (x.rightChild.rightChild == null)
				{
					x.rightChild = x.rightChild.leftChild;
					return;
				}
				else
				{
					String data = this.smallestNode(x.rightChild.rightChild).data;
					this.removeRecurse(x.rightChild.rightChild, data);
					x.rightChild.data = data;
				}
			}

			else
			{
				removeRecurse(x.rightChild, y);
			}
		}
		
		else {
			
		}
	}
}
