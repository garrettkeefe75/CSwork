package assignment03;

import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;
import components.stopwatch.Stopwatch;
import components.stopwatch.Stopwatch1;

public class SudokuPerformance
{
	
	/**
	 * Timer to calculate time elapsed between two points in the program.
	 */
	private static Stopwatch timer;

	/**
	 * A constant for 1000, used to convert nanoseconds to microseconds.
	 */
	private final static int THOUSAND = 1000;
	
	

	public static void main(String[] args)
	{	
		RecursivePerformance(new SudokuBacktrackRecursive("data/sudoku1.txt"));
		RecursivePerformance(new SudokuBacktrackRecursive("data/Sudoku2"));
		RecursivePerformance(new SudokuBacktrackRecursive("data/Sudoku3"));
		RecursivePerformance(new SudokuBacktrackRecursive("data/Sudoku5.txt"));
		
		System.out.println();
		
		ConstraintPerformance(new SudokuByElimination("data/sudoku1.txt"));
		ConstraintPerformance(new SudokuByElimination("data/Sudoku5.txt"));
		ConstraintPerformance(new SudokuByElimination("data/Sudoku6.txt"));
		ConstraintPerformance(new SudokuByElimination("data/Sudoku7.txt"));
	}
	
	private static void RecursivePerformance(SudokuBacktrackRecursive Sudoku)
	{	
		SimpleWriter out = new SimpleWriter1L();
		
		timer = new Stopwatch1();
		
		timer.reset();
		
		timer.start();
		
		Sudoku.solve();
		
		timer.stop();
		
		out.println(timer.elapsed() / THOUSAND + " " + Sudoku.getGuesses());
		
		out.close();
	}
	
	private static void ConstraintPerformance(SudokuByElimination Sudoku)
	{
		SimpleWriter out = new SimpleWriter1L();
		
		timer = new Stopwatch1();
		
		timer.reset();
		
		timer.start();
		
		Sudoku.solve();
		
		timer.stop();
		
		out.println(timer.elapsed() / THOUSAND + " " + Sudoku.getEliminations());
		
		out.close();
	}

}
