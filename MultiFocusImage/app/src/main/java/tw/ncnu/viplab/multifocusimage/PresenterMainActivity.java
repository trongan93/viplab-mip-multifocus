package tw.ncnu.viplab.multifocusimage;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;
import org.opencv.utils.Converters;


import java.util.ArrayList;
import java.util.List;

import tw.ncnu.viplab.multifocusimage.ImageProcessing.ControlImage;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.ImageBasicProcessing;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.ImageCombine2Regions;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.ImageSegmentation;

import static org.opencv.core.Core.BORDER_DEFAULT;
import static org.opencv.core.CvType.CV_32F;
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
        Mat waterShedMat1 = applyWaterShed(inputMat1);
        Mat waterShedMat2 = applyWaterShed(inputMat2);
        //Show Image
        ShowImage(processedImageView1, waterShedMat1);
        ShowImage(processedImageView2, waterShedMat2);


    }

    private Mat applyWaterShed(Mat mRgba) {
        Mat result;
        try {
//            Imgproc.cvtColor(mRgba,mRgba, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(mRgba, mRgba, 40, 120, Imgproc.THRESH_BINARY);
            Mat marker = new Mat(mRgba.size(), CvType.CV_8U);
            Imgproc.watershed(mRgba,marker);
            result = marker;

//            // Eliminate noise and smaller objects
//            Mat fg = new Mat(mRgba.size(), CvType.CV_8U);
//            Imgproc.erode(mRgba, fg, new Mat(), new Point(-1, -1), 2);
//
//            // Identify image pixels without objects
//            Mat bg = new Mat(mRgba.size(), CvType.CV_8U);
//            Imgproc.dilate(mRgba, bg, new Mat(), new Point(-1, -1), 3);
//            Imgproc.threshold(bg, bg, 0, 128, Imgproc.THRESH_BINARY_INV);


            // Create markers image
//            Mat marker = new Mat(mRgba.size(), CvType.CV_8U);
//            Core.add(fg, bg, marker);


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
//            WatershedSegmenter.setMarkers(marker);
//            result = WatershedSegmenter.process(mRgba);
        } catch (Exception e) {
            result = mRgba;
            Log.d("anbt", "Fail Apply WaterShed");
            e.printStackTrace();
        }
        return result;
    }

    public void MainProcess(){
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

        ShowImage(processedImageView1,processedMat1);
        ShowImage(processedImageView2,processedMat2);

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

       //Image Segmentation


        //Edge detection
        Mat processedMat1_detectedEdges = new Mat();
        Mat processedMat2_detectedEdges = new Mat();
        processedMat1_detectedEdges = EdgeDetector(inputMat1);
        processedMat2_detectedEdges = EdgeDetector(inputMat2);
//        ShowImage(processedImageView1, processedMat1_detectedEdges);
//        ShowImage(processedImageView2, processedMat2_detectedEdges);

//        Mat map2DetectedEdges = new Mat(processedMat1_detectedEdges.size(), CvType.CV_8U);

        //Defin T Object
//        float[][] T_Object = new float[processedMat1_detectedEdges.rows()][processedMat1_detectedEdges.cols()];//new process
//        if(processedMat1_detectedEdges.size().equals(processedMat2_detectedEdges.size()))
//        {
//            Log.d("anbt","2 Edges Object is the same size");
//            for(int row = 0; row < processedMat1_detectedEdges.rows(); row++){
//                for(int col = 0 ; col < processedMat1_detectedEdges.cols();col++){
//                    double valueNearFocus = processedMat1_detectedEdges.get(row,col)[0];
//                    double valueFarFocus = processedMat2_detectedEdges.get(row,col)[0];
//                    if(valueNearFocus > 0){
////                        map2DetectedEdges.put(row,col,0);
//                        T_Object[row][col] = 1; //new process
//                    }
//                    else if(valueFarFocus > 0){
////                        map2DetectedEdges.put(row,col,255);
//                        T_Object[row][col] = 0; //new process
//                    }
//                    else{
////                        map2DetectedEdges.put(row,col,128);
//                        T_Object[row][col] = 0.5f;//new process
//                    }
//                }
//            }
//        }
//        else
//        {
//            Log.d("anbt","2 Edges Object isn't the same size");
//        }


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


        //Calculate SF
//        double SFValue = calculatorSF(inputMat1);
//        Log.d("anbt","SF value: " + SFValue);

//        ShowImage(processedImageView1, processedMat1);
//        ShowImage(processedImageView2, processedMat2);

//        ShowImage(processedImageView1, processedMat1_detectedEdges);
//        ShowImage(processedImageView2, processedMat2_detectedEdges);
        // ShowImage(processedImageView2, drawing);

//        ShowImage(processedImageView1, processedMat1_WaterShedApplied);
//        ShowImage(processedImageView2, processedMat2_WaterShedApplied);

//        //Apply detection
//        Mat destinationMat1 = new Mat();
//        Mat destinationMat2 = new Mat();
//        applyDetection(inputMat1, destinationMat1);
//        applyDetection(inputMat2, destinationMat2);
//        ShowImage(processedImageView1, destinationMat1);
//        ShowImage(processedImageView2, destinationMat2);

        //Check Keypoint
        double[] RGBs = new double[3];

//        Mat mat1ForFillDetect = new Mat();
//        Mat mat2ForFillDetect = new Mat();
//        Imgproc.cvtColor(inputMat1, mat1ForFillDetect, Imgproc.COLOR_RGBA2GRAY);
//        Imgproc.cvtColor(inputMat2, mat2ForFillDetect, Imgproc.COLOR_RGBA2GRAY);
//        DrawingKeyPointFeatureTheSameRGB(mat1ForFillDetect,keypoints1);
//        DrawingKeyPointFeatureTheSameRGB(mat2ForFillDetect, keypoints2);
//        ShowImage(processedImageView1, mat1ForFillDetect);
//        ShowImage(processedImageView2, mat2ForFillDetect);


//        Mat matKeypoints1 = MappingListPointsToMat(keypoints1);
//        Mat matKeypoints2 = MappingListPointsToMat(keypoints1);
//        ShowImage(processedImageView1, matKeypoints1);
//        ShowImage(processedImageView2, matKeypoints2);

        //Detect Object by Color
//        Mat mat1DetectByColor = new Mat();
//        Mat mat2DetectByColor = new Mat();
//        mat1DetectByColor = DetectObjectByColor(inputMat1,new Scalar(0,100,100));
//        mat2DetectByColor = DetectObjectByColor(inputMat2, new Scalar(0,100,100));
//        ShowImage(processedImageView1, mat1DetectByColor);
//        ShowImage(processedImageView2, mat2DetectByColor);

        //Detect Object by Color
//        Mat colorMap1 = new Mat();
//        Mat colorMap2 = new Mat();
//        colorMap1 = GetColorMap(inputMat1);
//        colorMap2 = GetColorMap(inputMat2);
//        ShowImage(processedImageView1,colorMap1);
//        ShowImage(processedImageView2, colorMap2);

        Mat objectTrackingByColorMat1 = new Mat();
        Mat objectTrackingByColorMat2 = new Mat();
        objectTrackingByColorMat1 = ObjectTrackingByColor(inputMat1);
        objectTrackingByColorMat2 = ObjectTrackingByColor(inputMat2);
        ShowImage(processedImageView1,objectTrackingByColorMat1);
        ShowImage(processedImageView2,objectTrackingByColorMat2);

        //De Noising
//        Mat deNoisingImage1 = new Mat();
//        Mat deNoisingImage2 = new Mat();
//        deNoisingImage1 = DeNoisingImage(inputMat1);
//        deNoisingImage2 = DeNoisingImage(inputMat2);
//        ShowImage(processedImageView1, deNoisingImage1);
//        ShowImage(processedImageView2, deNoisingImage2);

        //Gradient 2nd
//        Mat processedMat1_gradient2 = new Mat();
//        Mat processedMat2_gradient2 = new Mat();
//        processedMat1_gradient2 = Gradient2nd(inputMat1);
//        processedMat2_gradient2 = Gradient2nd(inputMat2);
//        ShowImage(processedImageView1, processedMat1_gradient2);
//        ShowImage(processedImageView2, processedMat2_gradient2);

        //Apply watershed to detect region
        //Best apply WaterShed
        Mat waterShedMatInput1 = inputMat1.clone();
        Mat waterShedMatInput2 = inputMat2.clone();
        Mat appliedWaterShed1 = new Mat();
        Mat appliedWaterShed2 = new Mat();
        appliedWaterShed1 = StepToWaterShed(waterShedMatInput1);
        appliedWaterShed2 = StepToWaterShed(waterShedMatInput2);
//        ShowImage(processedImageView1, appliedWaterShed1);
//        ShowImage(processedImageView2, appliedWaterShed2);

//        ShowImage(processedImageView1,inputMat1);
//        ShowImage(processedImageView2,inputMat2);
        //Map Gradient 1st
        Mat processMat1_edgeStrenght = new Mat();
        Mat processMat2_edgeStrenght = new Mat();
        processMat1_edgeStrenght = CalculateMapStrength(inputMat1);
        processMat1_edgeStrenght = FilterMapStrength(processMat1_edgeStrenght);
        processMat2_edgeStrenght = CalculateMapStrength(inputMat2);
        processMat2_edgeStrenght = FilterMapStrength(processMat2_edgeStrenght);
//        ShowImage(processedImageView1, processMat1_edgeStrenght);
//        ShowImage(processedImageView2, processMat2_edgeStrenght);

        //Compile watershed result and map gradient result and ColorMap
//        Mat regionsOfImage1 = new Mat();
//        Mat regionsOfImage2 = new Mat();
//        regionsOfImage1 = DetectRegionOfImage(inputMat1, processMat1_edgeStrenght,appliedWaterShed1, colorMap1, processedMat1_detectedEdges);
//        regionsOfImage2 = DetectRegionOfImage(inputMat2, processMat2_edgeStrenght, appliedWaterShed2, colorMap2, processedMat2_detectedEdges);
//        ShowImage(processedImageView1,regionsOfImage1);
//        ShowImage(processedImageView2,regionsOfImage2);



//        Mat drawContoursMat1 = new Mat();
//        drawContoursMat1 = DrawContours(inputMat1,processMat1_edgeStrenght);
//        ShowImage(processedImageView1, drawContoursMat1);


        /**June 24 2017
         * Last Modifed: September 28th 2017
         * Image Segmentaion
         */
        Mat inputImageSegmentation1 = inputMat1.clone();
        Mat intputImageSegmentation2 = inputMat2.clone();
        Imgproc.cvtColor(inputImageSegmentation1,inputImageSegmentation1,Imgproc.COLOR_BGRA2BGR);
        Imgproc.cvtColor(intputImageSegmentation2,intputImageSegmentation2,Imgproc.COLOR_BGRA2BGR);
        Mat imageSegmentation1 = new Mat();
        Mat imageSegmentation2 = new Mat();
        ImageSegmentation ImageSegmetation1 = new ImageSegmentation(inputImageSegmentation1, processedMat1_detectedEdges, appliedWaterShed1);
        ImageSegmentation ImageSegmetation2 = new ImageSegmentation(intputImageSegmentation2, processMat2_edgeStrenght, appliedWaterShed2);
        imageSegmentation1 = ImageSegmetation1.GetResult();
        imageSegmentation2 = ImageSegmetation2.GetResult();


//        ShowImage(processedImageView1, imageSegmentation1);
//        ShowImage(processedImageView2, imageSegmentation2);

//        /**
//         * July 4th 2017
//         */
        ImageCombine2Regions Combine2Image = new ImageCombine2Regions(imageSegmentation1, imageSegmentation2);
        Mat result = Combine2Image.Combine2ImageWithRegion(inputMat1,inputMat2);
//
//        ShowImage(processedImageView1, Combine2Image.matResultAfterPreCombine);
//        ShowImage(processedImageView2, Combine2Image.matResultAfterPreCombine);
//
//        ShowImage(processedImageView1, result);
//        ShowImage(processedImageView2, result);

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
    private static Mat drawObjectWithKeypoints(MatOfKeyPoint keyPoints){
        Mat result = new Mat();

        return result;
    }
    public void applyDetection(Mat src, Mat dst) {
        if (dst != src) {
            src.copyTo(dst);
        }
        Mat img_gray,img_sobel, img_threshold, element;

        img_gray=new Mat();
        Imgproc.cvtColor(src, img_gray, Imgproc.COLOR_RGB2GRAY);

        img_sobel=new Mat();
        Imgproc.Sobel(img_gray, img_sobel, CvType.CV_8U, 1, 0, 3, 1, 0, BORDER_DEFAULT);

        img_threshold=new Mat();
        Imgproc.threshold(img_sobel, img_threshold, 0, 255, Imgproc.THRESH_OTSU+Imgproc.THRESH_BINARY);

        element=new Mat();
        element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3) );
        Imgproc.morphologyEx(img_threshold, img_threshold, Imgproc.MORPH_CLOSE, element);
        //Does the trick
        List<MatOfPoint>  contours=new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(img_threshold, contours, hierarchy, 0, 1);
        List<MatOfPoint> contours_poly=new ArrayList<MatOfPoint>(contours.size());
        contours_poly.addAll(contours);

        MatOfPoint2f mMOP2f1,mMOP2f2;
        mMOP2f1=new MatOfPoint2f();
        mMOP2f2=new MatOfPoint2f();

        for( int i = 0; i < contours.size(); i++ )

            if (contours.get(i).toList().size()>100)
            {
                contours.get(i).convertTo(mMOP2f1, CvType.CV_32FC2);
                Imgproc.approxPolyDP(mMOP2f1,mMOP2f2, 3, true );
                mMOP2f2.convertTo(contours_poly.get(i), CvType.CV_32S);
                Rect appRect=Imgproc.boundingRect(contours_poly.get(i));
                if (appRect.width>appRect.height)
                {
                    Imgproc.rectangle(dst, new Point(appRect.x,appRect.y) ,new Point(appRect.x+appRect.width,appRect.y+appRect.height), new Scalar(255,0,0));
                }
            }

    }
    public void DrawingKeyPointFeatureTheSameRGB(Mat matInput, MatOfKeyPoint matOfKeyPoint){
       //Draw match all keypoint
        KeyPoint[] keyPoints = matOfKeyPoint.toArray();
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < keyPoints.length; i++)
        {
            if(null != keyPoints[i].pt) {
                points.add(keyPoints[i].pt);
            }
        }


        for (int i = 0; i < points.size(); i++){
            if(i > 0){
                Imgproc.line(matInput,points.get(i-1),points.get(i),new Scalar(0,255,0));
            }


        }

//        //Define region
//        KeyPoint[] keyPoints = matOfKeyPoint.toArray();
//        List<Point> points = new ArrayList<>();
//        for (int i = 0; i < keyPoints.length; i++)
//        {
//            if(null != keyPoints[i].pt) {
//                points.add(keyPoints[i].pt);
//                for(int matOfKbCol = 0; matOfKbCol < matOfKeyPoint.cols(); matOfKbCol++){
//                    for (int matOfKbRow = 0; matOfKbRow < matOfKeyPoint.rows(); matOfKbRow++){
//
//                    }
//                }
//            }
//        }
    }
    private Mat MappingListPointsToMat(MatOfKeyPoint matOfKeyPoint){
        KeyPoint[] keyPoints = matOfKeyPoint.toArray();
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < keyPoints.length; i++)
        {
            if(null != keyPoints[i].pt) {
                points.add(keyPoints[i].pt);
            }
        }
//        Mat outputMat = Converters.vector_Point_to_Mat(points);
        Mat outputMat = new Mat();
        //Converters.vector_KeyPoint_to_Mat(matOfKeyPoint.toList()).convertTo(outputMat,CvType.CV_8U);
        outputMat = Converters.vector_Point2d_to_Mat(points);
        Mat output2 = new Mat();
        outputMat.convertTo(output2,CvType.CV_8UC1);
//        Imgproc.cvtColor(outputMat,outputMat,Imgproc.COLOR_GRAY2BGR);
//        Imgproc.cvtColor(outputMat,outputMat,Imgproc.COLOR);
        Log.d("anbt","Channel of Mat: " + outputMat.channels());
        return outputMat;
//        http://stackoverflow.com/questions/10137249/android-opencv-listkeypoint-to-mat
    }
    private Mat DetectObjectByColor(Mat input, Scalar scalar){
        Imgproc.cvtColor(input,input,Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(input,input,Imgproc.COLOR_RGB2HSV);
        Mat lower_red_hue_range = new Mat();
//        Mat upper_red_hue_range = new Mat();
        Core.inRange(input, new Scalar(0,100,100), new Scalar(350,255,255), lower_red_hue_range);
//        Core.inRange(input, new Scalar(0, 100, 100), new Scalar(10, 255, 255), lower_red_hue_range);
//        Core.inRange(input, new Scalar(10,255,255), new Scalar(0,100,100), lower_red_hue_range);
//        Core.inRange(input, new Scalar(160, 100, 100), new Scalar(179, 255, 255), upper_red_hue_range);
        return lower_red_hue_range;
    }
    private Mat GetColorMap(Mat inputMat) {
        Mat resultMat = inputMat.clone();
        Imgproc.cvtColor(inputMat, resultMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.applyColorMap(resultMat, resultMat, Imgproc.COLORMAP_JET);
        return resultMat;
    }
    private Mat DeNoisingImage(Mat inputMat){
        Imgproc.cvtColor(inputMat,inputMat, Imgproc.COLOR_RGBA2GRAY);
        Photo.fastNlMeansDenoising(inputMat, inputMat);
        return inputMat;
    }
    public Mat StepToWaterShed(Mat img) {
        Mat threeChannel = new Mat();
        Imgproc.cvtColor(img, threeChannel, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.threshold(threeChannel, threeChannel, 105, 255, Imgproc.THRESH_BINARY);
        Mat fg = new Mat(img.size(),CvType.CV_8U);
        int erosion_size = 6;
        Mat elementEroding = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
        Imgproc.erode(threeChannel,fg,elementEroding);
        Mat bg = new Mat(img.size(),CvType.CV_8U);
        int dilation_size = 6;
        Mat elementDilating = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
        Imgproc.dilate(threeChannel,bg,elementDilating);
        Imgproc.threshold(bg,bg, 1, 128,Imgproc.THRESH_BINARY_INV);
        Mat markers = new Mat(img.size(),CvType.CV_8U, new Scalar(0));
        Core.add(fg, bg, markers);

        WatershedSegmenter segmenter = new WatershedSegmenter();
        segmenter.setMarkers(markers);
//
        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGBA2BGR);
        Mat result1 = segmenter.process(img);
        return result1;
    }
    public class WatershedSegmenter {
        public Mat markers=new Mat();

        public void setMarkers(Mat markerImage)
        {
            markerImage.convertTo(markers, CvType.CV_32SC1);
        }

        public Mat process(Mat image)
        {
            Imgproc.watershed(image,markers);
            markers.convertTo(markers,CvType.CV_8U);
            return markers;
        }

    }
    private Mat EdgeDetector(Mat inputMat){
        Mat output = new Mat();
        Imgproc.cvtColor(inputMat, output, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.blur(output,output, new Size(3,3));
        double threshold = 40;
        //Gradient 1st
        Imgproc.Canny(output,output,40,120,3,false);//Old version 40 and 120; modified in June 27
//        Imgproc.Canny(output,output,42,255);
//        //Gradient 2nd
//        Imgproc.blur(inputMat, inputMat, new Size(5,5));
//        Imgproc.Canny(inputMat, inputMat, threshold, threshold*3,5,true);
        return output;
    }
    private Mat Gradient2nd(Mat inputMat){
//        Imgproc.Sobel(inputMat, inputMat, CvType.CV_8U, 1, 0, 5, 2, 0, BORDER_DEFAULT); //gradient X: dx = 1, dy = 0; gradient Y: dx = 0, dy = 1
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_8U;
        int maskSize = 5;
        Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Sobel(inputMat, inputMat, ddepth,0,1,maskSize,scale,delta);
        return inputMat;
    }
    private Mat CalculateMapStrength(Mat inputMat){
        Mat outputMat = new Mat();
        //Convert to grayscale
        Imgproc.cvtColor(inputMat,outputMat, Imgproc.COLOR_RGBA2GRAY);
        //Compute dx and dy derivatives
        Mat dx = new Mat();
        Mat dy = new Mat();
        Imgproc.Sobel(outputMat, dx, CV_32F, 1, 0);
        Imgproc.Sobel(outputMat, dy, CV_32F, 0, 1);
        Core.convertScaleAbs(dx,dx);
        Core.convertScaleAbs(dy,dy);
        //Compute gradient
//        Core.magnitude(dx, dy, inputMat);
        Mat resultMat = new Mat();
        Core.addWeighted(dx,0.5,dy,0.5,0,resultMat);
//        Core.magnitude(dx, dy, outputMat);
        return resultMat;
    }
    private Mat FilterMapStrength(Mat inputMat){
        Mat resultMat = new Mat();
        Imgproc.threshold(inputMat,resultMat,42,255,Imgproc.THRESH_BINARY);
        return resultMat;
    }
    private Mat DetectRegionOfImage(Mat inputMat, Mat mapStrenght, Mat markerWaterShed, Mat colorMap, Mat cannyDetected){
        Mat result = mapStrenght;
//        for(int r = 0; r < inputMat.rows(); r++){
//            for(int c = 0; c < inputMat.cols(); c++){
//                if(markerWaterShed.get(r,c)[0] == -1)
//                    Log.d("anbt","value marker: " + markerWaterShed.get(r,c)[0] );
//            }
//        }
//
////        for(int r = 0; r < inputMat.rows(); r++) {
////            for (int c = 0; c < inputMat.cols(); c++) {
////                if(mapStrenght.get(r,c)[0] == 255){
////                    result.put(r,c,colorMap.get(r,c));
////
////                }
////                else{
////                    result.put(r,c,0);
////                }
////            }
////        }
//        result = markerWaterShed;
//        Core.convertScaleAbs(markerWaterShed,result);

//        result = StepToWaterShed(mapStrenght);
//        Imgproc.Canny(markerWaterShed,result,45,150,3,false);
//        result = cannyDetected;
        return result;
//        //Draw
//
//        Mat drawing = zeros(inputMat.rows(),inputMat.cols(),CvType.CV_8UC3);
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
//        Mat hierarchy = new Mat();
//        // find contours:
//        Imgproc.findContours(cannyDetected, contours, hierarchy, Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
//        Log.d("anbt","contours size: " + contours.size());
//        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
//            Scalar color = new Scalar(new Random().nextInt(255),new Random().nextInt(255), new Random().nextInt(255) );
//            Imgproc.drawContours(drawing, contours, contourIdx, color, 5);
////            Imgproc.rectangle()
////            Imgproc.rectangle( drawing, boundRect[i].tl(), boundRect[i].br(), color, 2, 8, 0 );
//        }
//        return drawing;

    }
    private Mat DrawContours(Mat inputMat, Mat cannyDetected){
        Mat output = inputMat.clone();

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // find contours:
        Imgproc.findContours(cannyDetected, contours, hierarchy, Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++) {
            Imgproc.drawContours(output, contours, contourIdx, new Scalar(0, 0, 255), -1);
        }
        return output;
    }


    private Mat ObjectTrackingByColor(Mat inputMat)
    {
        Imgproc.cvtColor(inputMat,inputMat,Imgproc.COLOR_BGRA2BGR);
        Mat result = new Mat();
        Mat hsv_input = new Mat();
        //Convert BGR to HSV
        Imgproc.cvtColor(inputMat,hsv_input,Imgproc.COLOR_BGR2HSV);
        Mat hsv_blue = hsv_input.clone();
        Mat hsv_green = hsv_input.clone();
        Mat hsv_red = hsv_input.clone();

        //Define range of blue color in HSV
        Scalar lower_blue = new Scalar(110,50,50);
        Scalar upper_blue = new Scalar(130,255,255);

        //Define range of green color in HSV
        Scalar lower_green = new Scalar(50,100,100);
        Scalar upper_green = new Scalar(70,255,255);

        //Define range of red color in HSV
        Scalar lower_red = new Scalar(0,156,168);
        Scalar upper_red = new Scalar(165,201,219);

        //Threshold the HSV image to get only blue color
        Mat mask_blue = new Mat();
        Core.inRange(hsv_blue,lower_blue,upper_blue,mask_blue);
        //Threshold the HSV image to get only green color
        Mat mask_green = new Mat();
        Core.inRange(hsv_green,lower_green,upper_green,mask_green);

        Mat mask_red = new Mat();
        Core.inRange(hsv_red,lower_red,upper_red,mask_red);

        //Customization add blue and green mask
        Mat mask = new Mat();
//        Core.add(mask_blue,mask_green,mask);
        Core.add(mask_red,mask_green,mask);
        Core.add(mask,mask_blue,mask);
//        mask = mask_red;

        //Bitwise-AND mask and original image
        Core.bitwise_and(inputMat,inputMat,result,mask);

        //Get result
//        result = mask;
        return result;
    }
}
