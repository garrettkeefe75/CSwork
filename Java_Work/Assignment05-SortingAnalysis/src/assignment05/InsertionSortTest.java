package assignment05;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class InsertionSortTest extends SorterTest
{

	@Override
	protected Sorter<Integer> uutInt()
	{
		return new InsertionSort<Integer>();
	}

	@Override
	protected Sorter<String> uutString()
	{
		return new InsertionSort<String>();
	}

}
