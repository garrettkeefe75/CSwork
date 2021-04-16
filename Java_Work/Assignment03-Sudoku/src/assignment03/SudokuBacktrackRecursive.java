package assignment03;

import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * Brute force, recursive implementation of the sudoku solver.
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class SudokuBacktrackRecursive implements Sudoku
{

	private int[][] data;
	
	private int guesses;
	
	/**
	 * Returns the number of guesses taken to solve the sudoku puzzle. 
	 * @return returns the number of guesses taken to solve the puzzle
	 */
	protected int getGuesses()
	{
		return this.guesses;
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
	public SudokuBacktrackRecursive(String filename)
	{
		this.guesses = 0;
		
		SimpleReader file = new SimpleReader1L(filename);

		this.data = new int[Sudoku.PUZZLE_HEIGHT_WIDTH][Sudoku.PUZZLE_HEIGHT_WIDTH];

		// columns
		for (int i = 0; i < Sudoku.PUZZLE_HEIGHT_WIDTH; i++)
		{
			String k = file.nextLine();
			char[] kChar = k.toCharArray();

			// rows
			for (int j = 0; j < Sudoku.PUZZLE_HEIGHT_WIDTH; j++)
			{
				this.data[i][j] = Character.getNumericValue(kChar[j]);
			}
		}

		file.close();
	}

	/**
	 * Determines whether the given {@code number} can be placed in the given
	 * {@code row} without violating the rules of sudoku.
	 *
	 * @param    row    which row to see if the number can go into
	 * @param    number the number of interest
	 *
	 * @requires        [{@code row} is a valid row index] and [{@code number} is a
	 *                  valid digit]
	 * 
	 * @return          true iff it is possible to place that number in the row
	 *                  without violating the rule of 1 unique number per row.
	 */
	public boolean isValidForRow(int row, int number)
	{
		assert 0 < number && number <= PUZZLE_HEIGHT_WIDTH : "Violation of valid cadidate";
		assert 0 <= row && row < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";

		for (int i = 0; i < Sudoku.PUZZLE_HEIGHT_WIDTH; i++)
		{
			if (this.data[row][i] == number)
			{
				return false;
			}

		}

		return true;
	}

	/**
	 * Determines whether the given {@code number} can be placed in the given column
	 * without violating the rules of sudoku.
	 *
	 * @param    col    which column to see if the number can go into
	 * @param    number the number of interest
	 *
	 * @requires        [{@code col} is a valid column index] and [{@code number} is
	 *                  a valid digit]
	 * 
	 * @return          true if it is possible to place that number in the column
	 *                  without violating the rule of 1 unique number per row.
	 */
	public boolean isValidForColumn(int col, int number)
	{
		assert 0 <= col && col < PUZZLE_HEIGHT_WIDTH : "Violation of valid column index";
		assert 0 < number && number <= PUZZLE_HEIGHT_WIDTH : "Violation of valid cadidate";

		for (int i = 0; i < Sudoku.PUZZLE_HEIGHT_WIDTH; i++)
		{
			if (this.data[i][col] == number )
			{
				return false;
			}

		}

		return true;
	}

	/**
	 * Determines whether the given {@code number} can be placed in "box" starting
	 * at the given position without violating the rules of sudoku.
	 * 
	 * The positions marked # are the valid positions of start of a box. They are
	 * (0,0), (0,3), (0, 6), (3,0), (3,3), (3,6), (6,0), (6,3), (6,6).
	 * 
	 * <pre>
	 * #00|#00|#00|
	 * 000|000|000|
	 * 000|000|000|
	 * ---+---+---+
	 * #00|#00|#00|
	 * 000|000|000|
	 * 000|000|000|
	 * ---+---+---+
	 * #00|#00|#00|
	 * 000|000|000|
	 * 000|000|000|
	 * ---+---+---+
	 * </pre>
	 *
	 * @param    boxStartRow row index at which the box of interest starts
	 * @param    boxStartCol column index at which the box of interest starts
	 * @param    number      the number of interest
	 *
	 * @requires             [{@code boxStartRow} and {@code boxStartCol} are valid
	 *                       box start indices] and [{@code number} is a valid
	 *                       digit]
	 *
	 * @return               true iff it is possible to place that number in the
	 *                       column without violating the rule of 1 unique number
	 *                       per row.
	 */
	public boolean isValidForBox(int boxStartRow, int boxStartCol, int number)
	{
//		assert 0 < number && number <= PUZZLE_HEIGHT_WIDTH : "Violation of valid cadidate";
//		assert boxStartRow % BOX_HEIGHT_WIDTH == 0 : "Violation of valid boxStartRow";
//		assert boxStartCol % BOX_HEIGHT_WIDTH == 0 : "Violation of valid boxStartCol";

		// sets up each box with the remainder of each index
		int r = boxStartRow - boxStartRow % 3;
		int c = boxStartCol - boxStartCol % 3;

		for (int i = r; i < r + 3; i++)
		{
			for (int j = c; j < c + 3; j++)
			{
				if (this.data[i][j] == number && boxStartRow != i && boxStartCol != j)
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Determines whether the given {@code number} can be placed in the given
	 * position without violating the rules of sudoku.
	 *
	 * @param    row    which row to see if the number can go into
	 * @param    col    which column to see if the number can go into
	 * @param    number the number of interest
	 *
	 * @requires        [{@code row} is a valid row index] and [{@code col} is a
	 *                  valid column index] and [{@code number} is a valid digit]
	 * 
	 * @return          true iff it is possible to place that number in the column
	 *                  without violating the rule of 1 unique number per row.
	 */
	public boolean isValidForPosition(int row, int col, int number)
	{
		assert 0 <= row && row < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= col && col < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= number && number <= PUZZLE_HEIGHT_WIDTH : "Violation of valid cadidate";

		return this.isValidForBox(row, col, number) && this.isValidForColumn(col, number)
				&& this.isValidForRow(row, number);
	}

	/*
	 * Methods from sudoku interface implemented here
	 */

	@Override
	public int element(int i, int j)
	{
		assert 0 <= i && i < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= j && j < PUZZLE_HEIGHT_WIDTH : "Violation of valid column index";

		return this.data[i][j];
	}

	@Override
	public void setElement(int i, int j, int number)
	{
		assert 0 <= i && i < PUZZLE_HEIGHT_WIDTH : "Violation of valid row index";
		assert 0 <= j && j < PUZZLE_HEIGHT_WIDTH : "Violation of valid column index";
		assert 0 <= number && number <= PUZZLE_HEIGHT_WIDTH : "Violation of valid cadidate";

		this.data[i][j] = number;
	}

	@Override
	public boolean solve()
	{

		for (int row = 0; row < Sudoku.PUZZLE_HEIGHT_WIDTH; row++)
		{
			for (int col = 0; col < Sudoku.PUZZLE_HEIGHT_WIDTH; col++)
			{
				if (this.data[row][col] == 0)
				{
					for (int n = 1; n < 10; n++)
					{
						if (this.isValidForPosition(row, col, n))
						{
							this.guesses += 1;
							
							this.data[row][col] = n;
							if (this.solve())
							{
								return true;
							}

							else
							{
								this.data[row][col] = 0;
							}

						}
					}

					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean verify()
	{
		for (int row = 0; row < Sudoku.PUZZLE_HEIGHT_WIDTH; row++)
		{
			for (int col = 0; col < Sudoku.PUZZLE_HEIGHT_WIDTH; col++)
			{
				
				if (this.data[row][col] == 0)
				{
					return false;
				}

			}

		}

		return true;
	}

	/*
	 * Methods inherited from Object
	 */
	@Override
	public String toString()
	{
		String result = "";
		for (int row = 0; row < Sudoku.PUZZLE_HEIGHT_WIDTH; row++)
		{

			for (int col = 0; col < Sudoku.PUZZLE_HEIGHT_WIDTH; col++)
			{
				if (this.data[row][col] == 0)
				{
					result += " |";
				}
				else
				{
					result += this.data[row][col]   +"|";
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
	 * Main method. Produces the following output. You can modify it to debug and
	 * refine your implementation.
	 * 
	 * <pre>
	===== Sudoku puzzle =====
	
	| |3| |2| |6| | |
	9| | |3| |5| | |1|
	| |1|8| |6|4| | |
	-----+-----+-----+
	| |8|1| |2|9| | |
	7| | | | | | | |8|
	| |6|7| |8|2| | |
	-----+-----+-----+
	| |2|6| |9|5| | |
	8| | |2| |3| | |9|
	| |5| |1| |3| | |
	-----+-----+-----+
	
	
	
	===== Solving sudoku =====
	
	
	4|8|3|9|2|1|6|5|7|
	9|6|7|3|4|5|8|2|1|
	2|5|1|8|7|6|4|9|3|
	-----+-----+-----+
	5|4|8|1|3|2|9|7|6|
	7|2|9|5|6|4|1|3|8|
	1|3|6|7|9|8|2|4|5|
	-----+-----+-----+
	3|7|2|6|8|9|5|1|4|
	8|1|4|2|5|3|7|6|9|
	6|9|5|4|1|7|3|8|2|
	-----+-----+-----+
	 * </pre>
	 * 
	 * @param args command line arguments, not used
	 */
	public static void main(String[] args)
	{
		SimpleWriter out = new SimpleWriter1L();
		Sudoku s = new SudokuBacktrackRecursive("data/sudoku1.txt");

		out.println("===== Sudoku puzzle =====\n");
		out.println(s.toString());

		out.println("\n\n===== Solving sudoku =====\n\n");
		s.solve();
		out.println(s.toString());

		out.close();
	}
}
