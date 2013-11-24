package com.pathfinder.graph;

/**
 * Class for representing pairs of objects of the same type.
 * @param <T> The type of object to be stored
 */
public class Pair<T> {
	private final T left;
	private final T right;
	
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
	public Pair(T l, T r){
		left = l;
		right = r;
	}
	
	/**
	 * 
	 * @return The left entry of the pair
	 */
	public T getLeft(){
		return left;
	}
	
	/**
	 * 
	 * @return The right entry of the pair
	 */
	public T getRight(){
		return right;
	}
}
