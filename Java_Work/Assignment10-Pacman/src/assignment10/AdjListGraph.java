package assignment10;

import java.util.ArrayList;

import components.list.*;
import components.map.*;
import components.queue.*;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

/**
 * Adjacency List representation of the Graph.
 * 
 * @author
 *
 */
public class AdjListGraph implements Graph
{
	public static class Vertex
	{
		Vertex prev;
		List<Edge> data;
		int index;
		public Vertex(List<Edge> data, int index)
		{
			this.prev = null;
			this.data = data;
			this.index = index;
		}
		public Vertex(Vertex prev, List<Edge> data, int index)
		{
			this.prev = prev;
			this.data = data;
			this.index = index;
		}
	}
	public static class Edge
	{
		int dest;
		int cost;

		public Edge(int dest, int cost)
		{
			this.dest = dest;
			this.cost = cost;
		}

	}

	/*
	 * Private members
	 */

	/**
	 * A list of vertices, where each sub-List is a vertex, represented by a list,
	 * which contains that vertex's vertices.
	 */
	private List<List<Edge>> adjList;

	/**
	 * Vertex map, from String to Integer/Indices
	 */
	private Map<String, Integer> vertexMap;

	/**
	 * Vertex map, from Integer/Indices to String.
	 */
	private Map<Integer, String> intVertexMap;

	/**
	 * Last node modified.
	 */
	private int nodeId;

	/**
	 * Resets the matrix to the given number of vertices and resets the other fields
	 * to their initial values.
	 * 
	 * @param    numVertices number of vertices for the new graph
	 * @modifies             this
	 */
	private void resize(int numVertices)
	{
		this.adjList = new DoublyLinkedList<List<Edge>>();
		this.intVertexMap = new MapOnHashTable<Integer, String>();
		this.vertexMap = new MapOnHashTable<String, Integer>();
		for (int i = 0; i < numVertices; i++)
		{
			List<Edge> E = new DoublyLinkedList<Edge>();
			this.adjList.add(E);
		}
		this.nodeId = 0;
	}

	/**
	 * No argument constructor.
	 * 
	 * @param numVertices number of vertices
	 */
	public AdjListGraph(int numVertices)
	{
		resize(numVertices);
	}

	/**
	 * Constructor from a file.
	 * 
	 * @param    file path of a file in the specified format
	 * @requires      the file is in this format: first line is an integer,
	 *                indicating number of vertices (n), followed by n lines, each
	 *                containing an edge in this comma-separated format:
	 *                (src,dst,cost)
	 */
	public AdjListGraph(String file)
	{
		SimpleReader in = new SimpleReader1L(file);
		int numVertices = Integer.parseInt(in.nextLine());
		resize(numVertices);
		while (!in.atEOS())
		{
			String line = in.nextLine();
			String[] edgeParts = line.split(",");
			String src = edgeParts[0];
			if (!vertexMap.hasKey(src))
				addVertex(src);
			String dst = edgeParts[1];
			if (!vertexMap.hasKey(dst))
				addVertex(dst);
			int cost = Integer.parseInt(edgeParts[2]);
			addEdge(src, dst, cost);
		}
		in.close();
	}

	@Override
	public void addVertex(String label)
	{
		int index = this.nodeId;
		this.intVertexMap.add(index, label);
		if(!this.vertexMap.hasKey(label))
			this.vertexMap.add(label, index);
		this.nodeId++;
	}

	@Override
	public void addEdge(String src, String dst, int cost)
	{
		int x = this.vertexMap.value(src);
		int y = this.vertexMap.value(dst);
		Edge n = new Edge(y, cost);
		this.adjList.get(x).add(n);
	}

	@Override
	public int pathCost(List<String> path)
	{
		int totalCost = 0;
		int i = 0;
		while (i < path.size() - 1)
		{
			int indexStrt = this.vertexMap.value(path.get(i));
			int indexEnd = this.vertexMap.value(path.get(i + 1));
			for (Edge j : this.adjList.get(indexStrt))
			{
				if (j.dest == indexEnd)
				{
					totalCost += j.cost;
					break;
				}
				else if (j == this.adjList.get(indexStrt).get(this.adjList.get(indexStrt).size() - 1))
				{
					return Integer.MAX_VALUE;
				}
			}
			i++;
		}

		return totalCost;
	}
	
	public int getIndex(List<Edge> vertex)
	{
		for(int i = 0; i < this.adjList.size(); i++)
		{
			if(vertex == this.adjList.get(i))
				return i;
		}
		return 0;
	}
	
	public int size()
	{
		return this.nodeId;
	}
	
	public List<Edge> get(int index)
	{
		return this.adjList.get(index);
	}
	
	public String getString(int index)
	{
		return this.intVertexMap.value(index);
	}

	/*
	 * Methods from Object
	 */
	@Override
	public String toString()
	{
		int numVertices = intVertexMap.size();
		StringBuilder sb = new StringBuilder();
		sb.append("digraph g {\n");
		for (int i = 0; i < numVertices; i++)
		{
			for (Edge E : this.adjList.get(i))
			{
				int x = E.dest;
				int y = E.cost;
				String Dest = this.intVertexMap.value(x);
				String Strt = this.intVertexMap.value(i);
				sb.append(Strt + "->" + Dest + " [label=" + y + "];\n");
			}
		}
		sb.append("}\n");
		return sb.toString();
	}

	@Override
	public List<String> dfs(String start)
	{
		List<Integer> idxResult = new ListOnJavaArrayList<Integer>();
		dfsRecursive(vertexMap.value(start), idxResult);
		List<String> result = new ListOnJavaArrayList<String>();
		for (Integer idx : idxResult)
			result.add(intVertexMap.value(idx));
		return result;
	}

	public void dfsRecursive(int start, List<Integer> list)
	{
		list.add(start);
		for (int dstIdx = 0; dstIdx < this.adjList.get(start).size(); dstIdx++)
		{
			// if there is an edge and the dstIdx node has not been visited yet
			if (this.adjList.get(start).get(dstIdx) != null
					&& list.indexOf(this.adjList.get(start).get(dstIdx).dest) < 0)
				dfsRecursive(this.adjList.get(start).get(dstIdx).dest, list);
		}
	}

	/**
	 * 
	 * @param start
	 */
	public ArrayList<List<Edge>> breadthFirstSearch(String start, String dst)
	{
		// a queue of vertices
		ListQueue<Vertex> q = new ListQueue<Vertex>();

		ArrayList<List<Edge>> bfs = new ArrayList<List<Edge>>();

		// an array of visited vertices, corresponding with the adjList
		boolean[] visited = new boolean[this.adjList.size()];

		// marks start as visited
		visited[this.vertexMap.value(start)] = true;

		// adds start vertex to q
		Vertex v = new Vertex(this.adjList.get(this.vertexMap.value(start)), this.vertexMap.value(start));
		q.enqueue(v);

		// while the queue, q, is not empty
		while (q.size() > 0)
		{
			// A vertex, represented by a list of edges
			v = q.dequeue();

			// for each edge in the current Vertex V
			for (Edge E : v.data)
			{
				if (!visited[E.dest])
				{
					//creates a new vertex at the end of each edge and adds it to queue as well as the visited array
					Vertex n = new Vertex(v, this.adjList.get(E.dest), E.dest);
					q.enqueue(n);
					visited[E.dest] = true;
					//if the destination is found
					if(E.dest == this.vertexMap.value(dst))
					{
						//adds all of the previous vertices to an array list and returns it
						ArrayList<List<Edge>> c = new ArrayList<List<Edge>>();
						c.add(n.data);
						while(n.prev != null)
						{
							c.add(n.prev.data);
							n = n.prev;
						}
						return c;
					}
				}
			}
		}
		//return an empty list if it fails
		return bfs;
	}

	public ArrayList<List<Edge>> shortestPath(String start, String dst)
	{
		ArrayList<List<Edge>> bfs = this.breadthFirstSearch(start, dst);
		return bfs;
		
	}
}
