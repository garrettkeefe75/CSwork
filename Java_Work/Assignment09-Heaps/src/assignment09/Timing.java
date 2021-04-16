package assignment09;

import components.stopwatch.*;
import components.simplewriter.*;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class Timing
{

	private static final int[] SIZES =
	{ 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000 };

	private static final int THOUSAND = 1000;

	public static void main(String[] args)
	{
		Stopwatch timer = new Stopwatch1();
		SimpleWriter out = new SimpleWriter1L();
		addExperiment(timer, out);
		removeExperiment(timer, out);
		sortExperiment(timer, out);
	}

	private static void addExperiment(Stopwatch timer, SimpleWriter out)
	{
		Heap<Integer> heap;

		out.println("Add Experiment");
		out.println("Problem Size;Elapsed Time");
		for (int i = 0; i < SIZES.length; i++)
		{
			heap = generateHeap(SIZES[i]);
			timer.reset();
			timer.start();
			heap.add(SIZES[i] + 1);
			timer.stop();

			out.println(SIZES[i] + ";" + timer.elapsed() / THOUSAND);
		}
	}

	private static void removeExperiment(Stopwatch timer, SimpleWriter out)
	{
		Heap<Integer> heap;

		out.println("Remove Experiment");
		out.println("Problem Size;Elapsed Time");
		for (int i = 0; i < SIZES.length; i++)
		{
			heap = generateHeap(SIZES[i]);
			timer.reset();
			timer.start();
			heap.removeFirst();
			timer.stop();

			out.println(SIZES[i] + ";" + timer.elapsed() / THOUSAND);
		}
	}
	
	private static void sortExperiment(Stopwatch timer, SimpleWriter out)
	{
		Heap<Integer> heap;

		out.println("Sort Experiment");
		out.println("Problem Size;Elapsed Time");
		for (int i = 0; i < SIZES.length; i++)
		{
			heap = generateHeap(SIZES[i]);
			timer.reset();
			timer.start();
			heap.sort();
			timer.stop();

			out.println(SIZES[i] + ";" + timer.elapsed() / THOUSAND);
		}
	}

	private static Heap<Integer> generateHeap(int size)
	{
		Heap<Integer> heap = new Heap<Integer>();

		for (int i = size; i > 0; i--)
			heap.add(i);

		return heap;
	}

}
