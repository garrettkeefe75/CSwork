package assignment03;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test cases for public methods of the {@code SudokuByElimination} class.
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class SudokuByEliminationTest
{
	// default puzzle
	private SudokuByElimination s1;

	// a difficult puzzle
	private SudokuByElimination s2;

	private SudokuByElimination s2Solution;

	// a very difficult puzzle
	private SudokuByElimination s3;

	private SudokuByElimination s3Solution;

	@Before // executed before every test case
	public void setUp()
	{
		s1 = new SudokuByElimination("data/sudoku1.txt");
		//s1Solution = new SudokuBacktrackRecursive();
		
		s2 = new SudokuByElimination("data/Sudoku5.txt");
		s2Solution = new SudokuByElimination("data/Sudoku5Solution.txt");
		
		s3 = new SudokuByElimination("data/Sudoku6.txt");
		s3Solution = new SudokuByElimination("data/Sudoku6Solution.txt");
	}

	// solve methods

	//

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
	
	@Test
	public void testSolve2()
	{
		s2.solve();

		assertEquals(s2.toString(), s2Solution.toString());
	}
	
	@Test
	public void testVerify2()
	{	
		assertTrue(s2Solution.verify());
	}

	@Test
	public void testSolve3()
	{
		s3.solve();

		assertEquals(s3.toString(), s3Solution.toString());
	}
	
	@Test
	public void testVerify3()
	{
		assertTrue(s3Solution.verify());
	}

}
