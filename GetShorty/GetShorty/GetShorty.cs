/* Author: Garrett Keefe
 * Date: April 9th, 2021
 * Class: CS 4150
 * 
 * This program solves the problem described at https://utah.kattis.com/problems/getshorty
 * Which can be solved by using a modified version of Djikstra's algorithm.
 * 
 */

using System;
using System.Collections.Generic;

namespace GetShorty
{
    class GetShorty
    {
        /// <summary>
        /// Main driver code
        /// </summary>
        /// <param name="args"> Command Line Arguments </param>
        static void Main(string[] args)
        {
            string[] x = Console.ReadLine().Split(' ');
            int n = Int32.Parse(x[0]);
            int m = Int32.Parse(x[1]);
            
            while (n > 1 && m > 0)
            {
                List<Node> V = new List<Node>(n);

                for (int i = 0; i < n; i++)
                {
                    V.Add(new Node(i));
                }

                for (int i = 0; i < m; i++)
                {
                    x = Console.ReadLine().Split(' ');
                    int vertex1 = Int32.Parse(x[0]);
                    int vertex2 = Int32.Parse(x[1]);
                    double weight = double.Parse(x[2]);
                    
                    try { V[vertex1].addPath(V[vertex2], weight); }
                    catch { }
                }

                Node.modifiedDjikstras(V);
                Console.WriteLine(string.Format("{0:0.0000}", V[n - 1].size));

                x = Console.ReadLine().Split(' ');
                n = Int32.Parse(x[0]);
                m = Int32.Parse(x[1]);
            }
        }
    }

    /// <summary>
    /// Class which represents the Nodes in our graph.
    /// </summary>
    class Node
    {
        public int value;
        public double size;
        protected Dictionary<Node, double> paths;
        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="val"> int value of the Node </param>
        public Node(int val)
        {
            this.value = val;
            this.paths = new Dictionary<Node, double>();
            this.size = double.MinValue;
        }
        /// <summary>
        /// Adds a weighted path between two Nodes.
        /// </summary>
        /// <param name="node"> Node to be connected </param>
        /// <param name="weight"> weight of the path </param>
        public void addPath(Node node, double weight)
        {
            if(this.paths.ContainsKey(node))
            {
                if(weight > this.paths[node])
                {
                    this.paths[node] = weight;
                    node.paths[this] = weight;
                    return;
                }
                return;
            }
            this.paths.Add(node, weight);
            node.paths.Add(this, weight);
        }
        /// <summary>
        /// Modified version of Djikstra's Algorithm which prioritizes
        /// maximizing size, rather than path length.
        /// </summary>
        /// <param name="V"> List of Verticies to run on </param>
        static public void modifiedDjikstras(List<Node> V)
        {
            PriorityQueue pq = new PriorityQueue();
            // Setup for djikstras to run properly.
            foreach(Node n in V)
            {
                n.size = double.MinValue;
            }
            V[0].size = 1;
            pq.Update(V[0], 1);
            // Use pq to iterate through the nodes and finds the
            // correct size for each node.
            while(pq.size() > 0)
            {
                Node node = pq.Extract();
                foreach (Node n in node.paths.Keys)
                {
                    if (n.size < (node.paths[n] * node.size))
                    {
                        n.size = node.paths[n] * node.size;
                        pq.Update(n, n.size);
                    }
                }
            }
        }
    }
    /// <summary>
    /// Class which represents a priority queue for use in 
    /// modified Djikstra's.
    /// </summary>
    class PriorityQueue
    {
        protected List<Node> nodes;
        protected List<double> weights;
        /// <summary>
        /// Constructor
        /// </summary>
        public PriorityQueue()
        {
            this.nodes = new List<Node>();
            this.weights = new List<double>();
        }
        /// <summary>
        /// This checks if a node is already within the queue and if so,
        /// removes it so that the value can be added again with the lesser value.
        /// </summary>
        /// <param name="node"> node to be checked for </param>
        /// <param name="weight"> weight to update </param>
        public void Update(Node node, double weight)
        {
            if(this.nodes.Contains(node))
            {
                if (this.weights[this.nodes.IndexOf(node)] > weight)
                    return;
                int index = this.nodes.IndexOf(node);
                this.nodes.RemoveAt(index);
                this.weights.RemoveAt(index);                
            }
            this.Add(node, weight);
        }
        /// <summary>
        /// Adds and sorts the node, weight pair into the correct location.
        /// </summary>
        /// <param name="node"> node to be added </param>
        /// <param name="weight"> the weight of its path </param>
        private void Add(Node node, double weight)
        {
            int index = this.findIndex(weight);
            this.nodes.Insert(index, node);
            this.weights.Insert(index, weight);
        }
        /// <summary>
        /// Removes the first node and weight and returns the node
        /// </summary>
        /// <returns> node with highest weight </returns>
        public Node Extract()
        {
            if (this.nodes.Count == 0)
                return null;
            this.weights.RemoveAt(0);
            Node node = this.nodes[0];
            this.nodes.RemoveAt(0);
            return node;
        }
        /// <summary>
        /// size function for checking if PriorityQueue is empty.
        /// </summary>
        /// <returns> count of nodes </returns>
        public int size()
        {
            return this.nodes.Count;
        }
        /// <summary>
        /// Finds the lowest index possible to add our element
        /// </summary>
        /// <param name="weight"> weight to be sorted </param>
        /// <returns> index of location to insert </returns>
        private int findIndex(double weight)
        {
            for(int i = 0; i < this.weights.Count; i++)
            {
                if (this.weights[i] < weight)
                    return i;
            }
            return this.weights.Count;
        }
    }

}
