package assignment07;

import java.util.LinkedList;
import java.util.Random;

import components.simplewriter.*;
import components.stopwatch.*;

public class Timing
{

	final static int THOUSAND = 1000;

	final static int[] SIZES =
	{ 1, 10, 100, 1000, 10000, 100000, 1000000 };

	public static void main(String[] args)
	{
		SimpleWriter out = new SimpleWriter1L();

		out.println("---Ordered Tree Building Experiment---");
		out.println("Problem Size;Time");
		for (int i = 0; i < SIZES.length; i++)
		{
			buildAndContainsExperiment(SIZES[i], out);
		}

		out.println("---Random Tree Building Experiment---");
		out.println("Problem Size;Time");
		for (int i = 0; i < SIZES.length; i++)
		{
			Timing.buildAndContainsRandomExperiment(SIZES[i], out);
		}
		
		out.close();

	}

	private static LinkedList<String> buildList(int size)
	{
		LinkedList<String> list = new LinkedList<String>();

		for (int i = 0; i < size; i++)
			list.add("" + i);

		return list;

	}

	private static LinkedList<String> buildRandomList(int size, Random rand)
	{
		LinkedList<String> list = new LinkedList<String>();

		while (list.size() < size)
		{
			int i = rand.nextInt();
			if (!list.contains("" + i))
				list.add("" + i);
		}

		return list;
	}

	private static void buildAndContainsExperiment(int size, SimpleWriter out)
	{
		// -------------------------------------------
		// Setup
		// -------------------------------------------
		Stopwatch timer = new Stopwatch1();

		StringBuilder string = new StringBuilder();

		string.append(size + ";");

		LinkedList<String> list = buildList(size);

		//BalancedBST1<String> tree = new BalancedBST1<String>();
		BinarySearchTreeOfStrings tree = new BinarySearchTreeOfStrings();
		
		// -------------------------------------------
		// Insert experiment
		// -------------------------------------------
		timer.start();

		for (int i = 0; i < list.size(); i++)
			tree.insert(list.remove());

		timer.stop();

		string.append(timer.elapsed() / THOUSAND + ";");

		timer.reset();

		// -------------------------------------------
		// Contains experiment
		// -------------------------------------------
		timer.start();

		for (int i = 0; i < list.size(); i++)
			tree.contains(list.remove());

		timer.stop();

		string.append(timer.elapsed() / THOUSAND);

		out.println(string.toString());

	}

	private static void buildAndContainsRandomExperiment(int size, SimpleWriter out)
	{
		Stopwatch timer = new Stopwatch1();
				
		StringBuilder string = new StringBuilder();
		string.append(size + ";");
		
		long time = 0;
				
		for(int i = 0; i < 5; i++)
		{
			time += randomBuildExperiment(size, timer);
		}
		
		time /= 5;
		
		string.append(time + ";");
		
		time = 0;
		
		for(int i = 0; i < 5; i++)
		{
			time += randomContainsExperiment(size, timer);
		}
		
		time /= 5;
		
		string.append(time + ";");
		
		out.println(string.toString());

	}

	private static long randomBuildExperiment(int size, Stopwatch timer)
	{
		timer.reset();
		LinkedList<String> list = Timing.buildRandomList(size, new Random());
	//	BalancedBST1<String> tree = new BalancedBST1<String>();
		BinarySearchTreeOfStrings tree = new BinarySearchTreeOfStrings();

		//setup
		
		timer.start();
		
		for(int i = 0; i < list.size(); i++)
		{
			tree.insert(list.remove());
		}
		
		timer.stop();
		return timer.elapsed();
	}

	private static long randomContainsExperiment(int size, Stopwatch timer)
	{
		timer.reset();
		
		LinkedList<String> list = Timing.buildRandomList(size, new Random());
		LinkedList<String> copy = list;
		//BalancedBST1<String> tree = new BalancedBST1<String>();
		BinarySearchTreeOfStrings tree = new BinarySearchTreeOfStrings();

		for(int i = 0; i < list.size(); i++)
		{
			tree.insert(list.remove());
		}
		
		for(int i = 0; i < list.size(); i++)
			tree.insert(list.remove(0));
		
		timer.start();


		for (int i = 0; i < copy.size(); i++)
		{
			tree.contains(copy.remove());
		}

		timer.stop();

		return timer.elapsed() / THOUSAND;
	}

}
