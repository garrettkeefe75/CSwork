package assignment05;

import java.util.List;
import java.util.Random;

/**
 * Implements in-place QuickSort using the {@code Sorter} interface by selecting
 * a random element in the list as the pivot.
 * 
 * This class must implement the {@code setThreshold, threshold} methods.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <T> type of the element of the list this sorter can sort
 */
public class QuickSortPivotRandom<T extends Comparable<? super T>> extends AbstractQuickSort<T>
{
	public QuickSortPivotRandom( )
	{
		this.name = "QuickSortPivotRandom";
		this.complexity = ComplexityClass.NLOGN;
		this.setThreshold(1000);
		this.setSorter(new InsertionSort<T>());
	}

	@Override
	protected T pivot(List<T> list, int start, int end)
	{
		assert list != null : "Violation of: list is not null";

		int pivotIndex = pivotIndex(list, start, end);
		T pivot = list.get(pivotIndex);
		SortUtils.swapElementsAt(list, start, pivotIndex);

		return pivot;
	}

	@Override
	protected int pivotIndex(List<T> list, int start, int end)
	{
		return new Random().nextInt((end - start) + 1) + start;
	}

}