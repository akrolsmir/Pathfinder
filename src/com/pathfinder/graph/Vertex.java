package com.pathfinder.graph;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import com.pathfinder.graph.exception.EdgeNotInGraphException;

/**
 * Object representation of a vertex for a graph represented
 * with Adjacency-Lists. Contains helper methods for setting 
 */
public class Vertex {
	private Loc location;
	private Double dist;
	private Vertex prev;
	private boolean visited;
	
	/*
	 * Note: This may change to a HashTable if we really needed this to be thread-safe
	 */
	private Map<Vertex, Double> vertices;
	
	/**
	 * Constructs an unconnected Vertex at the origin.
	 */
	public Vertex(){
		location = new Loc();
		vertices = new HashMap<Vertex, Double>();
	}
	
	/**
	 * Construct an uncconected Vertex at (lat, lon)
	 * @param lat -- The latitude of the vertex
	 * @param lon -- The longitude of the vertex
	 */
	public Vertex(double lat, double lon){
		location = new Loc(lat, lon);
		vertices = new HashMap<Vertex, Double>();
	}
	
	/**
	 * Constructs a Vertex at LAT and LON connected to VERTS with edge weights 0.
	 * @param lat -- The latitude of the vertex
	 * @param lon -- The longitude of the vertex
	 * @param verts -- The vertices that will be connected.
	 */
	public Vertex(double lat, double lon, Iterable<Vertex> verts) throws EdgeNotInGraphException{
		location = new Loc(lat, lon);
		vertices = new HashMap<Vertex, Double>();
		if(verts != null){
			for (Vertex v : verts){
				setWeight(v, 0.0);
			}
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
	
	public Map<Vertex, Double> getVertices(){
		return vertices;
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
	 * @return the vertices adjacent to THIS
	 */
	public Set<Vertex> getAdjacent(){
		return vertices.keySet();
	}
	
	/**
	 * Makes v adjacent to THIS
	 * @param v -- a new adjacent vertex
	 * @param w -- The weight of (this, v)
	 */
	public void addAdjacent(Vertex v, double w){
		vertices.put(v, w);
		v.addOtherAdjacent(this, w);
	}
	
	/**
	 * Helper method for addAdjacent (must update both adjacency lists)
	 * @param v -- The adjacent vertex
	 * @param w -- The weight of (this, v)
	 */
	private void addOtherAdjacent(Vertex v, double w){
		vertices.put(v, w);
	}
	
	/**
	 * Removes the adjacent vertex v
	 * @param v
	 */
	public void removeAdjacent(Vertex v) throws EdgeNotInGraphException{
		if(vertices.containsKey(v)){
			vertices.remove(v);
			v.removeOtherAdjacent(this);
		} else {
			throw new EdgeNotInGraphException("Attempted to remove nonexistant edge");
		}
	}
	
	private void removeOtherAdjacent(Vertex v) throws EdgeNotInGraphException{
		if(vertices.containsKey(v)){
			vertices.remove(v);
		} else {
			throw new EdgeNotInGraphException("Attempted to remove nonexistant edge");
		}
	}
	
	/** 
	 * 
	 * @return distance used in computePath
	 */
	protected Double getDist(){
		return dist;
	}
	
	/**
	 * Sets this vertex's distance for use in computePath
	 * @param d -- the new distance
	 */
	protected void setDist(Double d){
		dist = d;
	}
	
	/**
	 * 
	 * @return previous vertex used in computePath
	 */
	protected Vertex getPrev(){
		return prev;
	}
	
	/**
	 * Sets the previous vertex for use in computePath
	 * @param v -- the previous vertex
	 */
	protected void setPrev(Vertex v){
		prev = v;
	}
	
	/**
	 * 
	 * @return the "visited" state of the vertex
	 */
	public boolean getVisited(){
		return visited;
	}
	
	/**
	 * Sets the "visited" state of this vertex to v
	 * @param v -- the new "visited" state
	 */
	public void setVisited(boolean v){
		visited = v;
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
	
	public int hashCode(){
		//a bad hashcode please fix me please please please
		//TODO
		return (int)(1000*location.getLatitude()*location.getLongitude());
	}
}
