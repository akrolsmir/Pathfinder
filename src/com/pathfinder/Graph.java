package com.pathfinder;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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
	public Graph(Iterable<Vertex> verts, Iterable<Pair<Vertex>> neighbors){
		//TODO
		vertices = new HashSet<Vertex>();
	}
	

	public Iterable<Vertex> computePath(Vertex start, Vertex end) throws VertexNotInGraphException{
		//TODO
		return new ArrayList<Vertex>();
	}
	

	public void addVertex(Vertex v){
		//TODO
	}
	

	public void addVertex(Vertex v, Iterable<Vertex> neighbors) throws VertexNotInGraphException{
		//TODO
	}
	
	public void removeVertex(Vertex v) throws VertexNotInGraphException{
		//TODO
	}

	public void addEdge(Vertex v1, Vertex v2) throws VertexNotInGraphException{
		//TODO
	}
	
	public void removeEdge(Vertex v1, Vertex v2) throws VertexNotInGraphException{
		//TODO
	}
}
