///<summary>
///Author: Garrett Keefe
///Date: 01/23/2020
///Collaborators: None
///
///  This code tests the DependencyGraph functionality
///  
/// I wrote this code by myself, excluding the initial code provided by the TAs and Professor Germain
/// 
///</summary>

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace SpreadsheetUtilities
{
    /// <summary>
    /// This class represents the dependencies present within the spreadsheet, being able to return the amount and an actual representation of all Dependencies.
    /// </summary>
    public class DependencyGraph
    {
        //Data structures that represent our dependencies.
        private Dictionary<string, HashSet<string>> dependentsDictionary = new Dictionary<string, HashSet<string>>();
        private Dictionary<string, HashSet<string>> dependeesDictionary = new Dictionary<string, HashSet<string>>();
        private int sizeOfDependencyGraph;
        /// <summary>
        /// Creates an empty DependencyGraph.
        /// </summary>
        public DependencyGraph()
        {
            dependentsDictionary = new Dictionary<string, HashSet<string>>();
            dependeesDictionary = new Dictionary<string, HashSet<string>>();
            sizeOfDependencyGraph = 0;
        }


        /// <summary>
        /// The number of ordered pairs in the DependencyGraph.
        /// </summary>
        public int Size => sizeOfDependencyGraph;


        /// <summary>
        /// The size of dependees(s).
        /// This property is an example of an indexer.  If dg is a DependencyGraph, you would
        /// invoke it like this:
        /// dg["a"]
        /// It should return the size of dependees("a")
        /// </summary>
        public int this[string s]
        {
            get { if (dependeesDictionary.TryGetValue(s, out HashSet<string> output)) 
                    return output.Count;
                  return 0;
            }
        }


        /// <summary>
        /// Reports whether dependents(s) is non-empty.
        /// </summary>
        public bool HasDependents(string s)
        {
            if (dependentsDictionary.ContainsKey(s) && dependentsDictionary[s].Count > 0)
                return true;
            return false;
        }


        /// <summary>
        /// Reports whether dependees(s) is non-empty.
        /// </summary>
        public bool HasDependees(string s)
        {
            if (dependeesDictionary.ContainsKey(s) && dependeesDictionary[s].Count > 0)
                return true;
            return false;
        }


        /// <summary>
        /// Enumerates dependents(s).
        /// </summary>
        public IEnumerable<string> GetDependents(string s)
        {
            IEnumerable<string> dependents = new HashSet<string>();
            if (dependentsDictionary.TryGetValue(s, out HashSet<string> output))
                dependents = output;
            return dependents;
        }

        /// <summary>
        /// Enumerates dependees(s).
        /// </summary>
        public IEnumerable<string> GetDependees(string s)
        {
            IEnumerable<string> dependees = new HashSet<string>();
            if (dependeesDictionary.TryGetValue(s, out HashSet<string> output))
                dependees = output;
            return dependees;
        }


        /// <summary>
        /// <para>Adds the ordered pair (s,t), if it doesn't exist</para>
        /// 
        /// <para>This should be thought of as:</para>   
        /// 
        ///   t depends on s
        ///
        /// </summary>
        /// <param name="s"> s must be evaluated first. T depends on S</param>
        /// <param name="t"> t cannot be evaluated until s is</param>        /// 
        public void AddDependency(string s, string t)
        {
            if (dependentsDictionary.addValues(s, t) && dependeesDictionary.addValues(t, s))
                sizeOfDependencyGraph++;
        }


        /// <summary>
        /// Removes the ordered pair (s,t), if it exists
        /// </summary>
        /// <param name="s"></param>
        /// <param name="t"></param>
        public void RemoveDependency(string s, string t)
        {
            if (dependentsDictionary.removeValues(s, t) && dependeesDictionary.removeValues(t, s))
                sizeOfDependencyGraph--;
        }


        /// <summary>
        /// Removes all existing ordered pairs of the form (s,r).  Then, for each
        /// t in newDependents, adds the ordered pair (s,t).
        /// </summary>
        public void ReplaceDependents(string s, IEnumerable<string> newDependents)
        {
            //removes all of the previous dependents by iterating through the HashSet backwards
            if (dependentsDictionary.TryGetValue(s, out HashSet<string> oldDependents))
            {
                foreach (string r in oldDependents.Reverse<string>())
                {
                    RemoveDependency(s, r);
                }
            }
            //adds all new dependents to the graph
            foreach (string t in newDependents)
            {
                AddDependency(s, t);
            }
        }


        /// <summary>
        /// Removes all existing ordered pairs of the form (r,s).  Then, for each 
        /// t in newDependees, adds the ordered pair (t,s).
        /// </summary>
        public void ReplaceDependees(string s, IEnumerable<string> newDependees)
        {
            //removes all of the previous dependees by iterating through the HashSet backwards
            if (dependeesDictionary.TryGetValue(s, out HashSet<string> oldDependees))
            {
                foreach (string r in oldDependees.Reverse<string>())
                {
                    RemoveDependency(r, s);
                }
            } 
            //adds all new dependees to the graph
            foreach (string t in newDependees)
            {
                AddDependency(t, s);
            }
        }

    }

    /// <summary>
    /// Class that holds the used extensions for DependencyGraph
    /// </summary>
    public static class extensions
    {
        /// <summary>
        /// Attempts to add values to a Dictinary, if completed returns true to represent success and vice versa.
        /// </summary>
        /// <param name="dictionary"> A dictionary of type string, HashSet </param>
        /// <param name="key"> The key in the Key, Value Pair </param>
        /// <param name="value"> The value in the Key, Value Pair </param>
        /// <returns> Whether operation succeeded or failed </returns>
        public static bool addValues(this Dictionary<string, HashSet<string>> dictionary, string key, string value)
        {
            if (!dictionary.ContainsKey(key))
            {
                HashSet<string> setToBeAdded = new HashSet<string>();
                setToBeAdded.Add(value);
                dictionary.Add(key, setToBeAdded);
                return true;
            }
            else
            {
                if (dictionary.TryGetValue(key, out HashSet<string> setToBeAddedTo) && !setToBeAddedTo.Contains(value))
                {
                    dictionary[key].Add(value);
                    return true;
                }
            }
            return false;
        }
        /// <summary>
        /// Attempts to remove values to a Dictinary, if completed returns true to represent success and vice versa.
        /// </summary>
        /// <param name="dictionary"> A dictionary of type string, HashSet </param>
        /// <param name="key"> The key in the Key, Value Pair </param>
        /// <param name="value"> The value in the Key, Value Pair </param>
        /// <returns> Whether operation succeeded or failed </returns>
        public static bool removeValues(this Dictionary<string, HashSet<string>> dictionary, string key, string value)
        {
            if(dictionary.ContainsKey(key) && dictionary[key].Contains(value))
            {
                dictionary[key].Remove(value);
                return true;
            }
            return false;
        }
    }
}