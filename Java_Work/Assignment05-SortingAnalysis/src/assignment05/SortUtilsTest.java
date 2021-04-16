package assignment05;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class SortUtilsTest
{

	@Test
	public void testIsSorted()
	{
		int[] list = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		List<Integer> testList = new ArrayList<Integer>();
		
		for(int i = 0; i < list.length; i++)
			testList.add(list[i]);
		
		assertTrue(SortUtils.isSorted(testList));
	}
	
	@Test
	public void testSwapElements()
	{
		int[] list = {1, 2};
		List<Integer> testList = new ArrayList<Integer>();
		
		for(int i = 0; i < list.length; i++)
			testList.add(list[i]);		
		
		int[] list1 = {2, 1};
		List<Integer> testList1 = new ArrayList<Integer>();
		
		for(int i = 0; i < list1.length; i++)
			testList1.add(list1[i]);
		
		SortUtils.swapElementsAt(testList, 0, 1);
		
		assertEquals(testList1, testList);
	}
	
	@Test
	public void testListOfDuplicateInts()
	{
		int[] list = {2, 2, 2, 2, 2};
		List<Integer> expectedList = new ArrayList<Integer>();
		
		for(int i = 0; i < list.length; i++)
			expectedList.add(list[i]);
		
		assertEquals(SortUtils.listOfDuplicateInts(5, 2), expectedList);
	}

	@Test
	public void testListOfReverseSortedInts()
	{
		int[] list = {9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		List<Integer> expectedList = new ArrayList<Integer>();
		
		for(int i = 0; i < list.length; i++)
			expectedList.add(list[i]);
		
		assertEquals(SortUtils.listOfReversedSortedInts(10), expectedList);
	}
	
	@Test
	public void testListOfSortedInts()
	{
		int[] list = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
		List<Integer> expectedList = new ArrayList<Integer>();
		
		for(int i = 0; i < list.length; i++)
			expectedList.add(list[i]);
		
		assertEquals(SortUtils.listOfSortedInts(10), expectedList);
	}
	
	@Test
	public void testIsSortedFalse()
	{
		assertFalse(SortUtils.isSorted(SortUtils.listOfReversedSortedInts(11)));
	}
	
	@Test
	public void testIsSortedTrue()
	{
		assertTrue(SortUtils.isSorted(SortUtils.listOfSortedInts(11)));
	}
}
