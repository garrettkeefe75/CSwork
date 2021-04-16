package assignment05;

public class SortAnalysis
{

	public static void main(String[] args)
	{
		// test MergeSort
		SortUtils.generateTimingReport(new MergeSort<Integer>(), 500, 500, 50000, 10);

		// test QuickSort Random
		SortUtils.generateTimingReport(new QuickSortPivotRandom<Integer>(), 500, 500, 50000, 10);
	}

}
