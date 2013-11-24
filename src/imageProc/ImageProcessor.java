package imageProc;

import java.util.HashMap;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import android.util.Log;

import com.pathfinder.graph.Graph;
import com.pathfinder.graph.Vertex;
import com.pathfinder.graph.exception.VertexNotInGraphException;

public class ImageProcessor {
	static int low_thresh = 0;
	static int high_thresh = 100;
	static double rho = 100;
	static double theta = 180;

	public static Graph process(Mat image) {
		Log.d("ImageProcessor", "calculating image of " + image.cols() + " x " + image.rows());
		Mat edgeDetect = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
		MatOfPoint lines = new MatOfPoint();
//		Mat lines = new Mat();
		Imgproc.Canny(image, edgeDetect, low_thresh, high_thresh);
		Log.d("TAG", "input edge image with channels: " + edgeDetect.channels() + " of size " + edgeDetect.rows() + " x " + edgeDetect.cols());
		Imgproc.HoughLinesP(edgeDetect, lines, 1, Math.PI/180, 100, 0, 0 );	
		Log.d("TAG", "Starting graph computations");
		return exportGraph(lines);
	}
	
	private static Graph exportGraph(Mat lines){
		HashMap<PixelLoc, Vertex> vertices = new HashMap<PixelLoc, Vertex>();
		Graph g = new Graph();
		Point[] edges = ((MatOfPoint) lines).toArray();
		for (int i = 0; i < edges.length; i++) {
			Point pt1 = new Point(), pt2 = new Point();
			double a = Math.cos(theta), b = Math.sin(theta);
			double x0 = a * rho, y0 = b * rho;
			pt1.x = Math.round(x0 + 1000 * (-b));
			pt1.y = Math.round(y0 + 1000 * (a));
			pt2.x = Math.round(x0 - 1000 * (-b));
			pt2.y = Math.round(y0 - 1000 * (a));
			
			PixelLoc pixel1 = new PixelLoc(pt1);
			PixelLoc pixel2 = new PixelLoc(pt2);
			Vertex v1 = new Vertex(pt1.x, pt1.y);
			Vertex v2 = new Vertex(pt2.x, pt2.y);
			
			if(!vertices.containsKey(pixel1)){
				vertices.put(pixel1, v1);
				g.addVertex(v1);
			}
			
			if(!vertices.containsKey(pixel2)){
				vertices.put(pixel2, v2);
				g.addVertex(v2);
			}
			v1 = vertices.get(pixel1); v2 = vertices.get(pixel2);
			try {
				g.addEdge(v1, v2, dist(pixel1, pixel2));
			} catch (VertexNotInGraphException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return g;
	}
	
	static double dist(PixelLoc p1, PixelLoc p2){
		double sqXDist = Math.pow(p1.x - p2.x, 2);
		double sqYDist = Math.pow(p1.y - p2.y, 2);
		return Math.sqrt(sqXDist + sqYDist);
	}

}
