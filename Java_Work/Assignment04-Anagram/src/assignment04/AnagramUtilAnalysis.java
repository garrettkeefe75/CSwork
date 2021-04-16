package assignment04;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.stopwatch.Stopwatch;
import components.stopwatch.Stopwatch1;

public class AnagramUtilAnalysis
{

	private static Stopwatch timer;

	private static final int THOUSAND = 1000;

	public static void main(String[] args)
	{
		SimpleWriter out = new SimpleWriter1L();

		int[] problemSize =
		{ 10, 100, 500, 1000, 5000, 10000, 50000, 100000};

		out.println("---AreAnagrams Experiment---");
		out.println("Problem Size | Time Elapsed");
		for (int i = 0; i < problemSize.length; i++)
		{
			areAnagramsExperiment(problemSize[i]);
		}

		out.println();
		out.println("---InsertionSortExperiment---(Strings)");
		out.println("Problem Size | Time Elapsed");
		for (int i = 0; i < problemSize.length - 2; i++)
		{
			insertionSortExperimentString(problemSize[i]);
		}

		out.println();
		out.println("---InsertionSortExperiment---(Integers)");
		out.println("Problem Size | Time Elapsed");
		for (int i = 0; i < problemSize.length; i++)
		{
			insertionSortExperimentInt(problemSize[i]);
		}

		out.println();
		out.println("---AnagramGroup Experiment---");
		out.println("Problem Size | Time Elapsed");
		for (int i = 0; i < problemSize.length - 2; i++)
		{
			anagramGroupExperiment(problemSize[i]);
		}
		
		out.close();
	}

	private static void areAnagramsExperiment(int problemSize)
	{
		SimpleWriter out = new SimpleWriter1L();

		// generate string of problem size n
		String s = randomString(problemSize);

		timer = new Stopwatch1();

		timer.reset();

		timer.start();

		AnagramUtil.areAnagrams(s, s);
		
		timer.stop();

		out.println(problemSize + " | " + timer.elapsed() / THOUSAND);

		out.close();
	}

	private static void insertionSortExperimentString(int problemSize)
	{
		SimpleWriter out = new SimpleWriter1L();
		
		timer = new Stopwatch1();

		List<String> list = AnagramUtil.getStrings(problemSize);
		
		order o = new order();		
		
		timer.reset();

		timer.start();
		
		AnagramUtil.insertionSort(list, o);

		timer.stop();
		
		out.println(problemSize + " | " + timer.elapsed() / THOUSAND);

		out.close();
	}

	private static void insertionSortExperimentInt(int problemSize)
	{
		SimpleWriter out = new SimpleWriter1L();

		timer = new Stopwatch1();

		ArrayList<Integer> list = new ArrayList<Integer>();

		IntegerComp c = new IntegerComp();

		for (int i = problemSize; i >= 0; i--)
		{
			list.add(i);
		}

		timer.reset();

		timer.start();

		AnagramUtil.insertionSort(list, c);

		timer.stop();

		out.println(problemSize + " | " + timer.elapsed() / THOUSAND);

		out.close();
	}

	private static void anagramGroupExperiment(int problemSize)
	{
		SimpleWriter out = new SimpleWriter1L();
		
		timer = new Stopwatch1();
		
		List<String> input = AnagramUtil.getStrings(problemSize);
		
		timer.reset();

		timer.start();
		
		AnagramUtil.getLargestAnagramGroup(input);
		
		timer.stop();
		
		out.println(problemSize + " | " + timer.elapsed() / THOUSAND);

		out.close();
	}

	private static String randomString(int problemSize)
	{
		String sample = "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "1234567890" + " ";

		Random randInt = new Random();

		String randomString = "";

		for (int i = 0; i < problemSize; i++)
		{
			randomString += sample.charAt(randInt.nextInt(sample.length()));
		}

		return randomString;
	}

	protected static class IntegerComp implements Comparator<Integer>
	{

		@Override
		public int compare(Integer o1, Integer o2)
		{
			return Integer.compare(o1, o2);
		}

	}
	
	protected static class order implements Comparator<String>
	{

		@Override
		public int compare(String o1, String o2)
		{
			if (AnagramUtil.areAnagrams(o1, o2))
			{
				return 0;
			}

			else if (AnagramUtil.sort(o1).compareTo(AnagramUtil.sort(o2)) < 0)
			{
				return -1;
			}

			else
			{
				return 1;
			}
		}

	}
}
