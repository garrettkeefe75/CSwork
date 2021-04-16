package assignment03;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test cases for public methods of the {@code SudokuBacktrackRecursive}
 * class.
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class SudokuBacktrackRecursiveTest
{
	// default puzzle
	private SudokuBacktrackRecursive s1;
	
	// a difficult puzzle
	private SudokuBacktrackRecursive s2;

	private SudokuBacktrackRecursive s2Solution;

	// a very difficult puzzle
	private SudokuBacktrackRecursive s3;
	
	private SudokuBacktrackRecursive s3Solution;

	// executed before every test case
	@Before
	public void setUp()
	{
		s1 = new SudokuBacktrackRecursive("data/sudoku1.txt");
		//s1Solution = new SudokuBacktrackRecursive();
		
		s2 = new SudokuBacktrackRecursive("data/Sudoku2");
		s2Solution = new SudokuBacktrackRecursive("data/Sudoku2Solution");
		
		s3 = new SudokuBacktrackRecursive("data/Sudoku3");
		s3Solution = new SudokuBacktrackRecursive("data/Sudoku3Solution");
	}

	// Testing SudokuBacktrackRecursive.isValid()
	// ---------------------------------------

	@Test
	public void number4IsValidAtRow0Col0()
	{
		final int number = 4;
		assertEquals(true, s1.isValidForRow(0, number));
		assertEquals(true, s1.isValidForColumn(0, number));
		assertEquals(true, s1.isValidForBox(0, 0, number));
	}

	@Test
	public void numberIsValidAt_1_1()
	{
		int number = 5;
		assertFalse(s1.isValidForRow(1, number));
		assertTrue(s1.isValidForColumn(1, number));
		assertTrue(s1.isValidForBox(0, 0, number));
	}

	// Testing Sudoku.element()
	// ---------------------------------------

	@Test
	public void testElement0()
	{
		assertEquals(s2Solution.element(3, 5), 7);
	}

	@Test
	public void testElement1()
	{
		assertEquals(s3Solution.element(3, 5), 8);
	}

	// Testing Sudoku.verify()
	// ---------------------------------------

	@Test
	public void testVerify1()
	{
		
		assertTrue(s3Solution.verify());
	}

	@Test
	public void testVerify2()
	{
		System.out.println(s2Solution.verify());
		
		assertTrue(s2Solution.verify());
	}

	// Test Sudoku.solve()
	// ---------------------------------------


	@Test
	public void testSolve1()
	{
		s2.solve();

		assertEquals(s2.toString(), s2Solution.toString());
	}

	@Test
	public void testSolve2()
	{
		s3.solve();

		assertEquals(s3.toString(), s3Solution.toString());
	}

	// testing SudokuBacktrackRecursive.toString()
	// ---------------------------------------

	@Test
	public void testToString()
	{
		assertEquals(" | |3| |2| |6| | |\n" + //
				"9| | |3| |5| | |1|\n" + //
				" | |1|8| |6|4| | |\n" + //
				"-----+-----+-----+\n" + //
				" | |8|1| |2|9| | |\n" + //
				"7| | | | | | | |8|\n" + //
				" | |6|7| |8|2| | |\n" + //
				"-----+-----+-----+\n" + //
				" | |2|6| |9|5| | |\n" + //
				"8| | |2| |3| | |9|\n" + //
				" | |5| |1| |3| | |\n" + //
				"-----+-----+-----+\n", s1.toString());
	}

}
