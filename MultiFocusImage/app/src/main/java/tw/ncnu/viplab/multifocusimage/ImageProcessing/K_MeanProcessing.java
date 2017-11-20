package tw.ncnu.viplab.multifocusimage.ImageProcessing;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Bui Trong An on 11/20/2017.
 * VipLab
 * trongan93@gmail.com
 */

public class K_MeanProcessing {
    private Mat inputMat1 = new Mat();
    private Mat outputMat1 = new Mat();
    public K_MeanProcessing(Mat inputMat1)
    {
        Imgproc.cvtColor(inputMat1,inputMat1,Imgproc.COLOR_BGRA2BGR);
        this.inputMat1 = inputMat1;
//        processing();
        outputMat1 = cluster(inputMat1,4);
    }
    private void processing(){
        inputMat1.convertTo(inputMat1, CvType.CV_32F);
        TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER,10,0.1);
        Core.kmeans(inputMat1,8, outputMat1, criteria, 10,Core.KMEANS_RANDOM_CENTERS);
    }

    private static Mat cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);

        Mat dst = cutout.clone();
        int rows = 0;
        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                int r = (int)centers.get(label, 2)[0];
                int g = (int)centers.get(label, 1)[0];
                int b = (int)centers.get(label, 0)[0];
                dst.put(y, x, b,g,r);
                rows++;
            }
        }
        return dst;
    }

    public Mat GetResult(){
        outputMat1.convertTo(outputMat1,CvType.CV_8U);
        return outputMat1;
    }

}
