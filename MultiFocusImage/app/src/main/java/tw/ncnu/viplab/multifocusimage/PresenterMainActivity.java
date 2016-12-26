package tw.ncnu.viplab.multifocusimage;

import android.widget.ImageView;

import org.opencv.core.Mat;

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
        //Convert Image to gray Image
        processedMat1 = imageBasicProcessing1.ConvertToGrayMat(inputMat1);
        processedMat2 = imageBasicProcessing2.ConvertToGrayMat(inputMat2);



        //Show Image
        ShowImage(processedImageView1, processedMat1);
        ShowImage(processedImageView2, processedMat2);
    }


}
