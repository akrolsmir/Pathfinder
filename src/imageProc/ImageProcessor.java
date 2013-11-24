package imageProc;

import org.opencv.core.*;
import org.opencv.imgproc.*;

public class ImageProcessor {
	static double low_thresh = 0;
	static double high_thresh = 100;
	
	public static Graph process(Mat image){
		Mat edges = Mat.zeros(image.size(), image.type());
		Mat lines = null;
		Imgproc.Canny(image, edges, low_thresh, high_thresh);
		Imgproc.HoughLinesP(edges, lines, rho, theta, low_thresh);
		
		
		
		return null;
	}

}
