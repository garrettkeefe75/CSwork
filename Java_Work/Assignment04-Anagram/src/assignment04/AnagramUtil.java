package assignment04;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

import components.simplereader.*;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class AnagramUtil
{

	/**
	 * Reports whether the two input strings are anagrams of each other.
	 *
	 * @param  s1 the first candidate string
	 * @param  s2 the second candidate string
	 * @return    true iff {@code  s1} and {@code  s2} are anagrams of each other
	 */
	public static boolean areAnagrams(String s1, String s2)
	{

		// run insertions sort on both strings to sort them,
		// then check that both groups of characters are equal from there
		return sort(s1).equals(sort(s2));
	}

	/**
	 * Returns the largest group of anagrams from the list of words in the given
	 * file , in no particular order.
	 *
	 * It is assumed that the file contains one word per line. If the file is empty
	 * , the method returns an empty list because there are no anagrams.
	 *
	 * @param  filename file to read strings from
	 * @return          largest group of anagrams in the input file
	 */
	public static List<String> getLargestAnagramGroup(String filename)
	{
		SimpleReader reader = new SimpleReader1L(filename);

		// takes the file and adds each line as a seperate string to a list
		List<String> input = new ArrayList<String>();

		String nextLine = "";

		while (!reader.atEOS())
		{
			nextLine = reader.nextLine();
			input.add(nextLine);

		}

		reader.close();

		return AnagramUtil.getLargestAnagramGroup(input); 
	}
	
	/**
	 * To be used with the analysis class, creates the list of strings
	 * up to a certain point
	 * @param problemSize The problem size of strings to put through various
	 * Tests. 
	 * @return A list of strings to the given problem size index. 
	 */
	protected static List<String> getStrings(int problemSize)
	{
		SimpleReader reader = new SimpleReader1L("data/words_english.txt");
		
		List<String> input = new ArrayList<String>();
		
		String nextLine = "";

		int i = 0;
		
		while (i < problemSize)
		{
			nextLine = reader.nextLine();
			input.add(nextLine);
			i++;
		}
		
		reader.close();
		
		return input;
	}

	/**
	 * Sorts the input list using an insertion sort and the input
	 * {@code  Comparator} object.
	 *
	 * @param    <T>   type of the element of the list
	 * @param    list  input list
	 * @param    order comparator used to sort elements
	 *
	 * @modifies       {@code  list}
	 */
	public static <T> void insertionSort(List<T> list, Comparator<? super T> order)
	{

		for (int i = 1; i < list.size(); i++)
		{
			T currentElement = list.get(i);

			int j = i - 1;

			while (j >= 0 && order.compare(list.get(j), currentElement) > 0)
			{
				list.set(j + 1, list.get(j));
				j -= 1;
			}

			list.set(j + 1, currentElement);
		}

	}

	/**
	 * Returns a case -insensitive sorted version of the input String. For example ,
	 * if {@code  str = "Utah"}, it returns {@code "ahtu "}. This sorting must be
	 * done using insertion sort.
	 *
	 * @param  str string to be sorted
	 * @return     sorted string
	 */
	public static String sort(String str)
	{
		char[] charArr = str.toLowerCase().toCharArray();

		List<Character> input = new ArrayList<Character>();

		for (int i = 0; i < charArr.length; i++)
		{
			input.add(charArr[i]);
		}

		characterComp c = new characterComp();

		insertionSort(input, c);

		String sortedString = "";

		for (int i = 0; i < input.size(); i++)
		{
			sortedString += input.get(i);
		}

		return sortedString;
	}

	/**
	 * Returns the largest group of anagrams in the input list of words , in no
	 * particular order.
	 *
	 * @param  input list of strings
	 * @return       largest group of anagrams in {@code  input} 3
	 */
	public static List<String> getLargestAnagramGroup(List<String> input)
	{
		order o1 = new order();
		AnagramUtil.insertionSort(input, o1);

		ArrayList<String> biggestAnagramList = new ArrayList<String>();

		ArrayList<String> currentList = new ArrayList<String>();

		for (int i = 0; i < input.size() - 1; i++)
		{
			while (i < input.size() - 1 && areAnagrams(input.get(i), input.get(i + 1)))
			{
				currentList.add(input.get(i));
				i++;
			}

			if (i != 0 && areAnagrams(input.get(i - 1), input.get(i)))
			{
				currentList.add(input.get(i));
			}

			if (currentList.size() > biggestAnagramList.size())
			{
				biggestAnagramList = currentList;
			}
			currentList = new ArrayList<String>();

		}

		return biggestAnagramList;
	}

	protected static class order implements Comparator<String>
	{

		@Override
		public int compare(String o1, String o2)
		{
			if (areAnagrams(o1, o2))
			{
				return 0;
			}

			else if (sort(o1).compareTo(sort(o2)) < 0)
			{
				return -1;
			}

			else
			{
				return 1;
			}
		}

	}

	protected static class characterComp implements Comparator<Character>
	{

		@Override
		public int compare(Character o1, Character o2)
		{
			return Character.compare(o1, o2);
		}

	}

}
