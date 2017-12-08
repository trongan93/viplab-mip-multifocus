package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bui Trong An on 11/22/2017.
 * VipLab
 * trongan93@gmail.com
 */

public class CombineKeyPointWithCluster {
    List<KeyPoint> keypoints = new ArrayList<>();
    Mat clusterMat = new Mat();
    Mat result = new Mat();
    public CombineKeyPointWithCluster(Mat clusterMat, MatOfKeyPoint matOfKeyPoint){
        this.keypoints = matOfKeyPoint.toList();
        this.clusterMat = clusterMat;
//        GetRegionWithKeyPoint();
        result = matOfKeyPoint;

    }
    private void GetRegionWithKeyPoint(){
        /*Option 1
        Map Cluster with Mat after convert from Points
         */
        List<Point> points = new ArrayList<>();
        for (KeyPoint keypoint : keypoints) {
            Point point = keypoint.pt;
            Log.d("anbt","Keypoint location: " + point.x + " : " + point.y);
            points.add(point);
        }
        Mat nearKeypointsMat = MapPointToMat(points);
        result = nearKeypointsMat;


    }
    public Mat GetResult(){
        return result;
    }
    private Mat MapPointToMat(List<Point> points)
    {
        Mat result = Converters.vector_Point2f_to_Mat(points);
//        return result;
        Mat newResult = new Mat();
        result.convertTo(newResult, CvType.CV_8U);
        return newResult;
    }
}
