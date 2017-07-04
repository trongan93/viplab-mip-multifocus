package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import org.opencv.core.Mat;

/**
 * Created by Bui Trong An on 7/4/2017.
 * VipLab
 * trongan93@gmail.com
 */

public class ImageCombine2Regions {
    Mat matNearRegionWithContourResult;
    Mat matFarRegionWithContourResult;

    public ImageCombine2Regions(Mat matNearRegionWithContourResult, Mat matFarRegionWithContourResult) {
        this.matNearRegionWithContourResult = matNearRegionWithContourResult;
        this.matFarRegionWithContourResult = matFarRegionWithContourResult;
    }

    public void Combine2Region(){
        
    }


}
