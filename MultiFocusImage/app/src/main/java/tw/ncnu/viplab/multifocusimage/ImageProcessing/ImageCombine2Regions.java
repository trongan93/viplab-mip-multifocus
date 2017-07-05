package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import android.graphics.Color;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Bui Trong An on 7/4/2017.
 * VipLab
 * trongan93@gmail.com
 */

public class ImageCombine2Regions {
    Mat matNearRegionWithContour;
    Mat matFarRegionWithContour;

    public Mat matNearRegionProcessing;
    public Mat matFarRegionProcessing;
    double[] white_value = {255.0,255.0,255.0};
    double[] black_value = {0.0, 0.0, 0.0};

    public ImageCombine2Regions(Mat matNearRegionWithContour, Mat matFarRegionWithContour) {
        this.matNearRegionWithContour = matNearRegionWithContour;
        this.matFarRegionWithContour = matFarRegionWithContour;
        matNearRegionProcessing = new Mat(matNearRegionWithContour.size(), matNearRegionWithContour.type());
        matFarRegionProcessing = new Mat(matFarRegionWithContour.size(), matFarRegionWithContour.type());
    }

    /*
        Pre process near region
     */
    public void PreProcessNearRegion() {
        if (null != matNearRegionWithContour) {
            for (int r = 0; r < matNearRegionWithContour.rows(); r++) {
                for (int c = 0; c < matNearRegionWithContour.cols(); c++) {
                    double[] pixelValues = matNearRegionWithContour.get(r,c);
                    if(pixelValues[0]== 255 && pixelValues[1] == 255 && pixelValues[2] == 255){
                        matNearRegionProcessing.put(r,c, black_value);
                    }
                    else{
                        matNearRegionProcessing.put(r,c,white_value);
                    }
                }
            }
        }
    }
    /*
        Pre process far region
     */
    public void PreProcessFarRegion(){
        if(null != matFarRegionWithContour){
            for(int r = 0; r < matFarRegionWithContour.rows(); r++){
                for(int c = 0; c < matFarRegionWithContour.cols(); c++){
                    double[] pixelValues = matFarRegionWithContour.get(r,c);
                    if(pixelValues[0] != 255 && pixelValues[1] != 255 && pixelValues[2] != 255) {
                        matFarRegionProcessing.put(r,c,black_value);
                    }
                    else{
                        matFarRegionProcessing.put(r,c,white_value);
                    }
                }
            }
        }
    }
}
