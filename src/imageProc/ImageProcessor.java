package imageProc;

import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.os.Environment;
import android.util.Log;

import com.pathfinder.graph.Graph;
import com.pathfinder.graph.Vertex;
import com.pathfinder.graph.exception.VertexNotInGraphException;

public class ImageProcessor {
	static int low_thresh = 80;
	static int high_thresh = 100;
	static int canny_low_thresh = 10;
	static int canny_high_thresh = 300;
	static int minLineLen = 2;
	static int maxLineGap = 80;
	static double rho = 100;
	static double theta = 180;
	static String fileName = Environment.getExternalStorageDirectory().getPath() + "/pathfinder_image_binary.jpg";
	static String vertexImg = Environment.getExternalStorageDirectory().getPath() + "/pathfinder_vertexImg.jpg";
	static String edgeImg = Environment.getExternalStorageDirectory().getPath() + "/pathfinder_edgeImg.jpg";
	
	public static Graph process(Mat image) {
		Log.d("ImageProcessor", "calculating image of " + image.cols() + " x " + image.rows());
		Mat edgeDetect = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
		MatOfPoint lines = new MatOfPoint();
		Mat grayscaleMat = new Mat(image.size(), CvType.CV_8U);
//		Imgproc.cvtColor( image, grayscaleMat, Imgproc.COLOR_BGRA2GRAY );
		Imgproc.threshold(image, grayscaleMat, high_thresh, high_thresh, Imgproc.THRESH_BINARY);
		Highgui.imwrite(fileName, grayscaleMat);
//		Mat lines = new Mat();
//		Imgproc.Canny(image, edgeDetect, low_thresh, high_thresh);
		Imgproc.Canny(grayscaleMat, edgeDetect, canny_low_thresh, canny_high_thresh);
		Highgui.imwrite(fileName, edgeDetect);
		Log.d("TAG", "input edge image with channels: " + edgeDetect.channels() + " of size " + edgeDetect.rows() + " x " + edgeDetect.cols());
		int i = 0;
		do{
			Imgproc.HoughLinesP(edgeDetect, lines, 1, Math.PI/180, low_thresh - i, minLineLen, maxLineGap );			
			i+=5;
		} while(lines.cols() < 3 && low_thresh - i > 0);
		
		while (lines.cols() > 100){
			Imgproc.HoughLinesP(edgeDetect, lines, 1, Math.PI/180, low_thresh - i, minLineLen, maxLineGap );			
			i-=5;
		}
		Log.d("TAG", "Starting graph computations with lineMat: " + lines.cols() + " x " + lines.rows());
		return exportGraph(lines, image);
	}
	
	private static Graph exportGraph(Mat lines, Mat image){
		Mat vertexMat = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
		Mat edgeMat = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
		HashMap<PixelLoc, Vertex> vertices = new HashMap<PixelLoc, Vertex>();
		Graph g = new Graph();
//		Point[] edges = ((MatOfPoint) lines).toArray();
		for (int i = 0; i < lines.cols(); i++) {
//			Point pt1 = new Point(), pt2 = new Point();
			double[] ptPair = lines.get(0, i);
			Point pt1 = new Point(ptPair[0], ptPair[1]), pt2 = new Point(ptPair[2], ptPair[3]);
//			if(i == 1) Log.d("PtVals","Point values are " + ptPair[0] +"+"+ ptPair[1] +"+"+ ptPair[2] +"+"+ ptPair[3]);
//			double theta = 1;
			
//			Log.d("Type", "out is " + lines.get(0, i).length);
//			double a = Math.cos(theta), b = Math.sin(theta);
//			double x0 = a * rho, y0 = b * rho;
//			pt1.x = Math.round(x0 + 1000 * (-b));
//			pt1.y = Math.round(y0 + 1000 * (a));
//			pt2.x = Math.round(x0 - 1000 * (-b));
//			pt2.y = Math.round(y0 - 1000 * (a));
			
			PixelLoc pixel1 = new PixelLoc(pt1);
			PixelLoc pixel2 = new PixelLoc(pt2);
			Vertex v1 = new Vertex(pt1.x, pt1.y);
			Vertex v2 = new Vertex(pt2.x, pt2.y);
			
			if(!vertices.containsKey(pixel1)){
				vertices.put(pixel1, v1);
				g.addVertex(v1);
				Core.rectangle(vertexMat, pt1, new Point(pt1.x + 1, pt1.y+1), new Scalar(255.0, 255.0, 255.0));
			}
			
			if(!vertices.containsKey(pixel2)){
				vertices.put(pixel2, v2);
				g.addVertex(v2);
				Core.rectangle(vertexMat, pt2, new Point(pt2.x + 1, pt2.y+1), new Scalar(255.0, 255.0, 255.0));
			}
			v1 = vertices.get(pixel1); v2 = vertices.get(pixel2);
			try {
				g.addEdge(v1, v2, dist(pixel1, pixel2));
				Log.d("Graph info", "new edge between " + pixel1 + " and " + pixel2);
				Core.line(edgeMat, pt1,pt2, new Scalar(255.0, 255.0, 255.0), 5);
				
			} catch (VertexNotInGraphException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
//		Highgui.imwrite(edgeImg, edgeMat);
//		Highgui.imwrite(vertexImg, vertexMat);
		if(lines.cols() < 50) g.takeStrongestSubgraph();
		outGraph(g, image);
		return g;
	}
	
	private static void outGraph(Graph g, Mat image){
		Mat vertexMat = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
		Mat edgeMat = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
		for (Vertex v : g.getVertices()) {
			Point pt = new Point(v.getLoc().getLatitude(), v.getLoc().getLongitude());
			Core.rectangle(vertexMat, pt, new Point(pt.x + 1, pt.y+1), new Scalar(255.0, 255.0, 255.0));
			for (Vertex other : v.getAdjacent()){
				Point otherPt = new Point(other.getLoc().getLatitude(), other.getLoc().getLongitude());
				Core.line(edgeMat, pt,otherPt, new Scalar(255.0, 255.0, 255.0), 5);
			}
		}
		
		Highgui.imwrite(edgeImg, edgeMat);
		Highgui.imwrite(vertexImg, vertexMat);
		
	}
	
	static double dist(PixelLoc p1, PixelLoc p2){
		double sqXDist = Math.pow(p1.x - p2.x, 2);
		double sqYDist = Math.pow(p1.y - p2.y, 2);
		return Math.sqrt(sqXDist + sqYDist);
	}

}