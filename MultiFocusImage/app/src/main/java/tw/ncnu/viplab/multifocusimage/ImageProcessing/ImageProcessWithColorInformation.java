package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bui Trong An on 11/15/2017.
 * VipLab
 * trongan93@gmail.com
 */

public class ImageProcessWithColorInformation {
    Mat inputImage = new Mat();
    public Mat red_mask = new Mat();
    public Mat green_mask = new Mat();
    public Mat blue_mask = new Mat();

    Mat outputImage = new Mat();
    public ImageProcessWithColorInformation(Mat inputImage){
        this.inputImage = inputImage;
        SplitImageChannelByColor(inputImage);
    }

    private void SplitImageChannelByColor(Mat inputMat)
    {
        Imgproc.cvtColor(inputMat,inputMat, Imgproc.COLOR_RGBA2RGB);
        List<Mat> channels = new ArrayList<>();
        Core.split(inputMat,channels);
        red_mask = channels.get(0);
        green_mask = channels.get(1);
        blue_mask = channels.get(2);
        //simple threshold
//        Imgproc.threshold(red_mat,red_mat,120,255,Imgproc.THRESH_BINARY);
//        Imgproc.threshold(green_mat,green_mat,120,255,Imgproc.THRESH_BINARY);
//        Imgproc.threshold(blue_mat,blue_mat,120,255,Imgproc.THRESH_BINARY);
        //adaptive threshold
//        Imgproc.adaptiveThreshold(red_mat,red_mat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,29,12);
        Imgproc.adaptiveThreshold(red_mask,red_mask,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,255,2);
        Imgproc.adaptiveThreshold(green_mask,green_mask,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,255,2); //old is 15/12
        Imgproc.adaptiveThreshold(blue_mask,blue_mask,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,255,2);//old is 15/12

        Mat gray_input = new Mat();
        Imgproc.cvtColor(inputMat,gray_input,Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(gray_input,gray_input,255,Imgproc.ADAPTIVE_THRESH_MEAN_C,Imgproc.THRESH_BINARY,255,2); //old is 141/8
    }
}
