package com.pathfinder.graph;

/**
 * Custom exception for graphs
 */
public class VertexNotInGraphException extends Exception {
	
	/**
	 * Default Serializable ID
	 */
	private static final long serialVersionUID = 1L;

	//default parameterless constructor
	public VertexNotInGraphException() {}
	
	//Standard message constructor
	public VertexNotInGraphException(String msg){
		super(msg);
	}
}
