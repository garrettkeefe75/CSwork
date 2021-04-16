package assignment06;

import components.stopwatch.*;

import java.util.Random;

import components.list.List;
import components.list.ListOnArrays;
import components.simplewriter.*;

public class Timing
{

	final static int THOUSAND = 1000;

	final static int[] SIZES =
	{ 10, 100, 1000, 10000, 100000, 1000000 };

	public static void main(String[] args)
	{

		Random rand = new Random();

		SimpleWriter out = new SimpleWriter1L();

		out.println("------------Timing Experiment------------");
		out.println("--size;time--");

		List<Integer> listOnArray = new ListOnArrays<Integer>();
		List<Integer> doublyLinkedList = new A6DoublyLinkedList<Integer>();

		for (int i = 0; i < SIZES.length; i++)
		{
			listOnArray = new ListOnArrays<Integer>();
			doublyLinkedList = new A6DoublyLinkedList<Integer>();

			for (int j = 0; j < SIZES[i]; j++)
			{
				listOnArray.add(rand.nextInt(SIZES[i]));
				doublyLinkedList.add(rand.nextInt(SIZES[i]));
			}

			out.println("------Add Experiment Array: " + SIZES[i] + "------");
			out.println(SIZES[i] + ";" + (addExperiment(listOnArray, 0) + addExperiment(listOnArray, 0)
					+ addExperiment(listOnArray, 0) + addExperiment(listOnArray, 0) + addExperiment(listOnArray, 0)
					+ addExperiment(listOnArray, 0) + addExperiment(listOnArray, 0) + addExperiment(listOnArray, 0)
					+ addExperiment(listOnArray, 0) + addExperiment(listOnArray, 0)) / 10);

			out.println(SIZES[i] + ";"
					+ (addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)
							+ addExperiment(listOnArray, listOnArray.size() / 2)) / 10);

			out.println(SIZES[i] + ";" + (addEndExperiment(listOnArray) + addEndExperiment(listOnArray)
					+ addEndExperiment(listOnArray) + addEndExperiment(listOnArray) + addEndExperiment(listOnArray)
					+ addEndExperiment(listOnArray) + addEndExperiment(listOnArray) + addEndExperiment(listOnArray)
					+ addEndExperiment(listOnArray) + addEndExperiment(listOnArray)) / 10);

			out.println(SIZES[i] + ";"
					+ (addRandomExperiment(listOnArray, rand) + addRandomExperiment(listOnArray, rand)
							+ addRandomExperiment(listOnArray, rand) + addRandomExperiment(listOnArray, rand)
							+ addRandomExperiment(listOnArray, rand) + addRandomExperiment(listOnArray, rand)
							+ addRandomExperiment(listOnArray, rand) + addRandomExperiment(listOnArray, rand)
							+ addRandomExperiment(listOnArray, rand) + addRandomExperiment(listOnArray, rand)) / 10);

			out.println("------Add Experiment Linked: " + SIZES[i] + "------");
			out.println(SIZES[i] + ";"
					+ (addExperiment(doublyLinkedList, 0) + addExperiment(doublyLinkedList, 0)
							+ addExperiment(doublyLinkedList, 0) + addExperiment(doublyLinkedList, 0)
							+ addExperiment(doublyLinkedList, 0) + addExperiment(doublyLinkedList, 0)
							+ addExperiment(doublyLinkedList, 0) + addExperiment(doublyLinkedList, 0)
							+ addExperiment(doublyLinkedList, 0) + addExperiment(doublyLinkedList, 0)) / 10);

			out.println(SIZES[i] + ";"
					+ (addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)
							+ addExperiment(doublyLinkedList, listOnArray.size() / 2)) / 10);

			out.println(SIZES[i] + ";"
					+ (addEndExperiment(doublyLinkedList) + addEndExperiment(doublyLinkedList)
							+ addEndExperiment(doublyLinkedList) + addEndExperiment(doublyLinkedList)
							+ addEndExperiment(doublyLinkedList) + addEndExperiment(doublyLinkedList)
							+ addEndExperiment(doublyLinkedList) + addEndExperiment(doublyLinkedList)
							+ addEndExperiment(doublyLinkedList) + addEndExperiment(doublyLinkedList)) / 10);

			out.println(SIZES[i] + ";"
					+ (addRandomExperiment(doublyLinkedList, rand) + addRandomExperiment(doublyLinkedList, rand)
							+ addRandomExperiment(doublyLinkedList, rand) + addRandomExperiment(doublyLinkedList, rand)
							+ addRandomExperiment(doublyLinkedList, rand) + addRandomExperiment(doublyLinkedList, rand)
							+ addRandomExperiment(doublyLinkedList, rand) + addRandomExperiment(doublyLinkedList, rand)
							+ addRandomExperiment(doublyLinkedList, rand) + addRandomExperiment(doublyLinkedList, rand))
							/ 10);

			out.println("------Remove Experiment Array: " + SIZES[i] + "------");
			out.println(SIZES[i] + ";"
					+ (removeExperiment(listOnArray, 0) + removeExperiment(listOnArray, 0)
							+ removeExperiment(listOnArray, 0) + removeExperiment(listOnArray, 0)
							+ removeExperiment(listOnArray, 0) + removeExperiment(listOnArray, 0)
							+ removeExperiment(listOnArray, 0) + removeExperiment(listOnArray, 0)
							+ removeExperiment(listOnArray, 0) + removeExperiment(listOnArray, 0)) / 10);

			out.println(SIZES[i] + ";"
					+ (removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)
							+ removeExperiment(listOnArray, listOnArray.size() / 2)) / 10);

			out.println(SIZES[i] + ";"
					+ (removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)
							+ removeExperiment(listOnArray, listOnArray.size() - 1)) / 10);

			out.println(SIZES[i] + ";"
					+ (removeRandomExperiment(listOnArray, rand) + removeRandomExperiment(listOnArray, rand)
							+ removeRandomExperiment(listOnArray, rand) + removeRandomExperiment(listOnArray, rand)
							+ removeRandomExperiment(listOnArray, rand) + removeRandomExperiment(listOnArray, rand)
							+ removeRandomExperiment(listOnArray, rand) + removeRandomExperiment(listOnArray, rand)
							+ removeRandomExperiment(listOnArray, rand) + removeRandomExperiment(listOnArray, rand))
							/ 10);

			out.println("------Remove Experiment Linked: " + SIZES[i] + "------");
			out.println(SIZES[i] + ";"
					+ (removeExperiment(doublyLinkedList, 0) + removeExperiment(doublyLinkedList, 0)
							+ removeExperiment(doublyLinkedList, 0) + removeExperiment(doublyLinkedList, 0)
							+ removeExperiment(doublyLinkedList, 0) + removeExperiment(doublyLinkedList, 0)
							+ removeExperiment(doublyLinkedList, 0) + removeExperiment(doublyLinkedList, 0)
							+ removeExperiment(doublyLinkedList, 0) + removeExperiment(doublyLinkedList, 0)) / 10);

			out.println(SIZES[i] + ";"
					+ (removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() / 2)) / 10);

			out.println(SIZES[i] + ";"
					+ (removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)
							+ removeExperiment(doublyLinkedList, doublyLinkedList.size() - 1)) / 10);

			out.println(SIZES[i] + ";"
					+ (removeRandomExperiment(doublyLinkedList, rand) + removeRandomExperiment(doublyLinkedList, rand)
							+ removeRandomExperiment(doublyLinkedList, rand) + removeRandomExperiment(doublyLinkedList, rand)
							+ removeRandomExperiment(doublyLinkedList, rand) + removeRandomExperiment(doublyLinkedList, rand)
							+ removeRandomExperiment(doublyLinkedList, rand) + removeRandomExperiment(doublyLinkedList, rand)
							+ removeRandomExperiment(doublyLinkedList, rand) + removeRandomExperiment(doublyLinkedList, rand))
							/ 10);

			out.println("------Reverse Experiment Array: " + SIZES[i] + "------");
			if (SIZES[i] <= 10000)
				out.println(SIZES[i] + ";"
						+ (reverseExperiment(listOnArray) + reverseExperiment(listOnArray)
								+ reverseExperiment(listOnArray) + reverseExperiment(listOnArray)
								+ reverseExperiment(listOnArray) + reverseExperiment(listOnArray)
								+ reverseExperiment(listOnArray) + reverseExperiment(listOnArray)
								+ reverseExperiment(listOnArray) + reverseExperiment(listOnArray)) / 10);

			out.println("------Reverse Experiment Linked: " + SIZES[i] + "------");
			if (SIZES[i] <= 10000)
				out.println(SIZES[i] + ";" + (reverseExperiment(doublyLinkedList) + reverseExperiment(doublyLinkedList)
						+ reverseExperiment(doublyLinkedList) + reverseExperiment(doublyLinkedList)
						+ reverseExperiment(doublyLinkedList) + reverseExperiment(doublyLinkedList)
						+ reverseExperiment(doublyLinkedList) + reverseExperiment(doublyLinkedList)
						+ reverseExperiment(doublyLinkedList) + reverseExperiment(doublyLinkedList)) / 10);
		}

		out.close();

	}

	private static long addExperiment(List<Integer> list, int index)
	{
		Stopwatch timer = new Stopwatch1();

		timer.start();

		list.add(index, 100);

		timer.stop();
		
		list.remove(index);

		return timer.elapsed() / THOUSAND;
	}

	private static long addEndExperiment(List<Integer> list)
	{
		Stopwatch timer = new Stopwatch1();

		timer.start();

		list.add(100);

		timer.stop();
		
		list.remove(list.size() - 1);

		return timer.elapsed() / THOUSAND;
	}

	private static long addRandomExperiment(List<Integer> list, Random rand)
	{
		Stopwatch timer = new Stopwatch1();

		int index = rand.nextInt(list.size() - 1);
		
		timer.start();

		list.add(index, 100);

		timer.stop();
		
		list.remove(index);

		return timer.elapsed() / THOUSAND;
	}

	private static long removeExperiment(List<Integer> list, int index)
	{
		Stopwatch timer = new Stopwatch1();

		timer.start();

		Integer x = list.remove(index);

		timer.stop();

		list.add(index, x);

		return timer.elapsed() / THOUSAND;
	}

	private static long removeRandomExperiment(List<Integer> list, Random rand)
	{
		Stopwatch timer = new Stopwatch1();

		timer.start();

		int index = rand.nextInt(list.size() - 1);

		Integer x = list.remove(index);

		timer.stop();

		list.add(index, x);

		return timer.elapsed() / THOUSAND;
	}

	private static long reverseExperiment(List<Integer> list)
	{
		Stopwatch timer = new Stopwatch1();

		timer.start();

		list.reverse();

		timer.stop();

		return timer.elapsed() / THOUSAND;
	}
}
