/// Garrett Keefe
/// This Class takes a list of numbers with each number representing how long an individual tree will
/// take to grow. The class sorts them in descending order and then uses an arbitrary planting and wait 
/// time to calculate how long it will take for all of the trees within the list to grow and be ready 
/// for a party.
/// Date: 1/03/21
using System;
using System.Collections.Generic;

namespace PlantingTrees
{
    class TreePlanter
    {
        /// <summary>
        /// Main driver for the class, retrieves the list of tree growth times and performs logic to calculate
        /// total wait time.
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            List<int> treeArray = new List<int>();
            string x = Console.ReadLine();
            int trees = Int32.Parse(x);
            x = Console.ReadLine();
            string[] nums = x.Split(" ");
            for (int i = 0; i < trees; i++)
            {
                int tree = Int32.Parse(nums[i]);
                treeArray.Add(tree);
            }

            treeArray = MergeSort(treeArray);
            treeArray.Reverse();
            int daysToParty = 0;
            for (int i = 0; i < treeArray.Count; i++)
            {
                int temp = treeArray[i] + 2 + i;
                if (temp > daysToParty)
                    daysToParty = temp;
            }
            Console.WriteLine(daysToParty);
        }

        /// <summary>
        /// Classic MergeSort algorithm that only works with List<int> types, preferably this would be made 
        /// generic in a full implementation of MergeSort.
        /// </summary>
        /// <param name="array">List to be sorted</param>
        /// <returns>Sorted List</returns>
        static List<int> MergeSort(List<int> array)
        {
            if (array.Count == 1)
                return array;
            else
            {
                int m = array.Count / 2;
                List<int> leftside = MergeSort(array.GetRange(0,m));
                List<int> rightside = MergeSort(array.GetRange(m, array.Count - m));
                array = Merge(leftside, rightside);
            }
            return array;
        }

        /// <summary>
        /// Helper function for MergeSort, performs the process of merge the split halves
        /// back together.
        /// </summary>
        /// <param name="leftside">left side of the original array.</param>
        /// <param name="rightside">right side of the original array</param>
        /// <returns>sorted array</returns>
        static List<int> Merge(List<int> leftside, List<int> rightside)
        {
            List<int> newArray = new List<int>(leftside.Count+rightside.Count);
            int leftIndex = 0;
            int rightIndex = 0;
            while(leftIndex < leftside.Count && rightIndex < rightside.Count)
            {
                if(leftside[leftIndex] <= rightside[rightIndex])
                {
                    newArray.Add(leftside[leftIndex]);
                    leftIndex++;
                }
                else
                {
                    newArray.Add(rightside[rightIndex]);
                    rightIndex++;
                }
            }
            if (leftIndex >= leftside.Count && rightIndex < rightside.Count)
                for (int i = rightIndex; i < rightside.Count; i++)
                    newArray.Add(rightside[i]);
            if (leftIndex < leftside.Count && rightIndex >= rightside.Count - 1)
                for (int i = leftIndex; i < leftside.Count; i++)
                    newArray.Add(leftside[i]);
            return newArray;
        }
    }
}
