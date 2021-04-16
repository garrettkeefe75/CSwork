package assignment03;

import java.util.ArrayList;

import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Iterative implementation of the sudoku solver.
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class SudokuByElimination implements Sudoku
{
	/**
	 * Each element is represented as a set of integers {1,..,9}
	 */
	private ArrayList<Set<Integer>> data;
	
	
	/**
	 * Counts the number of eliminations to solve;
	 */
	private int eliminations;

	protected ArrayList<Set<Integer>> getData()
	{
		return this.data;
	}
	
	/**
	 * Returns the number of eliminations to solve
	 * the puzzle. 
	 * 
	 * @return Returns the number of eliminations 
	 * made to solve the sudoku puzzle
	 */
	protected int getEliminations()
	{
		return this.eliminations;
	}

	/**
	 * Creates a new puzzle by reading a file.
	 *
	 * @param    filename Relative path of the file containing the puzzle in the
	 *                    given format
	 * @requires          The file must be 9 rows of 9 numbers separated by
	 *                    whitespace the numbers should be 1-9 or 0 representing an
	 *                    empty square
	 */
	public SudokuByElimination(String filename)
	{	
		SimpleReader file = new SimpleReader1L(filename);
		
		this.eliminations = 0;

		this.data = new ArrayList<Set<Integer>>();

		for (int row = 0; row < Sudoku.PUZZLE_HEIGHT_WIDTH; row++)
		{
			String k = file.nextLine();

			char[] kChar = k.toCharArray();

			int[] kInt = new int[9];

			for (int i = 0; i < Sudoku.PUZZLE_HEIGHT_WIDTH; i++)
			{
				kInt[i] = Character.getNumericValue(kChar[i]);
			}

			for (int i = 0; i < Sudoku.PUZZLE_HEIGHT_WIDTH; i++)
			{
				if (kInt[i] == 0)
				{
					Set<Integer> set = new Set1L<Integer>();
					set.add(1);
					set.add(2);
					set.add(3);
					set.add(4);
					set.add(5);
					set.add(6);
					set.add(7);
					set.add(8);
					set.add(9);

					this.data.add(set);
				}

				else
				{
					Set<Integer> set = new Set1L<Integer>();
					set.add(kInt[i]);
					this.data.add(set);
				}
			}
		}

		file.close();
	}

	/**
	 * Eliminates the {@code element} from {@code row}, {@code col}, and Box
	 * containing position (row,column).
	 * 
	 * @param    row     row index
	 * @param    col     column index
	 * @param    element to be eliminated
	 * 
	 * @requires         [{@code row} and {@code column} are valid indices]
	 */
	public void eliminate(int row, int col, int element)
	{
		assert 0 <= row && row < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= col && col < PUZZLE_HEIGHT_WIDTH : "Violation of valid col index";

		// row elimination
		// --------------------------------------------------
		for (int r = row * 9; r < (row * 9) + 9; r++)
		{
			if (this.data.get(r).size() != 1 && 
					this.data.get(r).contains(element))
			{
				this.data.get(r).remove(element);
			}
		}

		// column elimination
		// ---------------------------------------------------
		for (int c = col; c < 81; c += 9)
		{
			if (this.data.get(c).size() != 1 && 
				this.data.get(c).contains(element))
			{
				this.data.get(c).remove(element);
			}

		}

		// box elimination
		// ---------------------------------------------------

		// rows
		for (int br = (row / 3) * 3; br < ((row / 3) * 3) + 3; br++)
		{
			// columns
			for (int bc = (col / 3) * 3; bc < ((col / 3) * 3) + 3; bc++)
			{
				if (this.data.get((br * 9) + bc).size() != 1 && 
						this.data.get((br * 9) + bc).contains(element))
				{
					
					this.data.get((br * 9) + bc).remove(element);
				}
			}
		}
		
	}

	@Override
	public int element(int row, int col)
	{
		assert 0 <= row && row < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= col && col < PUZZLE_HEIGHT_WIDTH : "Violation of valid col index";

		int returnInteger = 0;

		// extracts the integer from the set
		for (Integer i : this.data.get((row * 9) + col))
		{
			returnInteger = i;
		}

		return returnInteger;
	}

	@Override
	public void setElement(int row, int col, int number)
	{
		// checks for valid column and row id
		assert 0 <= row && row < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= col && col < PUZZLE_HEIGHT_WIDTH : "Violation of valid col index";
		assert 0 <= number && number <= PUZZLE_HEIGHT_WIDTH : "Violation of valid number";

		// creates a new set to replace the old one with
		Set<Integer> set = new Set1L<Integer>();
		set.add(number);

		this.data.set((row * 9) + col, set);
	}

	@Override
	public boolean solve()
	{
		while (verify() == false)
		{

			for (int i = 0; i < 81; i++)
			{
				if (this.data.get(i).size() == 1)
				{
					int row = i / 9;
					int col = i % 9;

					this.eliminations += 1;
					
					eliminate(row, col, element(row, col));

				}
			}
		}

		return true;
	}

	@Override
	public boolean verify()
	{
		// iterates through the arraylist
		for (int i = 0; i < 81; i++)
		{
			// checks the size of each set
			if (this.data.get(i).size() != 1)
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString()
	{
		String result = "";
		for (int row = 0; row < 9; row++)
		{
			for (int col = 0; col < 9; col++)
			{
				if(this.data.get((row * 9) + col).size() > 1)
				{
					result += " |";
				}
				else
				{
					result += element(row, col) + "|";
				}
			}
			
			result += "\n";

			if ((row + 1) % 3 == 0)
			{
				result += "-----+-----+-----+\n";
			}
			
		}

		return result;
	}

	/**
	 * Main method, similar to the one in the other class.
	 * 
	 * @param args command line arguments, not used
	 */
	public static void main(String[] args)
	{
		SimpleWriter out = new SimpleWriter1L();
		Sudoku s = new SudokuByElimination("data/Sudoku2");

		out.println("===== Sudoku puzzle =====\n");
		out.println(s.toString());

		out.println("\n\n===== Solving sudoku =====\n\n");
		s.solve();
		out.println(s);

		out.close();
	}
}
