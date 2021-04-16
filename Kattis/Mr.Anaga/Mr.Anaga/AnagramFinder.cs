using System;
using System.Collections.Generic;

namespace Mr.Anaga
{
    /// <summary>
    /// Simple class which finds and removes anagrams from a list and reports the number of words remaining.
    /// </summary>
    class AnagramFinder
    {
        static void Main(string[] args)
        {
            List<string> wordList = new List<string>();
            string x = Console.ReadLine();
            int a = Int32.Parse(x.Split(" ")[0]);
            for(int i = 0; i < a; i++)
            {
                wordList.Add(Console.ReadLine());
            }

            Console.WriteLine(anagramFinder(wordList));
        }

        /// <summary>
        /// Finds and removes all anagrams from the given list and returns the amount of non-anagrams
        /// </summary>
        /// <param name="wordList">given list of words</param>
        /// <returns>Amount of words that are not anagrams</returns>
        static int anagramFinder(List<string> wordList)
        {
            List<string> sortedWords = new List<string>();
            List<string> foundAnagrams = new List<string>();
            foreach (string word in wordList)
            {
                char[] wordArray = word.ToCharArray();
                Array.Sort(wordArray);
                string sorted = new string(wordArray);
                if (!foundAnagrams.Contains(sorted))
                {
                    if (sortedWords.Contains(sorted))
                    {
                        sortedWords.Remove(sorted);
                        foundAnagrams.Add(sorted);
                    }
                    else
                        sortedWords.Add(sorted);
                }
            }
            
            return sortedWords.Count;
        }
    }
}
