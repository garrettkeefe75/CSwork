package assignment05;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class QuickSortPivotM3Test extends SorterTest
{
	@Override
	protected Sorter<Integer> uutInt()
	{
		return new QuickSortPivotM3<Integer>();
	}

	@Override
	protected Sorter<String> uutString()
	{
		return new QuickSortPivotM3<String>();
	}
}
