package com.pathfinder.graph.junit;

import static org.junit.Assert.*;

import org.junit.Test;

import com.pathfinder.graph.*;
import com.pathfinder.graph.exception.*;
import java.util.ArrayList;
import java.util.Iterator;
public class TestGraph {
	
	/*
	 * Test Constructors
	 */
	@Test
	public void foo(){
		fail("no");
	}
	@Test
	public void testConstructorOne(){
		Graph foo = new Graph();
		assertTrue("Graph.vertices should be non-null", foo.getVertices() != null);
		assertFalse("Graph.vertices should have a size of 0", foo.getVertices().hasNext());
	}
	@Test
	public void testConstructorTwo(){
		ArrayList<Vertex> verts = new ArrayList<Vertex>();
		verts.add(new Vertex());
		try{
			verts.add(new Vertex(3, 4, null));
			verts.add(new Vertex(1, 2, null));
			verts.add(new Vertex(5.1, 2, null));
			verts.add(new Vertex(99.243, 102.23, null));
			verts.add(new Vertex(34, 643, null));
			verts.add(new Vertex(.1, 102, null));
		} catch (EdgeNotInGraphException e){
			fail("Exception should not be caught");
		}
		Graph foo = new Graph(verts);
		Iterator<Vertex> graphVerts = foo.getVertices();
		assertTrue("Graph.vertices should be non-null", graphVerts != null);
		assertTrue("Graph.vertices should have a size > 0", graphVerts.hasNext());
		graphVerts.next();
		assertTrue("Graph vertices should have a size > 1", graphVerts.hasNext());
		graphVerts.next();
		assertTrue("Graph.vertices should have a size > 2", graphVerts.hasNext());
		graphVerts.next();
		assertTrue("Graph vertices should have a size > 3", graphVerts.hasNext());
		graphVerts.next();
		assertTrue("Graph.vertices should have a size > 4", graphVerts.hasNext());
		graphVerts.next();
		assertTrue("Graph vertices should have a size > 5", graphVerts.hasNext());
		graphVerts.next();
		assertFalse("Graph vertices should have a size = 6", graphVerts.hasNext());
	}
}
