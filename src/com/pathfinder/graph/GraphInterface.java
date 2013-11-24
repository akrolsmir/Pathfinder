package com.pathfinder.graph;

import java.util.ArrayList;

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
	 * adds a single unconnected vertex v 
	 * @param v -- The vertex to be added
	 */
	void addVertex(Vertex v);
	
	/**
	 * Adds a single vertex with edges (v, neighbor) for each neighbor
	 * @param v -- The vertex to be added
	 * @param neighbors -- The neighbors of v
	 * @param weights -- The corresponding weights of each (v, neighbor)
	 * @throws VertexNotInGraphException if neighbors are not in the graph
	 */
	void addVertex(Vertex v, ArrayList<Vertex> neighbors, ArrayList<Double> weights) throws VertexNotInGraphException;
	
	/**
	 * Removes v from the Graph
	 * @param v -- The vertex to be removed
	 * @throws VertexNotInGraphException if v is not in the graph
	 */
	void removeVertex(Vertex v) throws GraphException;
	
	/**
	 * Adds an edge between v1 and v2 in the graph
	 * @param v1 -- The first vertex the edge is incident to
	 * @param v2 -- The second vertex the edge is incident to
	 * @param w -- The weight of the edge
	 * @throws VertexNotInGraphException if v1 or v2 are not in the graph
	 */
	void addEdge(Vertex v1, Vertex v2, Double w) throws VertexNotInGraphException;
	
	/**
	 * Removes the edge (v1, v2) in the graph
	 * @param v1 -- The first vertex the edge is incident to
	 * @param v2 -- The second vertex the edge is incident to
	 * @throws VertexNotInGraphException if v1 or v2 are not in the graph
	 */
	void removeEdge(Vertex v1, Vertex v2) throws GraphException;
}
