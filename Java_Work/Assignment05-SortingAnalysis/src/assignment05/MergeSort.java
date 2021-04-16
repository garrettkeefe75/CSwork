package assignment05;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the merge sort using the {@code Sorter} interface.
 * 
 * This class must implement the {@code setThreshold, threshold} methods.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <T> type of the element of the list this sorter can sort
 */
public class MergeSort<T extends Comparable<? super T>> extends AbstractSorter<T>
{
	private InsertionSort<T> sort;

	private int threshold;

	@Override
	public void setThreshold(int threshold) throws UnsupportedOperationException
	{
		this.threshold = threshold;
	}

	@Override
	public int threshold() throws UnsupportedOperationException
	{
		return this.threshold;
	}

	public MergeSort( )
	{
		this.name = "MergeSort";
		this.complexity = ComplexityClass.NLOGN;
		this.sort = new InsertionSort<T>();
		this.threshold = 1000;
	}

	@Override
	public void sort(List<T> list)
	{
		assert list != null : "Violation of: list is not null";
		mergeSort(list, 0, list.size() - 1);
	}

	public void mergeSort(List<T> list, int start, int end)
	{
		// if right - left < 100
		// run insertionSort on that list

		if (end > start)
		{
			if (end - start < this.threshold)
			{
				List<T> tempList = list.subList(start, end + 1);
				sort.sort(tempList);

				int i = 0;
				for (int j = start; j <= end; j++)
				{
					list.set(j, tempList.get(i));
					i++;
				}
			}
			
			else
			{
				int midpoint = (end - start) / 2 + start;
				mergeSort(list, start, midpoint);
				mergeSort(list, midpoint + 1, end);
				merge(list, start, midpoint + 1, end);
			}
		}

	}

	/**
	 * 
	 * @param list
	 * @param leftStart
	 * @param rightStart
	 * @param rightEnd
	 */
	public void merge(List<T> list, int leftStart, int rightStart, int rightEnd)
	{
		List<T> result = new ArrayList<T>();

		int leftPointer = leftStart;
		int leftEnd = rightStart - 1;
		int rightPointer = rightStart;

		while (leftPointer <= leftEnd && rightPointer <= rightEnd)
		{
			if (list.get(leftPointer).compareTo(list.get(rightPointer)) < 0)
			{
				result.add(list.get(leftPointer));
				leftPointer++;
			}

			else
			{
				result.add(list.get(rightPointer));
				rightPointer++;
			}
		}

		while (leftPointer <= leftEnd)
		{
			result.add(list.get(leftPointer));
			leftPointer++;
		}

		while (rightPointer <= rightEnd)
		{
			result.add(list.get(rightPointer));
			rightPointer++;
		}

		int j = 0;
		for (int i = leftStart; i <= rightEnd; i++)
		{

			list.set(i, result.get(j));
			j++;
		}
	}

}
