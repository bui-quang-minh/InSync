package com.in_sync.activities;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.in_sync.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageDetailActivity extends AppCompatActivity {

    // creating a string variable, image view variable
    // and a variable for our scale gesture detector class.
    String imgPath;
    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private View deleteIcon;

    // on below line we are defining our scale factor.
    private float mScaleFactor = 1.0f;
    private float mPosX = 0.0f;
    private float mPosY = 0.0f;
    private float lastTouchX;
    private float lastTouchY;
    private float dX;
    private float dY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        // on below line getting data which we have passed from our adapter class.
        imgPath = getIntent().getStringExtra("imgPath");
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                Log.e("TAG", "handleOnBackPressed: ENDED 1");
            }
        });
        // initializing our image view.
        imageView = findViewById(R.id.idIVImage);
        deleteIcon = findViewById(R.id.delete_image_button);

        // on below line we are initializing our scale gesture detector for zoom in and out for our image.
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        catchEvent();
        // on below line we are getting our image file from its path.
        File imgFile = new File(imgPath);

        // if the file exists then we are loading that image in our image view.
        if (imgFile.exists()) {
            Picasso.with(this).load(imgFile).placeholder(R.drawable.ic_launcher_background).into(imageView);
        }
    }
    @Override
    public void onBackPressed() {
        // Logic to stop the activity
        super.onBackPressed();
        finish();
        Log.e("TAG", "handleOnBackPressed: ENDED 2");
    }

    private void catchEvent() {
        deleteIcon.setOnClickListener((view) -> {
            File imgFile = new File(imgPath);
            if (imgFile.exists()) {
                imgFile.delete();
                finish();
            }
        });
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        // inside on touch event method we are calling on
        // touch event method and passing our motion event to it.
        scaleGestureDetector.onTouchEvent(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchX = motionEvent.getX();
                lastTouchY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = motionEvent.getX() - lastTouchX;
                float dy = motionEvent.getY() - lastTouchY;

                if (mScaleFactor > 1.0f) { // Allow moving only if zoomed in
                    mPosX += dx;
                    mPosY += dy;

                    // Prevent the image from moving out of view bounds (optional)
                    float maxPosX = (imageView.getWidth() * mScaleFactor - imageView.getWidth()) / 2;
                    float maxPosY = (imageView.getHeight() * mScaleFactor - imageView.getHeight()) / 2;

                    mPosX = Math.max(-maxPosX, Math.min(mPosX, maxPosX));
                    mPosY = Math.max(-maxPosY, Math.min(mPosY, maxPosY));

                    imageView.setTranslationX(mPosX);
                    imageView.setTranslationY(mPosY);
                }
                lastTouchX = motionEvent.getX();
                lastTouchY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        // on below line we are creating a class for our scale
        // listener and extending it with gesture listener.
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            // inside on scale method we are setting scale
            // for our image in our image view.
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 10.0f));

            // on below line we are setting
            // scale x and scale y to our image view.
            imageView.setScaleX(mScaleFactor);
            imageView.setScaleY(mScaleFactor);
            return true;
        }
    }
}
