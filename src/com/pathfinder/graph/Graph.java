package com.pathfinder.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.PriorityQueue;

import android.util.SparseArray;

import com.pathfinder.graph.exception.EdgeNotInGraphException;
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
			v.setVisited(false);
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
				if(!adj.getVisited() && adj.getDist() > small.getDist() + small.getWeight(adj)){
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
			if (curr == null){
				return null;
			}
		}
		return path;
	}
	
	public Iterable<Vertex> computePathToGraph(Loc start, Vertex end)
			throws GraphException {
		Pair<Vertex, Double> close = closestVertexToPath(start);
		Vertex newVertex = new Vertex(start.getLatitude(), start.getLongitude());
		addVertex(newVertex);
		addEdge(close.getLeft(), newVertex, close.getRight());
		edges.remove(edges.size()-1);
		edges.remove(edges.size()-1);
		edges.remove(edges.size()-1);
		edges.remove(edges.size()-1);
		try{
			ArrayList<Vertex> path = computePath(close.getLeft(), end);
			removeVertex(newVertex);
			return path;
		} catch(GraphException g){
			throw g;
		}
		
		
	}

	public Pair<Vertex, Double> closestVertexToPath(Loc pos) {
		double dist = Double.MAX_VALUE;
		Vertex res = null;
		for (Vertex v : vertices){
			//might do some nearest neighbors thing (eg. 10x10 square)
			double temp = v.getLoc().computeDist(pos);
			if (temp < dist){
				res = v;
				dist = temp;
			}
		}
		return new Pair<Vertex, Double>(res, dist);
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
//				v.removeAdjacent(adj);
				adj.removeAdjacent(v);
			}
			vertices.remove(v);
		}
	}
	
	public ArrayList<Float> edges = new ArrayList<Float>();

	public void addEdge(Vertex v1, Vertex v2, Double w) throws VertexNotInGraphException{
		
		edges.add((float) v1.getLoc().getLatitude());
		edges.add((float) v1.getLoc().getLongitude());
		edges.add((float) v2.getLoc().getLatitude());
		edges.add((float) v2.getLoc().getLongitude());
		
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
	
	public Iterable<Vertex> getVertices(){
		return vertices;
	}
	
	public void takeStrongestSubgraph(){
		SparseArray<Vertex> vertexMap = new SparseArray <Vertex>();
		HashMap<Vertex, Integer> intMap = new HashMap<Vertex, Integer>();
		int i = 0;
		for(Vertex v : vertices){
			vertexMap.put(i, v);
			intMap.put(v, i);
			i++;
		}
		
		DisjointSets disJ = new DisjointSets(i+1);
		
		for (Vertex v : vertices) {
			for (Vertex other : v.getVertices().keySet()) {
				disJ.union(disJ.find(intMap.get(v)), disJ.find(intMap.get(other)));
			}
		}
		
		Set<Vertex> toRemove = new HashSet<Vertex>();
		
		int root = disJ.largestSet();
		for(Vertex v : vertices){
			if(disJ.find(intMap.get(v)) != root){
				toRemove.add(v);
			}
		}
		
		for(Vertex v : toRemove)
			try {
				this.removeVertex(v);
			} catch (GraphException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public String toString(){
		String verts = "";
		for(Vertex v : vertices){
			verts += v.toString() + "\n";
		}
		String edges = "";
		for(Vertex v : vertices){
			for (Vertex u : v.getAdjacent()){
				try {
					edges += v.toString() + " " + u.toString() + " " + v.getWeight(u) + "\n";
				} catch (EdgeNotInGraphException e) {
					//do nothing
				}
			}
		}
		return "Vertices:\n" + verts + "Edges:\n" + edges;
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