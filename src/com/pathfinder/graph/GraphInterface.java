package com.pathfinder.graph;

import com.pathfinder.graph.exception.VertexNotInGraphException;
import com.pathfinder.graph.exception.GraphException;

/**
 * An interface for methods for an adjacency-set graph
 */
public interface GraphInterface {

	/**
	 * Finds the shortest path between the start and end vertices.
	 * @param start -- The starting node
	 * @param end -- The target node
	 * @return A list representing the desired path, or NULL of the vertices are not connected.
	 * @throws GraphException if start and end are not in the graph
	 */
	Iterable<Vertex> computePath(Vertex start, Vertex end) throws GraphException;
	
	/**
	 * adds a single vertex v 
	 * @param v -- The vertex to be added
	 */
	void addVertex(Vertex v);
	
	/**
	 * Adds a single vertex with edges (v, neighbor) for each neighbor
	 * @param v -- The vertex to be added
	 * @param neighbors -- The neighbors of the v
	 * @throws VertexNotInGraphException if neighbors are not in the graph
	 */
	void addVertex(Vertex v, Iterable<Vertex> neighbors) throws VertexNotInGraphException;
	
	/**
	 * Removes v from the Graph
	 * @param v -- The vertex to be removed
	 * @throws VertexNotInGraphException if v is not in the graph
	 */
	void removeVertex(Vertex v) throws VertexNotInGraphException;
	
	/**
	 * Adds an edge between v1 and v2 in the graph
	 * @param v1 -- The first vertex the edge is incident to
	 * @param v2 -- The second vertex the edge is incident to
	 * @throws VertexNotInGraphException if v1 or v2 are not in the graph
	 */
	void addEdge(Vertex v1, Vertex v2) throws VertexNotInGraphException;
	
	/**
	 * Removes the edge (v1, v2) in the graph
	 * @param v1 -- The first vertex the edge is incident to
	 * @param v2 -- The second vertex the edge is incident to
	 * @throws VertexNotInGraphException if v1 or v2 are not in the graph
	 */
	void removeEdge(Vertex v1, Vertex v2) throws VertexNotInGraphException;
}
