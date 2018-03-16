package tw.ncnu.viplab.multifocusimage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageView selectedImageView;
    private Bitmap preProcessImage;
    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG,"OpenCV not loaded");
        }
//        System.loadLibrary("opencvNative");
    }
    ImageView processedImage1, processedImage2;
    Button btnProcessing;
    PresenterMainActivity presenterMainActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processedImage1 = (ImageView)this.findViewById(R.id.imvOriginal1);
        processedImage2 = (ImageView)this.findViewById(R.id.imvOriginal2);
        btnProcessing = (Button)this.findViewById(R.id.btnProcessing);

        processedImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageView = processedImage1;
                getImage();
            }
        });

        processedImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedImageView = processedImage2;
                getImage();
            }
        });

        btnProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preProcessImage = ((BitmapDrawable)processedImage1.getDrawable()).getBitmap();
                btnProcessing.setVisibility(View.INVISIBLE);
                processedImage2.setVisibility(View.INVISIBLE);

                presenterMainActivity = new PresenterMainActivity(processedImage1, processedImage2);
//        presenterMainActivity.Progress2016DecWeek4();
                presenterMainActivity.MainProcess();
                //Focus Touch and detect object
                //presenterMainActivity.Progress2017FebWeek4();
                presenterMainActivity.onTouchImageView();
            }
        });

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

    @Override
    public void onBackPressed() {
        //back to previous state
        processedImage1.setImageBitmap(preProcessImage);
        btnProcessing.setVisibility(View.VISIBLE);
        processedImage2.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    selectedImageView.setImageBitmap(imageBitmap);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    selectedImageView.setImageURI(selectedImage);
                }
                break;
        }
    }

    private void getImage() {
        final CharSequence[] options = {this.getString(R.string.get_from_camera),
                this.getString(R.string.get_from_gallery), this.getString(R.string.cancel)};
        AlertDialog.Builder chooseItem = new AlertDialog.Builder(this);
        chooseItem.setTitle(this.getString(R.string.get_image_from));
        chooseItem.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getResources().getString(R.string.get_from_camera))) {
                    dialog.dismiss();
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (options[item].equals(getResources().getString(R.string.get_from_gallery))) {
                    dialog.dismiss();
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto , 1);
                } else {
                    dialog.dismiss();
                }
            }
        });
        chooseItem.show();
    }
}
