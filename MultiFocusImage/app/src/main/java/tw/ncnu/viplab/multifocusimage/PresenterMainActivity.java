package tw.ncnu.viplab.multifocusimage;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


import java.util.ArrayList;
import java.util.List;

import tw.ncnu.viplab.multifocusimage.ImageProcessing.ControlImage;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.ImageBasicProcessing;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.WatershedSegmenter;
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

    //FeatureDetector detector;
    //DescriptorExtractor descriptorExtractor;
    //DescriptorMatcher descriptorMatcher;

    public PresenterMainActivity(ImageView processedImage1, ImageView processedImage2) {
        processedImageView1 = processedImage1;
        processedImageView2 = processedImage2;

        inputMat1 = InputImage(processedImage1);
        inputMat2 = InputImage(processedImage2);
        imageBasicProcessing1 = new ImageBasicProcessing(inputMat1);
        imageBasicProcessing2 = new ImageBasicProcessing(inputMat2);
    }

    private Mat InputImage(ImageView inputImage) {
        return ControlImage.ConvertImageViewToMat(inputImage);
    }

    private void ShowImage(ImageView inputImage, Mat processedMat) {
        ControlImage.ConvertMatToBitmap(processedMat, inputImage);
    }

    public void Progress2016DecWeek4() {
//        //Convert Image to gray Image
        processedMat1 = imageBasicProcessing1.ConvertToGrayMat(inputMat1);
        processedMat2 = imageBasicProcessing2.ConvertToGrayMat(inputMat2);

        //Reference document: http://docs.opencv.org/trunk/d3/db4/tutorial_py_watershed.html
        Imgproc.threshold(processedMat1, processedMat1, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        Imgproc.threshold(processedMat2, processedMat2, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);

        //# noise removal
        Mat kernel = zeros(3, 3, CvType.CV_8U);
        Mat opening1 = new Mat();
        Mat opening2 = new Mat();
//        Imgproc.morphologyEx(processedMat1, opening, Imgproc.MORPH_OPEN, kernel, new Point(),2);
        Imgproc.morphologyEx(processedMat1, opening1, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(processedMat2, opening2, Imgproc.MORPH_OPEN, kernel);

        //# sure background area
        Mat sure_bg1 = new Mat(processedMat1.size(), CvType.CV_8U);
        Mat sure_bg2 = new Mat(processedMat2.size(), CvType.CV_8U);
        Imgproc.dilate(opening1, sure_bg1, kernel, new Point(), 3);
        Imgproc.dilate(opening2, sure_bg2, kernel, new Point(), 3);

        //# Finding sure foreground area
        Mat distTransform1 = new Mat();
        Mat distTransform2 = new Mat();
        Imgproc.distanceTransform(opening1, distTransform1, Imgproc.DIST_L2, 5);
        Imgproc.distanceTransform(opening2, distTransform2, Imgproc.DIST_L2, 5);
        Mat sure_fg1 = new Mat(processedMat1.size(), CvType.CV_8U);
        Mat sure_fg2 = new Mat(processedMat2.size(), CvType.CV_8U);
        Imgproc.threshold(distTransform1, sure_fg1, 0.7 * Core.minMaxLoc(distTransform1).maxVal, 255, 0);
        Imgproc.threshold(distTransform2, sure_fg2, 0.7 * Core.minMaxLoc(distTransform2).maxVal, 255, 0);

        //# Finding unknown regions
//        Core.subtract(sure_bg1, sure_fg1, processedMat1);
//        Core.subtract(sure_bg2, sure_fg2, processedMat2);
        Core.subtract(sure_bg1, sure_fg1, processedMat1, new Mat(), CvType.CV_8U);
        Core.subtract(sure_bg2, sure_fg2, processedMat2, new Mat(), CvType.CV_8U);

        //Show Image
        ShowImage(processedImageView1, processedMat1);
        ShowImage(processedImageView2, processedMat2);
    }

    public void Progress2017JanWeek1() {
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
            Mat threeChannel = new Mat();
            Imgproc.cvtColor(mRgba, threeChannel, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(threeChannel, threeChannel, 100, 255, Imgproc.THRESH_BINARY);

            // Eliminate noise and smaller objects
            Mat fg = new Mat(mRgba.size(), CvType.CV_8U);
            Imgproc.erode(threeChannel, fg, new Mat(), new Point(-1, -1), 2);

            // Identify image pixels without objects
            Mat bg = new Mat(mRgba.size(), CvType.CV_8U);
            Imgproc.dilate(threeChannel, bg, new Mat(), new Point(-1, -1), 3);
            Imgproc.threshold(bg, bg, 1, 128, Imgproc.THRESH_BINARY_INV);


            // Create markers image
            Mat marker = new Mat(mRgba.size(), CvType.CV_8U, new Scalar(0));
            Core.add(fg, bg, marker);


//            //Convert Image to gray Image
//            result = imageBasicProcessing1.ConvertToGrayMat(mRgba);
//
//            //Reference document: http://docs.opencv.org/trunk/d3/db4/tutorial_py_watershed.html
//            Imgproc.threshold(result, result, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
//
//            //# noise removal
//            Mat kernel = zeros(3,3,CvType.CV_8U);
//            Mat opening = new Mat();
//            Imgproc.morphologyEx(result, opening, Imgproc.MORPH_OPEN, kernel);
//
//            //# sure background area
//            Mat sure_bg = new Mat(result.size(),CvType.CV_8U);
//            Imgproc.dilate(opening,sure_bg,kernel,new Point(),3);
//
//            //# Finding sure foreground area
//            Mat distTransform = new Mat();
//            Imgproc.distanceTransform(opening,distTransform,Imgproc.DIST_L2,5);
//            Mat sure_fg = new Mat(result.size(),CvType.CV_8U);
//            Imgproc.threshold(distTransform, sure_fg, 0.7*Core.minMaxLoc(distTransform).maxVal,255,0);
//
//            //# Finding unknown regions
//            Mat marker = new Mat(mRgba.size(), CvType.CV_8U, new Scalar(0));
//            Core.add(sure_fg, sure_bg, marker , new Mat(), CvType.CV_8U);

            // Create watershed segmentation object
            WatershedSegmenter.setMarkers(marker);
            result = WatershedSegmenter.process(mRgba);
        } catch (Exception e) {
            result = mRgba;
            Log.d("anbt", "Fail Apply WaterShed");
            e.printStackTrace();
        }
        return result;
    }

    public void Progress2017JanWeek3(){
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        DescriptorExtractor descriptorExtractor =  DescriptorExtractor.create(DescriptorExtractor.ORB);

        //detect the keypoints
        detector.detect(inputMat1,keypoints1);
        detector.detect(inputMat2,keypoints2);

        //detect decriptor
        Mat descriptors1 = new Mat();
        Mat descriptors2 = new Mat();
        descriptorExtractor.compute(inputMat1,keypoints1,descriptors1);
        descriptorExtractor.compute(inputMat2,keypoints2,descriptors2);

        processedMat1 = new Mat();
        processedMat2 = new Mat();

        Imgproc.cvtColor(inputMat1,processedMat1,Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(inputMat2,processedMat2, Imgproc.COLOR_RGBA2RGB);

        //Scalar in function drawKeypoints is color of Keypoint
        //DrawMatchesFlags
        //DEFAULT = 0, // Output image matrix will be created (Mat::create), i.e. existing memory of output image may be reused. Two source images, matches, and single keypoints will be drawn. For each keypoint, only the center point will be drawn (without a circle around the keypoint with the keypoint size and orientation).
        // DRAW_OVER_OUTIMG = 1, // Output image matrix will not be created (using Mat::create). Matches will be drawn on existing content of output image.
        // NOT_DRAW_SINGLE_POINTS = 2, // Single keypoints will not be drawn.
        // DRAW_RICH_KEYPOINTS = 4 // For each keypoint,
        // the circle around keypoint with keypoint size and orientation will be drawn.
        Features2d.drawKeypoints(processedMat1,keypoints1,processedMat1,new Scalar(255,255,0),4);
        Features2d.drawKeypoints(processedMat2,keypoints2,processedMat2,new Scalar(255,255,0),4);

//        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(4);
//        MatOfDMatch matches = new MatOfDMatch();
//        descriptorMatcher.match(descriptors1,descriptors2,matches);

//        //output Image
//        Mat outputImg = new Mat();
//        MatOfByte drawnMatches = new MatOfByte();
//        Features2d.drawMatches(inputMat1,keypoints1,inputMat2,keypoints2,matches,outputImg,new Scalar(255,0,0), new Scalar(0,255,0), drawnMatches, Features2d.NOT_DRAW_SINGLE_POINTS);

//        Mat H1 = Calib3d.findHomography(processedMat2, processedMat1);

        Log.d("trongan93","Image 1 - Descripter Size: " + descriptors1.size());
        Log.d("trongan93","Image 2 - Descripter Size: " + descriptors2.size());

       //Image Segmentation
        Mat processedMat1_Binary = new Mat();
        Mat processedMat2_Binary = new Mat();

        Mat processedMat1_detectedEdges = new Mat();
        Mat processedMat2_detectedEdges = new Mat();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.cvtColor(inputMat1, processedMat1_Binary, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.cvtColor(inputMat2, processedMat2_Binary, Imgproc.COLOR_RGB2GRAY);
        Imgproc.blur(processedMat1_Binary,processedMat1_detectedEdges, new Size(3,3));
        Imgproc.blur(processedMat2_Binary,processedMat2_detectedEdges, new Size(3,3));
        double threshold = 40;
        Imgproc.Canny(processedMat1_detectedEdges,processedMat1_detectedEdges,threshold,threshold*3,3,false);
        Imgproc.Canny(processedMat2_detectedEdges,processedMat2_detectedEdges,threshold,threshold*3,3,false);

//        Mat map2DetectedEdges = new Mat(processedMat1_detectedEdges.size(), CvType.CV_8U);

        float[][] T_Object = new float[processedMat1_detectedEdges.rows()][processedMat1_detectedEdges.cols()];//new process
        if(processedMat1_detectedEdges.size().equals(processedMat2_detectedEdges.size()))
        {
            Log.d("anbt","2 Edges Object is the same size");
            for(int row = 0; row < processedMat1_detectedEdges.rows(); row++){
                for(int col = 0 ; col < processedMat1_detectedEdges.cols();col++){
                    double valueNearFocus = processedMat1_detectedEdges.get(row,col)[0];
                    double valueFarFocus = processedMat2_detectedEdges.get(row,col)[0];
                    if(valueNearFocus > 0){
//                        map2DetectedEdges.put(row,col,0);
                        T_Object[row][col] = 1; //new process
                    }
                    else if(valueFarFocus > 0){
//                        map2DetectedEdges.put(row,col,255);
                        T_Object[row][col] = 0; //new process
                    }
                    else{
//                        map2DetectedEdges.put(row,col,128);
                        T_Object[row][col] = 0.5f;//new process
                    }
                }
            }
        }
        else
        {
            Log.d("anbt","2 Edges Object isn't the same size");
        }
//        printMatBinaryObject(map2DetectedEdges);
//        printMatrix(T_Object,processedMat1_detectedEdges.rows(),processedMat1_detectedEdges.cols());



//        Imgproc.findContours(processedMat1_detectedEdges.clone(),contours,hierarchy,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
//        hierarchy.release();
//
//        //draw Contours
//        Imgproc.drawContours(processedMat1_detectedEdges,contours,-1,new Scalar(Math.random()*255,Math.random()*255));
//
//        //create mask
//        Mat input1Mask = new Mat(inputMat1.rows(),inputMat1.cols(), CvType.CV_8U);
//        Imgproc.drawContours(input1Mask,contours,-1,new Scalar(Math.random()*255,Math.random()*255));

//        //Draw contours
//        Mat drawing = new Mat(inputMat1.size(),CvType.CV_8UC3, new Scalar(255,255,255));
//        for(int i = 0; i < contours.size(); i++){
//            Scalar color = new Scalar(Math.random()*255,Math.random()*255,Math.random()*255);
//            Imgproc.drawContours(drawing, contours, i, color, 2, 8, hierarchy, 0, new Point());
//        }
//        descriptorMatcher.match(descriptors1,drawing,matches);
        double SFValue = calculatorSF(inputMat1);
        Log.d("anbt","SF value: " + SFValue);


        ShowImage(processedImageView1, processedMat1_detectedEdges);
        ShowImage(processedImageView2, processedMat2_detectedEdges);
        // ShowImage(processedImageView2, drawing);
    }



    public void Progress2017FebWeek4(){
        //onTouchImageView(processedImageView1,processedImageView2);
    }
    public void onTouchImageView(){
        final List<Integer> RGBValueNearFocusTouched = new ArrayList<>();
        final List<Integer> RGBValueFarFocusTouched = new ArrayList<>();
        final Bitmap bitmapNearFocus = ((BitmapDrawable)processedImageView1.getDrawable()).getBitmap();
        final Bitmap bitmapFarFocus = ((BitmapDrawable)processedImageView2.getDrawable()).getBitmap();
        processedImageView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int pixel = bitmapNearFocus.getPixel((int)motionEvent.getX(),(int)motionEvent.getY());
                RGBValueNearFocusTouched.clear();
                RGBValueNearFocusTouched.add(Color.red(pixel));
                RGBValueNearFocusTouched.add(Color.green(pixel));
                RGBValueNearFocusTouched.add(Color.blue(pixel));
                Log.d("anbt","RGB Value touched of Near Focus Image: ["+ RGBValueNearFocusTouched.get(0) + "," + RGBValueNearFocusTouched.get(1) + "," + RGBValueNearFocusTouched.get(2) + "]");
                return false;
            }
        });
        processedImageView2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int pixel = bitmapFarFocus.getPixel((int)motionEvent.getX(),(int)motionEvent.getY());
                RGBValueFarFocusTouched.clear();
                RGBValueFarFocusTouched.add(Color.red(pixel));
                RGBValueFarFocusTouched.add(Color.green(pixel));
                RGBValueFarFocusTouched.add(Color.blue(pixel));
                Log.d("anbt","RGB Value touched of Far Focus Image: ["+ RGBValueFarFocusTouched.get(0) + "," + RGBValueFarFocusTouched.get(1) + "," + RGBValueFarFocusTouched.get(2) + "]");
                return false;
            }
        });
    }

    private double calculatorSF(Mat inputImage){
        double SF = 0;
        int rows = inputImage.rows();
        int cols = inputImage.cols();
        double squareRowFrequency = 0, squareColFrequency = 0;
        //N1 = rows; N2 = cols
        for(int row = 0; row < rows; row++){
            for(int col = 1; col < cols; col++){
                double pixelValueI1Row = inputImage.get(row,col)[0];//Only Get R value in Pixel Values
                double pixelValueI2Row = inputImage.get(row,col-1)[0];//Only Get R value in Pixel Values
                squareRowFrequency += Math.pow((pixelValueI1Row - pixelValueI2Row),2);
            }
        }
        squareRowFrequency = squareRowFrequency * (1f/(rows * cols));
        for(int row = 1; row <rows;row++){
            for(int col = 0; col <cols; col++){
                double pixelValueI1Col = inputImage.get(row,col)[0];//Only Get R value in Pixel Values
                double pixelValueI2Col = inputImage.get(row-1,col)[0];//Only Get R value in Pixel Values
                squareColFrequency += Math.pow((pixelValueI1Col-pixelValueI2Col),2);
            }
        }
        squareColFrequency = squareColFrequency * (1f/(rows * cols));
        SF = Math.sqrt(squareRowFrequency + squareColFrequency);
        return SF;
    }

    private void printMatBinaryObject(Mat inputBinaryMat){
        String result = "";
        for(int row = 0; row < inputBinaryMat.rows(); row++){
            for(int col = 0; col < inputBinaryMat.cols(); col++){
               result = String.format("%s %f", result, inputBinaryMat.get(row,col)[0]);
            }
            result = String.format("%s\n",result);
        }
        Log.d("anbt",result);
    }
    private void printMatrix(float[][] inputFloatMatrix, int rows, int cols){

        for(int row = 0; row < rows; row++){
            for(int col = 0; col < cols; col++){
                if(inputFloatMatrix[row][col] == 1){
                    Log.d("anbt", "Near" + inputFloatMatrix[row][col]);
                }
                else if(inputFloatMatrix[row][col] == 0){
                    Log.d("anbt", "Far" + inputFloatMatrix[row][col]);
                }
                else{
                    Log.d("anbt", "Mid" + inputFloatMatrix[row][col]);
                }
            }
        }
    }

    private static Mat minimusOfMat(Mat inputMatA, Mat inputMatB){
        Mat result = new Mat(inputMatA.size(),CvType.CV_8U);
        for(int row = 0; row < result.rows(); row++){
            for(int col = 0; col < result.cols(); col++){
                double[] pixelsValueA = inputMatA.get(row,col);
                double[] pixelsValueB = inputMatB.get(row,col);
                if(pixelsValueA.length == pixelsValueB.length){
                    double[] pixelsValueSum = new double[pixelsValueA.length];
                    for(int i = 0; i < pixelsValueA.length; i++){
                        pixelsValueSum[i] = pixelsValueA[i] + pixelsValueB[i];
                    }
                    result.put(row,col,pixelsValueSum);
                }
            }
        }
        return result;
    }
}
