package tw.ncnu.viplab.multifocusimage.ImageProcessing;

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
    private Mat edgeMat;
    private Mat waterShedResultMat;
    private Mat outputMat;

    public ImageSegmentation(Mat inputMat, Mat edgeMat, Mat waterShedResultMat) {
        this.inputMat = inputMat;
        this.edgeMat = edgeMat;
        this.waterShedResultMat = waterShedResultMat;
        this.outputMat = new Mat(inputMat.size(), inputMat.type());
        /**
         * Idead combine WaterShed and Contours
         * Step 1: Connect edge detected
         * Step 2: Define Contours
         * Step 3: Process for 2 image
         *      Step 3.1: Near focus image: Define region and draw near with black color and far with white color
         *      Step 3.2: Far focus image: Define region and draw near with black color and far with white color
         * Step 4: Combine 2 region from 3.1 and 3.2
         */
        ContourProcessWithEdgesResult();
    }

    public Mat GetResult()
    {
        return this.outputMat;
    }


    private void ContourProcessWithEdgesResult(){
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
//        Imgproc.dilate(edgeMat,edgeMat,elementDilating);
//
//        //Encrosing Canny Edges
//        int erosion_size = 6;
//        Mat elementEroding = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2*erosion_size + 1, 2*erosion_size+1));
//        Imgproc.erode(edgeMat,edgeMat,elementEroding);

        //OPTION 2
//        Mat structuringElement = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(50, 50));//source_1 use 57,57 with 2 edge deteced and MORPH_ELLIPSE --> 35
//        Imgproc.morphologyEx(edgeMat, edgeMat, Imgproc.MORPH_TOPHAT , structuringElement );//old is MORPH_CLOSE
//        outputMat = edgeMat;

        //OPTION 3 is Adaptive Thresholding


        //Using result of Edge
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierachy = new Mat();
        Imgproc.findContours(edgeMat,contours,hierachy,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        outputMat = inputMat.clone();
//        for(int i = 0; i < contours.size(); i++){
//            Imgproc.drawContours(outputMat,contours,i, new Scalar(255,255,255),-1);
//        }
        Imgproc.drawContours(outputMat,contours,-1, new Scalar(255,255,255),-1);//it is the same for all, when use -1 -> draw all contours




    }


}
