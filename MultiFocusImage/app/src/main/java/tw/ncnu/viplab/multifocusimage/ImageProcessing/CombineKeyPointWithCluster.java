package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import android.util.Log;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.Features2d;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Bui Trong An on 11/22/2017.
 * VipLab
 * trongan93@gmail.com
 */

public class CombineKeyPointWithCluster {
    MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
    List<KeyPoint> keypoints = new ArrayList<>();
    Mat clusterMat = new Mat();
    Mat matWithKeyPointLocation = new Mat();
    Mat result = new Mat();
    int colNum, rowNum = 0;
    double[] white = new double[]{255,255,255};
    public CombineKeyPointWithCluster(Mat clusterMat, MatOfKeyPoint matOfKeyPoint){
        this.matOfKeyPoint = matOfKeyPoint;
        this.keypoints = matOfKeyPoint.toList();
        this.clusterMat = clusterMat;
        colNum = clusterMat.cols();
        rowNum = clusterMat.rows();
        GetRegionWithKeyPoint();
//        result = matWithKeyPointLocation;
//        MapClusterAndKeyPoints(matWithKeyPointLocation,clusterMat);
        MapClusterAndKeyPoints2(matWithKeyPointLocation,clusterMat);

    }
    private void GetRegionWithKeyPoint(){
        Mat whiteMat = new Mat(clusterMat.rows(),clusterMat.cols(),clusterMat.type(),new Scalar(255,255,255));
        Features2d.drawKeypoints(whiteMat,matOfKeyPoint,matWithKeyPointLocation,new Scalar(0,0,0),2);
    }
    private void MapClusterAndKeyPoints(Mat keypointMat, Mat clusterMat){
        List<double[]> valueInfoClusterInKeypoints = new ArrayList<>();

        int count = 0;

//        String whiteString = white.toString();
        for(int c = 0; c < colNum; c++){
            for(int r = 0; r < rowNum; r++){
                double[] keypointMatValue = keypointMat.get(r,c);
//                Log.d("anbt","double size = " + keypointMatValue.length);

//                if(keypointMatValue[0] != 255 && keypointMatValue[1] != 255 && keypointMatValue[2] != 255){
//                    valueInfoClusterInKeypoints.add(clusterMat.get(r,c));
//                    count++;
//                }

                if(Arrays.equals(keypointMatValue,white) == false){
                    valueInfoClusterInKeypoints.add(clusterMat.get(r,c));
                    count++;
                }

//                if(keypointMatValue.toString().equals(whiteString) == false)
//                {
//                    valueInfoClusterInKeypoints.add(clusterMat.get(r,c));
//                    count++;
//                }



            }
        }
        //Delete duplicate data in list
//        Set<double[]> hs = new HashSet<>();
        HashSet<double[]> hs = new HashSet<>();
        hs.addAll(valueInfoClusterInKeypoints);
        valueInfoClusterInKeypoints.clear();
        valueInfoClusterInKeypoints.addAll(hs);
//        Log.d("anbt", "random print value in list: [" + valueInfoClusterInKeypoints.get(7440)[0]+"]["+valueInfoClusterInKeypoints.get(7440)[1]+"]["+valueInfoClusterInKeypoints.get(7440)[2]);
        Log.d("anbt","count of keypoint: " + count);
//        Log.d("anbt","count of list: " + valueInfoClusterInKeypoints.size());
        Mat newClusterAfterFilter = new Mat(clusterMat.size(),clusterMat.type());
        int cc =0;
        for(int i = 0; i <= valueInfoClusterInKeypoints.size();i++){
            for(int c = 0; c < colNum; c++) {
                for (int r = 0; r < rowNum; r++) {
                    double[] clusterValue = clusterMat.get(r,c);
                    if(Arrays.equals(clusterValue,valueInfoClusterInKeypoints.get(i)) == false){
                        newClusterAfterFilter.put(r,c,white);
                    }
                    else{
                        newClusterAfterFilter.put(r,c,clusterValue);
                    }
                }
            }
//            Log.d("anbt","pass: " + cc++);
        }
        result = newClusterAfterFilter;
    }

    private void MapClusterAndKeyPoints2(Mat keypointMat, Mat clusterMat) {
        List<double[]> keyPointsValue = new ArrayList<>();

        int count = 0;
        Log.d("anbt", "colNum = " + rowNum);
        for(int r = 0; r < rowNum; r++) {
            for(int c = 0; c < colNum; c++){
                if(Arrays.equals(keypointMat.get(r,c),white) == false)
                {
//                    if(keyPointsValue.contains(clusterMat.get(r,c)) == false)
                    if(CheckContainArrayInLIst(keyPointsValue,clusterMat.get(r,c)) == false)
                    {
                        keyPointsValue.add(clusterMat.get(r,c));
                    }
                    count++;
                }
            }
        }
        Log.d("anbt", "count2 = " + count);
        Log.d("anbt", "size of keypointsValue = " + keyPointsValue.size());
        result = clusterMat;
    }


    private boolean CheckContainArrayInLIst(List<double[]> listArrayDouble, double[] arrayDouble){
        for (double[] containInList: listArrayDouble) {
            if(containInList[0] == arrayDouble[0] && containInList[1] == arrayDouble[1] && containInList[2] == arrayDouble[2]){
                return true;
            }
        }
        return false;
    }

    public Mat GetResult(){
        return result;
    }




}
