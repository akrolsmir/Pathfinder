package com.pathfinder.graph.exception;

/**
 * Exception to be called if a vertex cannot be found in a graph.
 */
public class VertexNotInGraphException extends GraphException {

	/**
	 * Autogenerated serial ID
	 */
	private static final long serialVersionUID = 1891536331377530770L;

	//default parameterless constructor
	public VertexNotInGraphException() {}
	
	//Standard message constructor
	public VertexNotInGraphException(String msg){
		super(msg);
	}
}
