package tw.ncnu.viplab.multifocusimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV not loaded");
        }
    }
    ImageView processedImage1, processedImage2;
    PresenterMainActivity presenterMainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processedImage1 = (ImageView)this.findViewById(R.id.imvOriginal1);
        processedImage2 = (ImageView)this.findViewById(R.id.imvOriginal2);

        presenterMainActivity = new PresenterMainActivity(processedImage1, processedImage2);
//        presenterMainActivity.Progress2016DecWeek4();
        presenterMainActivity.Progress2017JanWeek3();
    }
}
