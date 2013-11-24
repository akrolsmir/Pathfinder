package com.pathfinder.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.pathfinder.graph.exception.GraphException;
import com.pathfinder.graph.exception.VertexNotInGraphException;

/**
 *  Adjacency-set representation of a map. Abstractly paths are edges and stores/rooms are vertices
 */
public class Graph implements GraphInterface{
	private Set<Vertex> vertices;
	
	
	/**
	 * Constructs an empty graph
	 */
	public Graph(){
		vertices = new HashSet<Vertex>();
	}
	
	/**
	 * Constructs a graph from some Iterable of vertices, assuming that the vertices
	 * already have information stored about their neighbors.
	 * @param verts -- The vertices of the graph
	 */
	public Graph(Iterable<Vertex> verts){
		vertices = new HashSet<Vertex>();
		// May want to do some sort of checking for valid edges
		for (Vertex v : verts){
			vertices.add(v);
		}
	}
	
	/**
	 * Constructs a graph containing VERTS, filling in information for VERTS with
	 * NEIGHBORS
	 * @param verts -- The vertices of the graph
	 * @param neighbors -- Pairs of adjacent vertices.
	 */
	public Graph(ArrayList<Vertex> verts, ArrayList<Pair<Vertex, Vertex>> edges, ArrayList<Double> weights) 
			throws GraphException, IndexOutOfBoundsException{
		vertices = new HashSet<Vertex>();
		for(Vertex v : verts){
			vertices.add(v);
		}
		for(int i = 0; i < edges.size(); i++){
			if(!(vertices.contains(edges.get(i).getLeft()) && vertices.contains(edges.get(i).getRight()))){
				throw new GraphException("Bad Graph construction");
			} else {
				addEdge(edges.get(i).getLeft(), edges.get(i).getRight(), weights.get(i));
			}
		}
	}
	
	public ArrayList<Vertex> computePath(Vertex start, Vertex end) throws GraphException{
		 /*
		  * We will proceed using Dijkstra's algorithm
		  * Adapted from Algorithms by Dasgupta, Papadimitriou, Vazirani
		  */
		if(!vertices.contains(start)){
			throw new VertexNotInGraphException("Vertex: " + start.toString() + " is not in graph");
		} else if (!vertices.contains(end)){
			throw new VertexNotInGraphException("Vertex: " + start.toString() + " is not in the graph");
		}
		//vertexInfo will store for each vertex v the pair (dist(v), prev(v))
		for (Vertex v : vertices){
			v.setDist(Double.MAX_VALUE);
			v.setPrev(null);
			v.setVisited(true);
		}
		start.setDist(0.0);
		PriorityQueue<Vertex> H = new PriorityQueue<Vertex>(vertices.size(), new CompareDist());
		for(Vertex v : vertices){
			H.add(v);
		}
		while(H.size() != 0){
			Vertex small = H.poll();
			small.setVisited(true);
			for (Vertex adj : small.getAdjacent()){
				if(!adj.getVisted() && adj.getDist() > small.getDist() + small.getWeight(adj)){
					adj.setDist(small.getDist() + small.getWeight(adj));
					adj.setPrev(small);
					H.add(adj);
				}
			}
		}
		//Process result of Dijkstra's into a list
		Vertex curr = end;
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		path.add(0, end);
		while(curr != start){
			path.add(0, curr.getPrev());
			curr = curr.getPrev();
		}
		return path;
	}


	public void addVertex(Vertex v){
		vertices.add(v);
	}
	

	public void addVertex(Vertex v, ArrayList<Vertex> neighbors, ArrayList<Double> weights) 
			throws VertexNotInGraphException, IndexOutOfBoundsException{
		vertices.add(v);
		for(int i = 0; i < neighbors.size(); i++){
			if(!vertices.contains(neighbors.get(i))){
				vertices.remove(v);
				throw new VertexNotInGraphException("Vertex is not in the graph");
			} else {
				v.addAdjacent(neighbors.get(i), weights.get(i));
			}
		}
	}
	
	public void removeVertex(Vertex v) throws GraphException{
		if(!vertices.contains(v)){
			throw new VertexNotInGraphException("Vertex " + v.toString() + " is not in the graph");
		} else {
			for(Vertex adj : v.getAdjacent()){
				v.removeAdjacent(adj);
			}
			vertices.remove(v);
		}
	}

	public void addEdge(Vertex v1, Vertex v2, Double w) throws VertexNotInGraphException{
		if(!(vertices.contains(v1) && vertices.contains(v2))){
			throw new VertexNotInGraphException("Vertices are not in graph.");
		} else {
			v1.addAdjacent(v2, w);
		}
	}
	
	public void removeEdge(Vertex v1, Vertex v2) throws GraphException{
		if(!(vertices.contains(v1) && vertices.contains(v2))){
			throw new VertexNotInGraphException("Vertices are not in graph");
		} else {
			v1.removeAdjacent(v2);
		}
	}
	
	public Iterator<Vertex> getVertices(){
		return vertices.iterator();
	}
}

class CompareDist implements Comparator<Vertex>{

	public int compare(Vertex arg0, Vertex arg1) throws NullPointerException{
		if(arg0 == null && arg1 == null){
			throw new NullPointerException("Vertex does not exist");
		}
		return Double.compare(arg0.getDist(), arg1.getDist());
	}
}
