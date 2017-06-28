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
//        System.loadLibrary("opencvNative");
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
        presenterMainActivity.MainProcess();
        //Focus Touch and detect object
        //presenterMainActivity.Progress2017FebWeek4();
        presenterMainActivity.onTouchImageView();
//        final List<Integer> RGBValueNearFocusTouched = new ArrayList<>();
//        final Bitmap bitmapNearFocus = ((BitmapDrawable)processedImage1.getDrawable()).getBitmap();
//        processedImage1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int pixel = bitmapNearFocus.getPixel((int)motionEvent.getX(),(int)motionEvent.getY());
//                Log.d("anbt","pixel: " + pixel);
//                RGBValueNearFocusTouched.add(Color.red(pixel));
//                Log.d("anbt","red value (real): " + Color.red(pixel));
//                Log.d("anbt","red value (virtual):" + RGBValueNearFocusTouched.get(0));
//                return false;
//            }
//        });


    }
}
