package assignment05;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class MergeSortTest extends SorterTest
{

	@Override
	protected Sorter<Integer> uutInt()
	{
		return new MergeSort<Integer>();
	}

	@Override
	protected Sorter<String> uutString()
	{
		return new MergeSort<String>();
	}

}
