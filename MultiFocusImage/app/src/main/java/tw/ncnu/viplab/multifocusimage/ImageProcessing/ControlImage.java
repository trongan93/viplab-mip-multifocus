package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by Bui Trong An on 12/26/2016.
 * VipLab
 * trongan93@gmail.com
 */

public final class ControlImage {
    public static Mat ConvertImageViewToMat(ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap imageBitmap = drawable.getBitmap();
        //Convert Bitmap to Mat
        Mat matImage = new Mat();
        Utils.bitmapToMat(imageBitmap, matImage);
        return matImage;
    }
    public static void ConvertMatToBitmap(Mat srcMat, ImageView imageView){
        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
        Bitmap resultBitmap = drawable.getBitmap();

        Utils.matToBitmap(srcMat, resultBitmap);
        imageView.setImageBitmap(resultBitmap);
    }


}
