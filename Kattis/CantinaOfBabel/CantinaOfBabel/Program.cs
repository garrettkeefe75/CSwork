/* Author: Garrett Keefe
 * Date: March 31, 2021
 * Class: CS 4150
 * 
 * This program solves the problem defined at https://utah.kattis.com/problems/cantinaofbabel
 * which can be represented as a graph problem wherein each vertex has edges which point to which vertices
 * that vertex can understand. Then we can use Kosaraju's Algorithm as described in Class to label all 
 * of the Strong Components and use them to find the component with the most members. 
 * 
 */

using System;
using System.Collections.Generic;

namespace CantinaOfBabel
{
    class Program
    {
        /// <summary>
        /// Main driver method
        /// </summary>
        /// <param name="args"> Command Line Inputs </param>
        static void Main(string[] args)
        {
            // Performs setup for reading input.
            int N = Int32.Parse(Console.ReadLine());
            List<Node> characters = new List<Node>();
            for(int i = 0; i < N; i++)
            {
                // Grabs necessary info from lines and creates a Node with it.
                string[] characterInfo = Console.ReadLine().Split(' ');
                string characterName = characterInfo[0];
                string spokenLanguage = characterInfo[1];
                List<string> understoodLanguage = new List<string>();
                for(int j = 2; j < characterInfo.Length; j++)
                {
                    understoodLanguage.Add(characterInfo[j]);
                }
                characters.Add(new Node(characterName, spokenLanguage, understoodLanguage));
            }

            // Logic for addition of edges to our graph with a focus on who can understand who.
            foreach(Node n in characters)
            {
                foreach(Node s in characters)
                {
                    if(n != s)
                    {
                        if (n.spokenLanguage == s.spokenLanguage)
                            n.addPath(s);
                        else if (n.understoodLanguages.Contains(s.spokenLanguage))
                            n.addDirectedPath(s);
                    }
                }
            }
            // Runs Kosaraju on our graph.
            Node.KosarajuSharir(characters);

            // Collects all of our labels and finds the label with the highest occurence. 
            Dictionary<string, int> parties = new Dictionary<string, int>();
            foreach(Node n in characters)
            {
                if (parties.ContainsKey(n.root.characterName))
                    parties[n.root.characterName]++;
                else
                    parties.Add(n.root.characterName, 1);
            }

            // Reports back the maximum label occurence and finds the amount of characters to be removed.
            int maxPartySize = -1;
            foreach(int i in parties.Values)
            {
                if (i > maxPartySize)
                    maxPartySize = i;
            }
            Console.WriteLine(N - maxPartySize);
        }

        /// <summary>
        /// This class represents the vertices for our graph.
        /// </summary>
        class Node
        {
            public string characterName;
            public string spokenLanguage;
            public List<string> understoodLanguages;
            protected List<Node> paths;
            protected List<Node> revPaths;
            public Node root;
            /// <summary>
            /// Constructor
            /// </summary>
            /// <param name="val"> char value of the Node </param>
            public Node(string charName, string spknLnge, List<string> undrstdLnge)
            {
                this.characterName = charName;
                this.spokenLanguage = spknLnge;
                this.understoodLanguages = undrstdLnge;
                this.paths = new List<Node>();
                this.revPaths = new List<Node>();
            }
            /// <summary>
            /// Adds a non-directed path between two Nodes.
            /// </summary>
            /// <param name="node"> Node to be connected </param>
            public void addPath(Node node)
            {
                if (paths.Contains(node))
                    return;
                this.paths.Add(node);
                node.paths.Add(this);
                this.revPaths.Add(node);
                node.revPaths.Add(this);
            }
            /// <summary>
            /// Adds a directed path between two Nodes.
            /// </summary>
            /// <param name="node"> Node to be connected </param>
            public void addDirectedPath(Node node)
            {
                if (paths.Contains(node))
                    return;
                this.paths.Add(node);
                node.revPaths.Add(this);
            }
            /// <summary>
            /// Runs a depth first search algorithm on the reverse paths and marks a node as visited if it is reachable. 
            /// Also adds the nodes to be evaluated in Kosaraju's.
            /// </summary>
            /// <param name="visited"> Nodes that have already been visited by this recursive function </param>
            /// <param name="S"> Stack for use in Kosaraju's </param>
            protected void PushPostRevDFS(List<Node> visited, Stack<Node> S)
            {
                //Adds this node to the visited list so recursion does not return to it.
                visited.Add(this);

                //Checks for next node being visited and if not, recurses on that node.
                foreach (Node n in revPaths)
                {
                    if (!visited.Contains(n))
                        n.PushPostRevDFS(visited, S);
                }
                S.Push(this);
            }
            /// <summary>
            /// Kosaraju's Algorithm. This finds the Strong Component Graph and Labels them according to 
            /// their root nodes. Found the pseudo code for this algorithm from Week 10 Tuesday lecture.
            /// </summary>
            /// <param name="graph"> graph to apply Kosaraju's to </param>
            static public void KosarajuSharir(List<Node> graph)
            {
                Stack<Node> S = new Stack<Node>();
                List<Node> visited = new List<Node>();
                foreach (Node n in graph)
                    n.root = null;

                foreach(Node n in graph)
                {
                    if (!visited.Contains(n))
                        n.PushPostRevDFS(visited, S);
                }
                while(S.Count > 0)
                {
                    Node temp = S.Pop();
                    if (temp.root == null)
                        LabelOneDFS(temp, temp);
                }
            }
            /// <summary>
            /// Helper function for Kosaraju's algorithm.
            /// </summary>
            /// <param name="v"> vertex to add our root to </param>
            /// <param name="r"> the root vertex </param>
            static protected void LabelOneDFS(Node v, Node r)
            {
                v.root = r;
                foreach(Node n in v.paths)
                {
                    if (n.root == null)
                        LabelOneDFS(n, r);
                }
            }
        }
    }
}
