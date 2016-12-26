package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Bui Trong An on 12/26/2016.
 * VipLab
 * trongan93@gmail.com
 */

public class ImageBasicProcessing {
    private Mat _src;
    public ImageBasicProcessing(Mat src){
        _src = src;
    }
    public Mat ConvertToGrayMat(Mat src){
        Mat processedImage = new Mat();
        Imgproc.cvtColor(src,processedImage,Imgproc.COLOR_BGR2GRAY);
        return processedImage;
    }

}
