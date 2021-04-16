/* Author: Garrett Keefe
 * Date: Feb, 19, 2021
 * Class: CS 4150 SP 2021
 * 
 * Description:
 * This class solves the Narrow Gallery problem by utilizing a backtracking algorithm with memoized recursion.
 * The memoized recursion is necessary for time complexity issues when dealing with larger 'Galleries'.
 * Problem description found here: https://utah.kattis.com/problems/narrowartgallery
 * 
 * Notes: Implemented Memoization because Piazza Question 121 stated "For the Narrow Art Gallery Kattis, 
 * we recommend you consider how one would take the approach outlined by the quiz and turn it into DP, but this is not required for full credit."
 * Had hard time trying to figure out DP algo for this problem.
 * Also used while trying to solve DP: https://www.geeksforgeeks.org/dynamic-programming/
 */
using System;
namespace NarrowArtGallery
{
    class ArtGallery
    {
        //public variables for calculation.

        //Array which represents the Narrow Gallery.
        public static int[,] GalleryArray;
        //Array that holds all of the memoized recursion.
        public static int[,,] MemoizationArray;
        //size of the Array
        public static int n;
        //number of rooms to be closed
        public static int originalK;

        /// <summary>
        /// Main Method which takes in input provided and calls the logic function.
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            //Reads the first line and saves the values.
            string x = Console.ReadLine();
            n = Int32.Parse(x.Split(" ")[0]);
            originalK = Int32.Parse(x.Split(" ")[1]);
            //Initializes both the GalleryArray and MemoizationArray as soon as possible.
            GalleryArray = new int[n, 2];
            MemoizationArray = new int[n, 3, originalK + 1 + (n - originalK)];
            //Reads the rest of the line and saves them into the GalleryArray
            for (int i = 0; i < n; i++)
            {
                string[] y = Console.ReadLine().Split(" ");
                GalleryArray[i, 0] = Int32.Parse(y[0]);
                GalleryArray[i, 1] = Int32.Parse(y[1]);
            }
            Console.ReadLine();

            //Prints out the return of the main logic function.
            Console.WriteLine(recursiveSolver(0, 2, originalK));
        }

        /// <summary>
        /// Main logic function. Calculates the total value that can be achieved while closing all of the 
        /// required doors.
        /// </summary>
        /// <param name="r">current row</param>
        /// <param name="m">0=closing left door, 1=closing right door, 2=ambiguous</param>
        /// <param name="k">doors left to be closed</param>
        /// <returns>Max value of rooms that can be staffed.</returns>
        static int recursiveSolver(int r, int m, int k)
        {
            //used for indexing the memoization array
            int tk = k + (n - originalK);

            //if no rows left, return 0
            if (r >= n)
                return 0;

            //when there is an equal amount of rows left and doors that need to be closed, we simplify our logic.
            if (k == n - r)
            {
                if (m == 0)
                {
                    if (MemoizationArray[r, m, tk] == 0)
                    {
                        int tempv = GalleryArray[r, 0] + recursiveSolver(r + 1, 0, k - 1);
                        MemoizationArray[r, m, tk] = tempv;
                    }
                    return MemoizationArray[r, m, tk];
                }
                if (m == 1)
                {
                    if (MemoizationArray[r, m, tk] == 0)
                    {
                        int tempv = GalleryArray[r, 1] + recursiveSolver(r + 1, 1, k - 1);
                        MemoizationArray[r, m, tk] = tempv;
                    }
                    return MemoizationArray[r, m, tk];
                }
                if (m == 2)
                {
                    if (MemoizationArray[r, m, tk] == 0)
                    {
                        int tempv = Math.Max((GalleryArray[r, 0] + recursiveSolver(r + 1, 0, k - 1)), GalleryArray[r, 1] + recursiveSolver(r + 1, 1, k - 1));
                        MemoizationArray[r, m, tk] = tempv;
                    }
                    return MemoizationArray[r, m, tk];
                }
            }

            //Logic for handling when there does not exist a clear candidate for room closing.
            else if (m == 0)
            {
                if (MemoizationArray[r, m, tk] == 0)
                {
                    int tempv = Math.Max((GalleryArray[r, 0] + recursiveSolver(r + 1, 0, k - 1)), (GalleryArray[r, 0] + GalleryArray[r, 1] + recursiveSolver(r + 1, 2, k)));
                    MemoizationArray[r, m, tk] = tempv;
                }
                return MemoizationArray[r, m, tk];
            }
            else if (m == 1)
            {
                if (MemoizationArray[r, m, tk] == 0)
                {
                    int tempv = Math.Max((GalleryArray[r, 1] + recursiveSolver(r + 1, 1, k - 1)), (GalleryArray[r, 0] + GalleryArray[r, 1] + recursiveSolver(r + 1, 2, k)));
                    MemoizationArray[r, m, tk] = tempv;
                }
                return MemoizationArray[r, m, tk];
            }
            else if (m == 2)
            {
                if (MemoizationArray[r, m, tk] == 0)
                {
                    int tempv = Math.Max((GalleryArray[r, 0] + recursiveSolver(r + 1, 0, k - 1)), Math.Max((GalleryArray[r, 1] + recursiveSolver(r + 1, 1, k - 1)),
                                            (GalleryArray[r, 0] + GalleryArray[r, 1] + recursiveSolver(r + 1, 2, k))));
                    MemoizationArray[r, m, tk] = tempv;
                }
                return MemoizationArray[r, m, tk];
            }

            
            return 0;
        }
    }
}