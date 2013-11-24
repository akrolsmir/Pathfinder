package com.pathfinder.graph;

/**
 * Class for representing pairs of objects of the same type.
 * @param <K> The type of the left object
 * @param <V> The type of the right object
 */
public class Pair<K, V> {
	private K left;
	private V right;
	
	/**
	 * Constructs a new Pair with null entries
	 */
	public Pair(){
		left = null;
		right = null;
	}
	
	/**
	 * Constructs a pair with entries l and r
	 * @param l -- the left entry
	 * @param r -- the right entry
	 */
	public Pair(K l, V r){
		left = l;
		right = r;
	}
	
	public void setValues(K l, V r){
		left = l;
		right = r;
	}
	
	/**
	 * 
	 * @return The left entry of the pair
	 */
	public K getLeft(){
		return left;
	}
	
	/**
	 * 
	 * @return The right entry of the pair
	 */
	public V getRight(){
		return right;
	}
}
