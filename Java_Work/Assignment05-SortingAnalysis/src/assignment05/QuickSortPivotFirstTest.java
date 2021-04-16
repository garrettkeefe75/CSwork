package assignment05;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class QuickSortPivotFirstTest extends SorterTest
{
	@Override
	protected Sorter<Integer> uutInt()
	{
		return new QuickSortPivotFirst<Integer>();
	}

	@Override
	protected Sorter<String> uutString()
	{
		return new QuickSortPivotFirst<String>();
	}
}
