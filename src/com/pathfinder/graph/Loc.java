package com.pathfinder.graph;

/**
 * Wrapper class for the latitude and longitude.
 * Although there is a Location class in the Andriod API, we
 * do not really need it for the purposes of the graph.
 */
public class Loc {
	private double latitude, longitude;
	
	/**
	 * Constructs a Loc at the origin
	 */
	public Loc(){
		latitude = 0;
		longitude = 0;
	}
	
	/**
	 * Constructs a Loc at (lat, lon)
	 * @param lat -- Latitude
	 * @param lon -- Longitude
	 */
	public Loc(double lat, double lon){
		latitude = lat;
		longitude = lon;
	}
	
	/**
	 * Updates my coordinates
	 * @param lat -- new latitude
	 * @param lon -- new longitude
	 */
	public void update(double lat, double lon){
		latitude = lat;
		longitude = lon;
	}
	
	/**
	 * 
	 * @return -- the current latitude
	 */
	public double getLatitude(){
		return latitude;
	}
	
	/**
	 * 
	 * @return -- the current longitude
	 */
	public double getLongitude(){
		return longitude;
	}
	
	public String toString(){
		return "(" + latitude + " " + longitude + ")";
	}
	
}
