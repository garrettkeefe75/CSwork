package assignment10;

import java.util.ArrayList;

import assignment10.AdjListGraph.Edge;
import components.list.*;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.*;

/***
 * 
 * @author Garrett Keefe && David Harrington
 *
 */

public class PathFinder
{
	
	static String[][] emptyMaze;
	static int x;
	static int y;

	/**
	 * Builds a maze with a breadth first solution in mind.
	 * 
	 * @param  inputFileName
	 * @return
	 */
	private static AdjListGraph mazeBuilder(String inputFileName)
	{
		SimpleReader file = new SimpleReader1L(inputFileName);

		String firstLine = file.nextLine();

		String[] arr = firstLine.split(" ");

		// maximum x and y
		x = Integer.parseInt(arr[1]);
		y = Integer.parseInt(arr[0]);

		// The matrix representation of the maze, used for building
		String[][] rep = new String[y][x];

		int k = 0;
		int j = 0;
		// Builds the matrix
		while (!file.atEOS())
		{
			String line = file.nextLine();

			// the replacement variable for empty strings

			// Iterates through x
			for (int i = 0; i < x; i++)
			{
				// checks for a blank space and replaces it with whatever k is set to.
				if ((Character.toString(line.charAt(i))).compareTo(" ") == 0)
				{
					rep[j][i] = Integer.toString(k);
					k++;
				}

				else
				{
					rep[j][i] = "" + line.charAt(i);
				}
			}
			j++;

		}
		

		file.close();
		emptyMaze = rep;
		
		// Builds the maze
		// Iterates through x
		int size = 0;
		for (int i = 0; i < x; i++)
		{
			// Iterates through y and adds vertices
			for (int j1 = 0; j1 < y; j1++)
			{
					size++;
			}
		}
		AdjListGraph maze = new AdjListGraph(size);

		for (int i = 0; i < y; i++)
		{
			// Iterates through y and sets the vertices
			for (int j1 = 0; j1 < x; j1++)
			{
					maze.addVertex(rep[i][j1]);
			}
		}

		// connects the vertices
		for (int i = 0; i < y; i++)
		{
			// Iterates through y
			for (int j1 = 0; j1 < x; j1++)
			{
				// Checks if rep[i][j] is a valid vertex to add
				if (!rep[i][j1].equals("X"))
				{	
					// connect edges
					if (!rep[i - 1][j1].equals("X"))
					{
						maze.addEdge(rep[i][j1], rep[i - 1][j1], 0);
					}

					if (!rep[i][j1 - 1].equals("X"))
					{
						maze.addEdge(rep[i][j1], rep[i][j1 - 1], 0);
					}

					if (!rep[i + 1][j1].equals("X"))
					{
						maze.addEdge(rep[i][j1], rep[i + 1][j1], 0);
					}

					if (!rep[i][j1 + 1].equals("X"))
					{
						maze.addEdge(rep[i][j1], rep[i][j1 + 1], 0);
					}
				}
			}
		}

		return maze;

	}

	private static void fileBuilder(AdjListGraph maze, String outputFileName,
			ArrayList<List<Edge>> path)
	{
		//Opens file for writing and adds the dimensions
		SimpleWriter file = new SimpleWriter1L(outputFileName);
		file.print(y + " " + x);
		file.println();
		int currentIndex = 0;
		for(int i = 0; i < y; i++)
		{
			// iterates through List and adds the appropriate values in their correct spots
			for(int j = 0; j < x; j++)
			{
				if(path.contains(maze.get(currentIndex)))
				{
					if(maze.getString(currentIndex).equals("S"))
						file.print("S");
					else if(maze.getString(currentIndex).equals("G"))
						file.print("G");
					else
						file.print(".");
				}
				else if(maze.getString(currentIndex).equals("X"))
					file.print("X");
				else
					file.print(" ");
				currentIndex++;
			}
			file.println();
		}
		file.close();
		
	}

	public static int solveMaze(String inputFileName, String outputFileName)
	{
		//builds maze
		AdjListGraph maze = mazeBuilder(inputFileName);
		//uses Breadth First to search for shortest unweighted paths
		ArrayList<List<Edge>> path = maze.shortestPath("S", "G");
		//returns false if the breadth first algorithm fails or returns a list with only the start node in it.
		if(path.equals(null) || path.size()==1)
			return -1;
		else
		{
			//places all values within the file
			fileBuilder(maze, outputFileName, path);
		}
		//returns the amount of periods
		return path.size()-1;
	}
}