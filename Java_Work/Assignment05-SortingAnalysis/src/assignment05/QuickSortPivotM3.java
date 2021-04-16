package assignment05;

import java.util.List;

/**
 * Implements in-place QuickSort using the {@code Sorter} interface by selecting
 * the "median of three" as the pivot.
 * 
 * This class must implement the {@code setThreshold, threshold} methods.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <T> type of the element of the list this sorter can sort
 */
public class QuickSortPivotM3<T extends Comparable<? super T>> extends AbstractQuickSort<T>
{

	public QuickSortPivotM3( )
	{
		this.name = "QuickSortPivotM3";
		this.complexity = ComplexityClass.NLOGN;
		this.setThreshold(100);
		this.setSorter(new InsertionSort<T>());
	}

	@Override
	protected T pivot(List<T> list, int start, int end)
	{
		assert list != null : "Violation of: list is not null";

		int medianIndex = pivotIndex(list, start, end);

		T median = list.get(medianIndex);

		SortUtils.swapElementsAt(list, start, medianIndex);

		return median;
	}

	@Override
	protected int pivotIndex(List<T> list, int start, int end)
	{
		T startT = list.get(start);

		T midT = list.get(end / 2);

		T endT = list.get(end);

		// if start < mid < end || end < mid < start
		// return mid
		if (startT.compareTo(midT) < 0 && midT.compareTo(endT) < 0
				|| endT.compareTo(midT) < 0 && midT.compareTo(startT) < 0)
			return end / 2;

		// if mid < start < end || end < start < mid
		// return start
		else if (midT.compareTo(startT) < 0 && startT.compareTo(endT) < 0
				|| endT.compareTo(startT) < 0 && startT.compareTo(endT) < 0)
			return start;

		// if start < end < mid || mid < end < start
		// return end
		else
		{
			return end;
		}

	}

}
