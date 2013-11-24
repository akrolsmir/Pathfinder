package com.pathfinder.graph;

import java.util.Map;
import java.util.HashMap;

import com.pathfinder.graph.exception.EdgeNotInGraphException;

/**
 * Object representation of a vertex for a graph represented
 * with Adjacency-Lists. Contains helper methods for setting 
 */
public class Vertex {
	private Loc location;
	
	/*
	 * Note: This may change to a HashTable if we really needed this to be thread-safe
	 */
	private Map<Vertex, Double> vertices;
	
	/**
	 * Constructs an unconnected Vertex at the origin.
	 */
	public Vertex(){
		location = new Loc();
	}
	
	/**
	 * Constructs a Vertex at LAT and LON connected to VERTS with edge weights 0.
	 * @param lat -- The latitude of the vertex
	 * @param lon -- The longitude of the vertex
	 * @param verts -- The vertices that will be connected.
	 */
	public Vertex(double lat, double lon, Iterable<Vertex> verts){
		location = new Loc(lat, lon);
		vertices = new HashMap<Vertex, Double>();
		for (Vertex v : verts){
			vertices.put(v, 0.0);
		}
	}
	
	/**
	 * Sets a new weight W of the edge(this, V). Throws IndexOutOfBoundsException if
	 * V is not adjacent to this.
	 * @param v -- The adjacent vertex.
	 * @param w -- The new weight of the vertex
	 * @throws IndexOutOfBoundsException -- Thrown if v is not adjacent to this.
	 */
	public void setWeight(Vertex v, double w) throws EdgeNotInGraphException{
		if(vertices.containsKey(v)){
			vertices.put(v, w);
			try{
				v.setOtherWeight(this, w);
			} catch (IndexOutOfBoundsException e){
				throw new EdgeNotInGraphException("Vertex " + v.toString() + " is not in graph"); //TODO update msg
			}
		} else {
			throw new EdgeNotInGraphException("Vertex " + v.toString() + " is not in graph"); //TODO update msg
		}
	}
	
	/**
	 * Helper method for setWeight(Vertex, double), since if we need to set the weight
	 * of of an edge we need to set it in both vertices' adjacency lists.
	 * @param v -- The adjacent vertex
	 * @param w -- The new weight of the vertex
	 * @throws IndexOutOfBoundsException -- Thrown if v is not adjacent to this.
	 */
	private void setOtherWeight(Vertex v, double w) throws EdgeNotInGraphException{
		if(vertices.containsKey(v)){
			vertices.put(v, w);
		} else {
			throw new EdgeNotInGraphException("Vertex " + v.toString() + " is not in graph"); // TODO update msg
		}
	}
	
	/**
	 * 
	 * @param v -- the vertex that is adjacent to THIS.
	 * @return -- the weight of the edge (this, V)
	 * @throws IndexOutOfBoundsException -- Thrown if v is not adjecent to this.
	 */
	public double getWeight (Vertex v) throws EdgeNotInGraphException{
		if(vertices.containsKey(v)){
			return vertices.get(v);
		} else{
			throw new EdgeNotInGraphException("Vertex " + v.toString() + " is not in graph"); //TODO update msg
		}
	}
	
	/**
	 * 
	 * @return -- The current location of the vertex
	 */
	public Loc getLoc(){
		return location;
	}
	
	//
	public String toString(){
		return location.toString();
	}
}
