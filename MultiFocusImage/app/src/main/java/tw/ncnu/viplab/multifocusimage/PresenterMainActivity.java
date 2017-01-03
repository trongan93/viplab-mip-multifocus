package tw.ncnu.viplab.multifocusimage;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.lang.reflect.Array;

import tw.ncnu.viplab.multifocusimage.ImageProcessing.ControlImage;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.ImageBasicProcessing;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.WatershedSegmenter;

import static org.opencv.core.Mat.ones;
import static org.opencv.core.Mat.zeros;

/**
 * Created by Bui Trong An on 12/26/2016.
 * VipLab
 * trongan93@gmail.com
 */

public class PresenterMainActivity {
    Mat inputMat1, inputMat2;
    ImageBasicProcessing imageBasicProcessing1, imageBasicProcessing2;
    Mat processedMat1, processedMat2;
    ImageView processedImageView1, processedImageView2;

    //Variable for detector
    FeatureDetector detector;
    DescriptorExtractor descriptorExtractor;
    DescriptorMatcher descriptorMatcher;

    public PresenterMainActivity(ImageView processedImage1, ImageView processedImage2){
        processedImageView1 = processedImage1;
        processedImageView2 = processedImage2;

        inputMat1 = InputImage(processedImage1);
        inputMat2 = InputImage(processedImage2);
        imageBasicProcessing1 = new ImageBasicProcessing(inputMat1);
        imageBasicProcessing2 = new ImageBasicProcessing(inputMat2);
    }
    private Mat InputImage(ImageView inputImage){
        return ControlImage.ConvertImageViewToMat(inputImage);
    }

    private void ShowImage(ImageView inputImage, Mat processedMat){
         ControlImage.ConvertMatToBitmap(processedMat, inputImage);
    }

    public void Progress2016DecWeek4(){
//        //Convert Image to gray Image
        processedMat1 = imageBasicProcessing1.ConvertToGrayMat(inputMat1);
        processedMat2 = imageBasicProcessing2.ConvertToGrayMat(inputMat2);

        //Reference document: http://docs.opencv.org/trunk/d3/db4/tutorial_py_watershed.html
        Imgproc.threshold(processedMat1, processedMat1, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        Imgproc.threshold(processedMat2, processedMat2, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        //# noise removal
        Mat kernel = zeros(3,3,CvType.CV_8U);
        Mat opening1 = new Mat();
        Mat opening2 = new Mat();
//        Imgproc.morphologyEx(processedMat1, opening, Imgproc.MORPH_OPEN, kernel, new Point(),2);
        Imgproc.morphologyEx(processedMat1, opening1, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(processedMat2, opening2, Imgproc.MORPH_OPEN, kernel);

        //# sure background area
        Mat sure_bg1 = new Mat(processedMat1.size(),CvType.CV_8U);
        Mat sure_bg2 = new Mat(processedMat2.size(),CvType.CV_8U);
        Imgproc.dilate(opening1,sure_bg1,kernel,new Point(),3);
        Imgproc.dilate(opening2,sure_bg2,kernel,new Point(),3);

        //# Finding sure foreground area
        Mat distTransform1 = new Mat();
        Mat distTransform2 = new Mat();
        Imgproc.distanceTransform(opening1,distTransform1,Imgproc.DIST_L2,5);
        Imgproc.distanceTransform(opening2, distTransform2, Imgproc.DIST_L2,5);
        Mat sure_fg1 = new Mat(processedMat1.size(),CvType.CV_8U);
        Mat sure_fg2 = new Mat(processedMat2.size(),CvType.CV_8U);
        Imgproc.threshold(distTransform1, sure_fg1, 0.7*Core.minMaxLoc(distTransform1).maxVal,255,0);
        Imgproc.threshold(distTransform2, sure_fg2, 0.7*Core.minMaxLoc(distTransform2).maxVal,255,0);

        //# Finding unknown regions
//        Core.subtract(sure_bg1, sure_fg1, processedMat1);
//        Core.subtract(sure_bg2, sure_fg2, processedMat2);
        Core.subtract(sure_bg1, sure_fg1, processedMat1 , new Mat(), CvType.CV_8U);
        Core.subtract(sure_bg2, sure_fg2, processedMat2, new Mat(), CvType.CV_8U);

        //Show Image
        ShowImage(processedImageView1, processedMat1);
        ShowImage(processedImageView2, processedMat2);
    }

    public void Progress2017JanWeek1(){
//        detector = FeatureDetector.create(FeatureDetector.ORB);
//        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//        descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//
//
//        MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();
//        MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();
//        detector.detect(inputMat1,keyPoint1);
//        detector.detect(inputMat2,keyPoint2);


//        Mat markerMask = new Mat();
//        Mat marker = new Mat();
//        Imgproc.cvtColor(inputMat1, markerMask, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.watershed(inputMat1, marker);
//        marker.convertTo(marker,CvType.CV_8U);
//        ShowImage(processedImageView1, marker);


        //Apply watershed
        processedMat1 = applyWaterShed(inputMat1);
        processedMat2 = applyWaterShed(inputMat2);
        //Show Image
        ShowImage(processedImageView1, processedMat1);
        ShowImage(processedImageView2, processedMat2);


    }
    private Mat applyWaterShed(Mat mRgba) {
        Mat result;
        try {
            ///Old Process
//            Mat threeChannel = new Mat();
//            Imgproc.cvtColor(mRgba, threeChannel, Imgproc.COLOR_BGR2GRAY);
//            Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY);
//
//            // Eliminate noise and smaller objects
//            Mat fg = new Mat(mRgba.size(), CvType.CV_8U);
//            Imgproc.erode(threeChannel, fg, new Mat(), new Point(-1, -1), 2);
//
//            // Identify image pixels without objects
//            Mat bg = new Mat(mRgba.size(), CvType.CV_8U);
//            Imgproc.dilate(threeChannel, bg, new Mat(), new Point(-1, -1), 3);
//            Imgproc.threshold(bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV);
//
//            // Create markers image
//            Mat marker = new Mat(mRgba.size(), CvType.CV_8U, new Scalar(0));
//            Core.add(fg, bg, marker);


            //New process
            //Convert Image to gray Image
            ImageBasicProcessing imageBasicProcessing = new ImageBasicProcessing(mRgba);
            result = imageBasicProcessing.ConvertToGrayMat(mRgba);

            //Reference document: http://docs.opencv.org/trunk/d3/db4/tutorial_py_watershed.html
            Imgproc.threshold(result, result, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

            //# noise removal
            Mat kernel = zeros(3,3,CvType.CV_8U);
            Mat opening = new Mat();
            Imgproc.morphologyEx(result, opening, Imgproc.MORPH_OPEN, kernel);

            //# sure background area
            Mat sure_bg = new Mat(result.size(),CvType.CV_8U);
            Imgproc.dilate(opening,sure_bg,kernel,new Point(),3);

            //# Finding sure foreground area
            Mat distTransform = new Mat();
            Imgproc.distanceTransform(opening,distTransform,Imgproc.DIST_L2,5);
            Mat sure_fg = new Mat(result.size(),CvType.CV_8U);
            Imgproc.threshold(distTransform, sure_fg, 0.7*Core.minMaxLoc(distTransform).maxVal,255,0);

            //# Finding unknown regions
            //Mat marker = new Mat(mRgba.size(), CvType.CV_8U, new Scalar(0));
            //Core.add(sure_fg, sure_bg, marker , new Mat(), CvType.CV_8U);
            Mat unknow = new Mat();
            Core.absdiff(sure_bg,sure_fg,unknow);

            //#Marker labelling
            Mat markers = new Mat();
            Imgproc.connectedComponents(sure_bg, markers);

            Imgproc.watershed(result, markers);


            //Apply watershed
            // Create watershed segmentation object
//            WatershedSegmenter.setMarkers(marker);
//            result = WatershedSegmenter.process(mRgba);
        } catch (Exception e) {
            result = mRgba;
            Log.d("anbt","Fail Apply WaterShed");
            e.printStackTrace();
        }
        return result;
    }

}
