package assignment05;

import java.util.List;

/**
 * Implements insertion sort using the {@code Sorter} interface.
 * 
 * This class does not implement the {@code setThreshold} method.
 * 
 * @author     David Harrington && Garrett Keefe
 *
 * @param  <T> type of the element of the list this sorter can sort
 */
public class InsertionSort<T extends Comparable<? super T>> extends AbstractSorter<T>
{
	public InsertionSort( )
	{
		this.name = "InsertionSort";
		this.complexity = ComplexityClass.NSQUARED;
	}

	@Override
	public void sort(List<T> list)
	{
		assert list != null : "Violation of: list is not null";

		for (int i = 1; i < list.size(); i++)
		{
			T currentElement = list.get(i);

			int j = i - 1;

			while (j >= 0 && list.get(j).compareTo(currentElement) > 0)
			{
				list.set(j + 1, list.get(j));
				j -= 1;
			}

			list.set(j + 1, currentElement);
		}
	}
}
