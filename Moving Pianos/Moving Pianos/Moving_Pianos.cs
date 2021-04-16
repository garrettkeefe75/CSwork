/* Author: Garrett Keefe
 * Date: 02/25/2021
 * Class: CS 4150
 * 
 * This Program takes in a list of tasks that represent moving and tuning a piano. The goal of the program 
 * is to calculate whether this list of tasks can be completed with the constraints given at https://utah.kattis.com/problems/piano
 * It then prints out whether the list can be completed without weekends being used, with weekends being used,
 * or not at all.
 * 
 */

using System;

namespace Moving_Pianos
{
    class Moving_Pianos
    {
        /// <summary>
        /// Main method which handles the printing of the results and reading of the parameters needed.
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            int tests = Int32.Parse(Console.ReadLine());
            for(int i = 0; i < tests; i++)
            {
                string[] constraints = Console.ReadLine().Split(" ");
                int tasks = Int32.Parse(constraints[0]);
                int pianoTuners = Int32.Parse(constraints[1]);
                int[,] taskTimes = new int[tasks,2];

                //Reads in the list of tasks
                for (int j = 0; j < tasks; j++)
                {
                    string[] timeconstraints = Console.ReadLine().Split(" ");
                    int b = Int32.Parse(timeconstraints[0]);
                    int e = Int32.Parse(timeconstraints[1]);
                    taskTimes[j, 0] = b;
                    taskTimes[j, 1] = e;
                }
                
                //Calculates the amount of moving pairs we have.
                int pianoMovingPairs = pianoTuners / 2;
                taskTimes = pianoSort(taskTimes);

                //Prints whether the jobs given can be completed
                if (pianoMovingNoWeekends(pianoMovingPairs, tasks, taskTimes))
                    Console.WriteLine("fine");
                else if (pianoMoving(pianoMovingPairs, tasks, taskTimes))
                    Console.WriteLine("weekend work");
                else
                    Console.WriteLine("serious trouble");
            }
        }

        /// <summary>
        /// Piano Sort implements Insertion Sort that sorts the list of tasks by their end time. Insertion
        /// Sort is used because of the relatively small maximum list size. (1000)
        /// </summary>
        /// <param name="taskTimes">List to be sorted</param>
        /// <returns>Sorted list</returns>
        static int[,] pianoSort(int[,] taskTimes)
        {
            for(int i = 0; i < taskTimes.Length/2; i++)
            {
                int j = i - 1;
                //Save current times
                int endTime = taskTimes[i, 1];
                int startTime = taskTimes[i, 0];

                //Iterate backwards and keep replacing untill correct position is found
                while (j >= 0 && taskTimes[j, 1] > endTime) 
                {
                    taskTimes[j + 1, 1] = taskTimes[j, 1];
                    taskTimes[j + 1, 0] = taskTimes[j, 0];
                    j = j - 1;
                }
                //Insert current times when the correct positions has been found.
                taskTimes[j + 1, 1] = endTime;
                taskTimes[j + 1, 0] = startTime;
            }
            return taskTimes;
        }

        /// <summary>
        /// This drives the logic for the calculation for needing no work on the weekends to complete the piano 
        /// moving tasks.
        /// </summary>
        /// <param name="pianoMovers">Amount of pairs of workers</param>
        /// <param name="tasks">Amount of tasks to complete</param>
        /// <param name="taskTimes">The range for which each task can be completed</param>
        /// <returns>True if possible, False if not</returns>
        static bool pianoMovingNoWeekends(int pianoMovers, int tasks, int[,] taskTimes)
        {
            int[] jobsDuringDay = new int[100];
            for (int i = 0; i < tasks; i++)
            {
                //checks to see if the task can be done with the current amount of workers.
                if (!firstDayAvailableNoWeekends(taskTimes[i, 0], taskTimes[i, 1], ref jobsDuringDay, pianoMovers))
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// Helper functions for pianoMovingNoWeekends
        /// </summary>
        /// <param name="start">Start time of task</param>
        /// <param name="end">End time of task</param>
        /// <param name="jobsDuringDay">The current amount of jobs in a given day</param>
        /// <param name="workerPairs">The pairs of workers available</param>
        /// <returns>True if there is space for another job, false otherwise</returns>
        static bool firstDayAvailableNoWeekends(int start, int end, ref int[] jobsDuringDay, int workerPairs)
        {
            for (int i = start; i <= end; i++)
            {
                //checks that their is enough workers for the day and if it is not a weekend
                if (jobsDuringDay[i] < workerPairs && i % 7 != 6 && i % 7 != 0)
                {
                    //adds a worker to the day
                    jobsDuringDay[i]++;
                    return true;
                }
            }
            return false;
        }
        /// <summary>
        /// This drives the logic for the calculation of completing the piano 
        /// moving tasks.
        /// </summary>
        /// <param name="pianoMovers">Amount of pairs of workers</param>
        /// <param name="tasks">Amount of tasks to complete</param>
        /// <param name="taskTimes">The range for which each task can be completed</param>
        /// <returns>True if possible, False if not</returns>
        static bool pianoMoving(int pianoMovers, int tasks, int[,] taskTimes)
        {
            int[] jobsDuringDay = new int[100];
            for(int i = 0; i < tasks; i++)
            {
                //checks to see if the task can be done with the current amount of workers.
                if(!firstDayAvailable(taskTimes[i,0], taskTimes[i,1], ref jobsDuringDay, pianoMovers))
                {
                    return false;
                }
            }
            return true;
        }

        /// <summary>
        /// Helper functions for pianoMoving
        /// </summary>
        /// <param name="start">Start time of task</param>
        /// <param name="end">End time of task</param>
        /// <param name="jobsDuringDay">The current amount of jobs in a given day</param>
        /// <param name="workerPairs">The pairs of workers available</param>
        /// <returns>True if there is space for another job, false otherwise</returns>
        static bool firstDayAvailable(int start, int end, ref int[] jobsDuringDay, int workerPairs)
        {
            for(int i = start; i <= end; i++)
            {
                //Checks to see if their is enough workers for the day
                if(jobsDuringDay[i] < workerPairs)
                {
                    //adds a worker to the day
                    jobsDuringDay[i]++;
                    return true;
                }
            }
            return false;
        }
    }
}
