/* Author: Garrett Keefe
 * Date: March 10, 2021
 * CS 4150
 * 
 * This code solves the problem provided in https://utah.kattis.com/problems/gold
 * which is a optimal solution solver for a minigame that has the user traversing a graph and picking up treasure
 * as safely as possible.
 * 
 */
using System;
using System.Collections.Generic;

namespace Getting_Gold
{
    /// <summary>
    /// Class which contains the console app driving the startup for the Getting Gold problem.
    /// </summary>
    class Getting_Gold
    {
        static void Main(string[] args)
        {
            int height;
            int width;
            string input = Console.ReadLine();
            width = Int32.Parse(input.Split(" ")[0]);
            height = Int32.Parse(input.Split(" ")[1]);
            Node playerPosition = new Node(' ');
            //tempmap allows us to reference Nodes later for connection of two Nodes
            Node[,] tempmap = new Node[width, height]; 

            for(int i = 0; i < height; i++)
            {
                input = Console.ReadLine();
                for(int j = 0; j < width; j++)
                {
                    //intialize the new Node and adds it to the temp map while creating paths to the known connections
                    char nodeVal = input[j];
                    Node temp = new Node(nodeVal);
                    if (i != 0)
                        temp.addPath(tempmap[j, i - 1]);
                    if (j != 0)
                        temp.addPath(tempmap[j - 1, i]);
                    tempmap[j, i] = temp;

                    //check if we have found players position
                    if (nodeVal == 'P')
                        playerPosition = temp;
                }
            }

            //intializes the necessary visited List and runs the path finder
            List<Node> visited = new List<Node>();
            Console.WriteLine(playerPosition.safePathFinder(visited));
        }
    }

    /// <summary>
    /// Class which represents the Nodes in our graph.
    /// </summary>
    class Node
    {
        protected char value;
        protected List<Node> paths;
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="val"> char value of the Node </param>
        public Node(char val)
        {
            this.value = val;
            this.paths = new List<Node>();
        }
        /// <summary>
        /// Adds a path between two Nodes.
        /// </summary>
        /// <param name="node"> Node to be connected </param>
        public void addPath(Node node)
        {
            this.paths.Add(node);
            node.paths.Add(this);
        }
        /// <summary>
        /// This uses the logic provided in the Getting Gold problem to find the most lucrative path through a graph without endangering the player.
        /// </summary>
        /// <param name="visited"> Nodes that have already been visited by this recursive function </param>
        /// <returns> the amount of gold possible to retrieve </returns>
        public int safePathFinder(List<Node> visited)
        {
            //Adds this node to the visited list so recursion does not return to it.
            visited.Add(this);
            
            //Wall check
            if (value == '#')
                return 0;
            
            int total = 0;
            //If we have found gold, pick it up and add it to our total.
            if (value == 'G')
            {
                total++;
                value = '.';
            }
            
            //Makes sure we are not about to fall into a trap.
            for(int i = 0; i < paths.Count; i++)
            {
                if (paths[i].value == 'T')
                    return total;
            }

            //Checks for next node being visited and if not, recurses on that node.
            for (int j = 0; j < paths.Count; j++)
            {
                if(!visited.Contains(paths[j]))
                    total = total + paths[j].safePathFinder(visited);
            }
            return total;
        }
    }
}
