package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import org.opencv.core.Mat;

/**
 * Created by Bui Trong An on 3/4/2018.
 * VipLab
 * trongan93@gmail.com
 */

public class CombineImageRealMethod {
    double[] white_value = {255.0,255.0,255.0};
    double[] black_value = {0.0, 0.0, 0.0};
    Mat nearFocusedSegmantation, farFocusedSegmantation, nearOriginal, farOriginal;
    int numOfRow, numOfCol;
    public CombineImageRealMethod(Mat matNearRegionWithContour, Mat matFarRegionWithContour, Mat matNearInput, Mat matFarInput) {
        this.nearFocusedSegmantation = matNearRegionWithContour;
        this.farFocusedSegmantation = matFarRegionWithContour;
        this.nearOriginal = matNearInput;
        this.farOriginal = matFarInput;
        this.numOfRow = matNearInput.rows();
        this.numOfCol = matNearInput.cols();
    }
    public Mat SFMat(){
        Mat SFMat = new Mat(nearFocusedSegmantation.size(),nearFocusedSegmantation.type());

        for(int r = 0; r < numOfRow; r++){
            for(int c = 0; c < numOfCol; c++){
                double[] nearPixelValueOnSegmantation = nearFocusedSegmantation.get(r,c);
                double[] farPixelvalueOnSegmatation = farFocusedSegmantation.get(r,c);
                double[] nearPixelValueOnOriginal = nearOriginal.get(r,c);
                double[] farPixelValueOnOriginal = farOriginal.get(r,c);
                if(nearPixelValueOnSegmantation[0] == 0 && nearPixelValueOnSegmantation[1] == 0 && nearPixelValueOnSegmantation[2] == 0){
                    double value0 = 1*nearPixelValueOnOriginal[0];
                    double value1 = 1*nearPixelValueOnOriginal[1];
                    double value2 = 1*nearPixelValueOnOriginal[2];
                    SFMat.put(r,c,value0, value1, value2);
                }
                else if(farPixelvalueOnSegmatation[0] == 0 && farPixelvalueOnSegmatation[1] == 0 && farPixelvalueOnSegmatation[2] == 0){
                    double value0 = 1*farPixelValueOnOriginal[0];
                    double value1 = 1*farPixelValueOnOriginal[1];
                    double value2 = 1*farPixelValueOnOriginal[2];
                    SFMat.put(r,c,value0,value1,value2);
                }
                else{
                    double value0 = 0.5*(nearPixelValueOnOriginal[0] + farPixelValueOnOriginal[0]);
                    double value1 = 0.5*(nearPixelValueOnOriginal[1] + farPixelValueOnOriginal[1]);
                    double value2 = 0.5*(nearPixelValueOnOriginal[2] + farPixelValueOnOriginal[2]);
                    SFMat.put(r,c,value0,value1,value2);
                }
            }
        }

        return SFMat;
    }

}
