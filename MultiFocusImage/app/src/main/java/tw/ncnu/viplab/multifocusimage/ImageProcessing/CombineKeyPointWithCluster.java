package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import android.util.Log;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;

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
        GetRegionWithKeyPoint();
    }
    private void GetRegionWithKeyPoint(){
        for (KeyPoint keypoint : keypoints) {
            Point point = keypoint.pt;
            Log.d("anbt","Keypoint location: " + point.x + " : " + point.y);
        }

    }
    public Mat GetResult(){
        return result;
    }
}
