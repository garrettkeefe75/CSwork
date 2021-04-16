package assignment05;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class QuickSortPivotRandomTest extends SorterTest
{
	@Override
	protected Sorter<Integer> uutInt()
	{
		return new QuickSortPivotRandom<Integer>();
	}

	@Override
	protected Sorter<String> uutString()
	{
		return new QuickSortPivotRandom<String>();
	}
}
