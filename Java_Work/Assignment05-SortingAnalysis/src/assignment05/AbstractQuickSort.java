package assignment05;

import java.util.List;

/**
 * Implementation of common methods for all quick-sort variants.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <T> type of elements of the collection to be sorted
 */
public abstract class AbstractQuickSort<T extends Comparable<? super T>> extends AbstractSorter<T> implements Sorter<T>
{
	private InsertionSort<T> sort;

	private int threshold;

	protected void setSorter(InsertionSort<T> sort)
	{
		this.sort = sort;
	}

	/**
	 * Returns the pivot around which to quick-sort the list. This method may modify
	 * the {@code list}, e.g., median of three will move smallest value to front of
	 * the list.
	 * 
	 * @param    list  the list to find the pivot in
	 * @param    start start-index of the sublist to consider
	 * @param    end   end-index of the sublist to consider
	 * @return         pivot for quick-sort
	 * 
	 * @requires       start <= end
	 * 
	 * @modifies       {@code list}
	 */
	protected abstract T pivot(List<T> list, int start, int end);

	/**
	 * Returns the pivot index to be used in the pivot method
	 * 
	 * @param    list  the list to find the pivot index in
	 * @param    start start index of the list/sublist to consider
	 * @param    end   the end index of the list/sublist to consider
	 * @return         the pivot index for the quick-sort
	 * 
	 * @requires       start <= end
	 */
	protected abstract int pivotIndex(List<T> list, int start, int end);

	/**
	 * Determines a pivot and partitions the {@code list} between {@code left} and
	 * {@code right} such that all elements less than pivot are on its left and all
	 * elements greater than pivot are to its right.
	 * 
	 * @param    list  the list to be partitioned
	 * @param    left  start-index of the sublist to partition
	 * @param    right end-index of the sublist to partition
	 * @return         the location of the pivot in the resulting list
	 * 
	 * @modifies       {@code list}
	 */
	protected int partition(List<T> list, int left, int right)
	{

		assert list != null : "Violation of: list is not null";

		T pivot = pivot(list, left, right);

		int pivotIndex = left;

		left++;

		while (left <= right)
		{
			while (left <= right && list.get(left).compareTo(pivot) <= 0)
				left++;

			while (left <= right && list.get(right).compareTo(pivot) > 0)
				right--;
			if (left <= right)
				SortUtils.swapElementsAt(list, left, right);
		}

		SortUtils.swapElementsAt(list, pivotIndex, right);

		return right;
	}

	/**
	 * Sorts the virtual {@code list} between indexes {@code left} and {@code right}
	 * using the Quick-Sort algorithm.
	 * 
	 * @param    list  the list to be sorted
	 * @param    left  start-index of the sublist to be sorted
	 * @param    right end-index of the sublist to be sorted
	 * 
	 * @modifies       {@code list}
	 */
	protected void quickSort(List<T> list, int left, int right)
	{
		assert list != null : "Violation of: list is not null";

		if (left < right)
		{
			if (list.size() <= threshold)
			{
				this.sort.sort(list);
			}

			else
			{
				int pivotIndex = partition(list, left, right);
				quickSort(list, left, pivotIndex - 1);
				quickSort(list, pivotIndex + 1, right);
			}

		}

	}

	@Override
	public void sort(List<T> list)
	{
		assert list != null : "Violation of: list is not null";

		quickSort(list, 0, list.size() - 1);
	}

	@Override
	public void setThreshold(int threshold) throws UnsupportedOperationException
	{
		this.threshold = threshold;
	}

	@Override
	public int threshold() throws UnsupportedOperationException
	{
		return this.threshold();
	}

}
