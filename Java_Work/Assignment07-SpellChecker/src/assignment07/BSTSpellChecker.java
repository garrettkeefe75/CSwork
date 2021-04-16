package assignment07;

import components.list.DoublyLinkedList;
import components.list.List;
import components.simplereader.*;

public class BSTSpellChecker implements SpellChecker
{

	/**
	 * The set of valid words, represented by a Binary Search Tree.
	 */
	private BinarySearchTreeOfStrings validWords;

	/**
	 * Default, no-argument constructor.
	 */
	public BSTSpellChecker( )
	{
		this.validWords = new BinarySearchTreeOfStrings();
	}

	@Override
	public void loadValidWords(String filename)
	{
		SimpleReader reader = new SimpleReader1L(filename);

		this.validWords.clear();

		while (!reader.atEOS())
		{
			validWords.insert(reader.nextLine());
		}
		
		reader.close();

	}

	@Override
	public List<String> misspelledWords(String filename)
	{
		SimpleReader reader = new SimpleReader1L(filename);

		List<String> list = new DoublyLinkedList<String>();

		while (!reader.atEOS())
		{
			misspelledListBuilder(reader.nextLine(), list);
		}

		reader.close();

		return list;
	}

	@Override
	public void clear()
	{
		this.validWords.clear();
	}
	
	//------------------------------------------------------------------
	//Helper methods
	//------------------------------------------------------------------
	
	/**
	 * 
	 * @param string A line from the file to spell check.
	 * @param list   The list of Invalid strings.
	 */
	private void misspelledListBuilder(String string, List<String> list)
	{

		String[] arr = string.split(" ");

		for (int i = 0; i < arr.length; i++)
			if (!this.validWords.contains(arr[i]))
				list.add(arr[i]);

	}

}
