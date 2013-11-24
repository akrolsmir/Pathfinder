package imageProc;

import org.opencv.core.Point;

class PixelLoc{
	double x, y; 
	public PixelLoc(double x, double y){
		this.x = x; this.y = y;
	}
	
	public PixelLoc(Point pt){
		this.x = pt.x; this.y = pt.y;
	}
	
	public boolean equals(PixelLoc other){
		return other.x == this.x && other.y == this.y;
	}
	
	public int hashCode(){
		return 1000*(int)this.y + (int)this.x;
	}
	
	public String toString(){
		return "(" + x + ", " + y + ")";
	}
}
