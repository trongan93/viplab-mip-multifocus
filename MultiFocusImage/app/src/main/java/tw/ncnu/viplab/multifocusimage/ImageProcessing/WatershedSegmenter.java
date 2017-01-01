package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Bui Trong An on 1/1/2017.
 * VipLab
 * trongan93@gmail.com
 */

public final class WatershedSegmenter {
    public static Mat markers= new Mat();

    public static void setMarkers(Mat markerImage)
    {
        markerImage.convertTo(markers, CvType.CV_32SC1);
    }

    public static Mat process(Mat image)
    {
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGRA2BGR);
        Imgproc.watershed(image,markers);
        markers.convertTo(markers,CvType.CV_8U);
        return markers;
    }
}
