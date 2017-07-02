package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import android.widget.ImageView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bui Trong An on 6/24/2017.
 * VipLab
 * trongan93@gmail.com
 * Function WaterShed and Distance Transform
 */

public class ImageSegmentation {
    private Mat inputMat;
    private Mat cannyEdgeMat;
    private Mat waterShedResultMat;
    private Mat outputMat;
    public ImageSegmentation(Mat inputMat) {
        this.inputMat = inputMat;
        this.outputMat = new Mat(inputMat.size(), inputMat.type());
        ChangeBackgroud();
    }

    public ImageSegmentation(Mat inputMat, Mat cannyEdgeMat, Mat waterShedResultMat) {
        this.inputMat = inputMat;
        this.cannyEdgeMat = cannyEdgeMat;
        this.waterShedResultMat = waterShedResultMat;
        this.outputMat = new Mat(inputMat.size(), inputMat.type());
        /**
         * Idead combine WaterShed and Contours
         * Step 1: Define Contours
         * Step 2: Recevice WaterShed from Pesenter
         * Step 3: Combine 2 results
         */
        CombineWaterShedAndContours();
    }

    public Mat GetResult()
    {
        return this.outputMat;
    }

    //! [black_bg]
    // Change the background from white to black, since that will help later to extract
    // better results during the use of Distance Transform
    private void ChangeBackgroud()
    {
        for( int x = 0; x < inputMat.rows(); x++ ) {
            for( int y = 0; y < inputMat.cols(); y++ ) {
//                if ( inputMat.at<Vec3b>(x, y) == Vec3b(255,255,255) ) {
//                    src.at<Vec3b>(x, y)[0] = 0;
//                    src.at<Vec3b>(x, y)[1] = 0;
//                    src.at<Vec3b>(x, y)[2] = 0;
//                }
                if(inputMat.get(x,y).equals(new double[]{255,255,255})){
                    inputMat.put(x,y,new double[]{0,0,0});
                }

            }
        }
    }

    private void CombineWaterShedAndContours(){
//        Mat inputConverted = inputMat.clone();
//        Imgproc.cvtColor(inputConverted, inputConverted, Imgproc.COLOR_BGR2GRAY);
//        Mat threshold = new Mat();
//        Imgproc.threshold(inputConverted,threshold,150,255,Imgproc.THRESH_BINARY);
        /**
         * Step 1: Define Contours
         * Reference: http://answers.opencv.org/question/43700/android-using-drawcontours-to-fill-region/
         */
        //OPTION 1
//        //dilating Canny edges
//        int dilation_size = 6;
//        Mat elementDilating = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
//        Imgproc.dilate(cannyEdgeMat,cannyEdgeMat,elementDilating);
//
//        //Encrosing Canny Edges
//        int erosion_size = 6;
//        Mat elementEroding = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2*erosion_size + 1, 2*erosion_size+1));
//        Imgproc.erode(cannyEdgeMat,cannyEdgeMat,elementEroding);

        //OPTION 2
        Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(55, 55));
        Imgproc.morphologyEx(cannyEdgeMat, cannyEdgeMat, Imgproc.MORPH_CLOSE, structuringElement );
        outputMat = cannyEdgeMat;

        //Using result of Canny Edge
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierachy = new Mat();
        Imgproc.findContours(cannyEdgeMat,contours,hierachy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        outputMat = inputMat.clone();
        for(int i = 0; i < contours.size(); i++){
            Imgproc.drawContours(outputMat,contours,i, new Scalar(255,255,255),-1);
        }
//        Imgproc.drawContours(outputMat,contours,-1, new Scalar(255,255,255),-1);//it is the same for all, when use -1 -> draw all contours




    }


}
