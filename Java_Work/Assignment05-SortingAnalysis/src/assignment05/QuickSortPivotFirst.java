package assignment05;

import java.util.List;

/**
 * Implements in-place QuickSort using the {@code Sorter} interface by selecting
 * the first element in the list as the pivot.
 * 
 * This class must implement the {@code setThreshold, threshold} methods.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <T> type of the element of the list this sorter can sort
 */
public class QuickSortPivotFirst<T extends Comparable<? super T>> extends AbstractQuickSort<T>
{
	public QuickSortPivotFirst( )
	{
		this.name = "QuickSortPivotFirst";
		this.complexity = ComplexityClass.NLOGN;
		this.setThreshold(100);
		this.setSorter(new InsertionSort<T>());
	}

	@Override
	protected T pivot(List<T> list, int start, int end)
	{
		assert list != null : "Violation of: list is not null";
		int pivotIndex = pivotIndex(list, start, end);
		return list.get(pivotIndex);
	}

	@Override
	protected int pivotIndex(List<T> list, int start, int end)
	{
		return start;
	}
}
