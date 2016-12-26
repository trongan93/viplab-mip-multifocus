package tw.ncnu.viplab.multifocusimage;

import android.widget.ImageView;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.lang.reflect.Array;

import tw.ncnu.viplab.multifocusimage.ImageProcessing.ControlImage;
import tw.ncnu.viplab.multifocusimage.ImageProcessing.ImageBasicProcessing;

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

//        // Eliminate noise and smaller objects
//        Photo.fastNlMeansDenoising(processedMat1,processedMat1); //denoising image
//        Mat fg = new Mat(processedMat1.size(), CvType.CV_8U);
//        Imgproc.erode(processedMat1, fg, new Mat(), new Point(-1, -1), 2);
//
//        // Identify image pixels without objects
//        Mat bg = new Mat(processedMat1.size(), CvType.CV_8U);
//        Imgproc.dilate(processedMat1, bg, new Mat(), new Point(-1, -1), 3);
//        Imgproc.threshold(bg,bg,1,128,Imgproc.THRESH_BINARY_INV);
//
//        // Create markers image
//        Mat markers = new Mat(processedMat1.size(), CvType.CV_8U, new Scalar(0));
//        Core.add(fg, bg, markers);
//        Imgproc.watershed(processedMat1,markers);

        //# noise removal
        Mat kernel = new Mat(3,3,CvType.CV_8U);
        //Imgproc.morphologyEx(inputMat1, inputMat1, Imgproc.MORPH_OPEN, kernel);
        Mat opening = new Mat();
        Imgproc.morphologyEx(processedMat1,opening,Imgproc.MORPH_OPEN,kernel,new Point(),2);

        //# sure background area
        Mat sure_bg = new Mat(processedMat1.size(),CvType.CV_8U);
        Imgproc.dilate(opening,sure_bg,kernel,new Point(),3);

        //# Finding sure foreground area
        Mat distTransform = new Mat();
        Imgproc.distanceTransform(opening,distTransform,Imgproc.DIST_L2,5);
        Imgproc.threshold(distTransform, processedMat1, 0.7*Core.minMaxLoc(distTransform).maxVal,255,0);

        //# Finding unknown region
        Mat sure_fg = new Mat(processedMat1.size(),CvType.CV_8U);
        Core.subtract(sure_bg, sure_fg, processedMat1);

        //Show Image
        ShowImage(processedImageView1, processedMat1);
        ShowImage(processedImageView2, processedMat2);
    }


}
