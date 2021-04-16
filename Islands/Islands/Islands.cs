/* Author: Garrett Keefe
 * Date: March 21, 2021
 * CS 4150
 * 
 * This code solves the problem at https://utah.kattis.com/problems/islands3 
 * which is a problem where the program is supposed to take in a map of an area 
 * represented as text and find the minimum number of possible Islands in that area.
 * 
 */

using System;
using System.Collections.Generic;

namespace Islands
{
    /// <summary>
    /// Class which contains the console app driving the startup for the Islands problem.
    /// </summary>
    class Islands
    {
        static void Main(string[] args)
        {
            int height;
            int width;
            string input = Console.ReadLine();
            width = Int32.Parse(input.Split(" ")[1]);
            height = Int32.Parse(input.Split(" ")[0]);
            //tempmap allows us to reference Nodes later for connection of two Nodes
            Node[,] tempmap = new Node[width, height];

            for (int i = 0; i < height; i++)
            {
                input = Console.ReadLine();
                for (int j = 0; j < width; j++)
                {
                    //intialize the new Node and adds it to the temp map
                    char nodeVal = input[j];
                    Node temp = new Node(nodeVal);
                    //connects nodes with a value of Clouds or Land
                    if(nodeVal != 'W')
                    {
                        if (i != 0 && tempmap[j, i - 1].getVal() != 'W')
                            temp.addPath(tempmap[j, i - 1]);
                        if (j != 0 && tempmap[j - 1, i].getVal() != 'W')
                            temp.addPath(tempmap[j - 1, i]);
                    }
                    tempmap[j, i] = temp;
                }
            }

            //intializes the necessary visited List and runs the path finder
            List<Node> visited = new List<Node>();
            Console.WriteLine(Node.islandFinder(tempmap, visited));
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
        /// Returns the char value.
        /// </summary>
        /// <returns>value</returns>
        public char getVal()
        {
            return value;
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
        /// Driver that handles the logic for evaluating the minimum number of islands as explained by the Islands problem
        /// </summary>
        /// <param name="map"> map representing the area to be evaluated </param>
        /// <param name="visited"> Nodes that have been visited by the DFS algorithm </param>
        /// <returns> the minimum number of islands </returns>
        static public int islandFinder(Node[,] map, List<Node> visited)
        {
            int islands = 0;
            foreach(Node n in map)
            {
                if(n.getVal() == 'L' && !visited.Contains(n))
                {
                    islands++;
                    n.DFS(visited);
                }
            }
            return islands;
        }
        /// <summary>
        /// Runs a depth first search algorithm that simply marks a node as visited if it is reachable.
        /// </summary>
        /// <param name="visited"> Nodes that have already been visited by this recursive function </param>
        protected void DFS(List<Node> visited)
        {
            //Adds this node to the visited list so recursion does not return to it.
            visited.Add(this);

            //Checks for next node being visited and if not, recurses on that node.
            for (int j = 0; j < paths.Count; j++)
            {
                if (!visited.Contains(paths[j]))
                    paths[j].DFS(visited);
            }
        }
    }
}
