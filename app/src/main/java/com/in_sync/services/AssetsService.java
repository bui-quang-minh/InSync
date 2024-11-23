package com.in_sync.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Choreographer;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.util.Pair;

import com.in_sync.R;
import com.in_sync.actions.Action;
import com.in_sync.adapters.ImageGalleryAdapter;
import com.in_sync.file.FileSystem;
import com.in_sync.helpers.NotificationUtils;
import com.in_sync.models.Coordinate;
import com.in_sync.models.Sequence;
import com.in_sync.models.Step;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class AssetsService extends AccessibilityService {
    private static final String TAG = "AssetsService";
    private static final String RESULT_CODE = "RESULT_CODE";
    private static final String DATA = "DATA";
    private static final String ACTION = "ACTION";
    private static final String START = "START";
    private static final String STOP = "STOP";
    private static final String SCREENCAP_NAME = "screencap";

    private WindowManager windowManager;
    private View overlayView;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private AssetsService.OrientationChangeCallback mOrientationChangeCallback;
    private String mStoreDir;
    private ImageGalleryAdapter imageGalleryAdapter;
    private String json;
    private Step[] steps;
    private ImageView imageView;
    private String appOpened = "";
    private Action action;
    private Sequence sequence;
    private static int IMAGES_PRODUCED;
    private int ACCURACY_POINT;
    private Coordinate prev_point = new Coordinate(0, 0);
    private AccessibilityNodeInfo source;
    private com.in_sync.models.Action currentAction = null;
    private boolean isExpanded = true;
    private List<String> images;
    private Queue<com.in_sync.models.Action> actionQueue = new LinkedList<>();
    private static Context contexts;

    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, AssetsService.class);
        intent.putExtra(ACTION, START);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        contexts = context;
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, AssetsService.class);
        intent.putExtra(ACTION, STOP);
        return intent;
    }

    private static boolean isStartCommand(Intent intent) {
        return intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                && intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), START);
    }

    private static boolean isStopCommand(Intent intent) {
        return intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), STOP);
    }

    private static int getVirtualDisplayFlags() {
        return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
        }
    }

    private class OrientationChangeCallback extends OrientationEventListener {
        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // Clean up existing virtual display and image reader
                    if (mVirtualDisplay != null) {
                        mVirtualDisplay.release();
                        mVirtualDisplay = null;
                    }
                    if (mImageReader != null) {
                        mImageReader.setOnImageAvailableListener(null, null);
                        mImageReader = null;
                    }
                    // Delay recreation slightly to avoid rapid re-triggering
                    mHandler.postDelayed(() -> {
                        try {
                            createVirtualDisplay();
                        } catch (Exception e) {
                            // Use the Activity context for the AlertDialog
                            removeOverlay();

                            new Handler(Looper.getMainLooper()).post(() -> {
                                AlertDialog alertDialog = new AlertDialog.Builder(contexts)
                                        .setTitle("Warning")
                                        .setMessage("Detect orientation change! Please restart the service.")
                                        .setNeutralButton("OK", (dialog, which) -> {
                                            dialog.dismiss();
                                        })
                                        .create();
                                alertDialog.show();
                            });
                        }
                    }, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e(TAG, "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    mMediaProjection.unregisterCallback(AssetsService.MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create store dir
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.getAbsolutePath() + "/screenshots/";
            File storeDirectory = new File(mStoreDir);

            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.");
                    stopSelf();
                }
            } else {
                Log.e(TAG, "file storage directory already exists.");
            }
        } else {
            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
            stopSelf();
        }

        images = FileSystem.getFileName(this);
        imageGalleryAdapter = new ImageGalleryAdapter(this, images);

        Log.e(TAG, "onCreate: sevices started");
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.loadLibrary("opencv_java4");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        info.notificationTimeout = 100;
        this.setServiceInfo(info);
        showOverlay();
        currentAction = null;
        Log.e(TAG, "onStartCommand Services started");
        action = new Action(getApplicationContext(), AssetsService.this);
        if (isStartCommand(intent)) {
            Pair<Integer, Notification> notification = NotificationUtils.getNotification(this);
            startForeground(notification.first, notification.second);

            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra(DATA);
            startProjection(resultCode, data);
        } else if (isStopCommand(intent)) {
            stopSelf();
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startProjection(int resultCode, Intent data) {
        MediaProjectionManager mpManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mMediaProjection = null;
        if (mMediaProjection == null) {
            mMediaProjection = mpManager.getMediaProjection(resultCode, data);
            if (mMediaProjection != null) {
                // display metrics
                mDensity = Resources.getSystem().getDisplayMetrics().densityDpi;
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                mDisplay = windowManager.getDefaultDisplay();
                mMediaProjection.registerCallback(new AssetsService.MediaProjectionStopCallback(), mHandler);
                createVirtualDisplay();
                mOrientationChangeCallback = new AssetsService.OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }
                mMediaProjection.registerCallback(new AssetsService.MediaProjectionStopCallback(), mHandler);
            }
        }
    }

    private void stopProjection() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mMediaProjection != null) {
                        mMediaProjection.stop();
                    }
                    if (mVirtualDisplay != null) {
                        mVirtualDisplay.release();
                        mVirtualDisplay = null; // Clear the reference
                    }
                    if (mImageReader != null) {
                        mImageReader.setOnImageAvailableListener(null, null);
                        mImageReader.close();
                        mImageReader = null; // Clear the reference
                    }
                    if (mOrientationChangeCallback != null) {
                        mOrientationChangeCallback.disable();
                        mOrientationChangeCallback = null; // Clear the reference
                    }
                }
            });
        }
    }

    @SuppressLint("WrongConstant")
    private void createVirtualDisplay() {
        // get width and height
        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight,
                mDensity, getVirtualDisplayFlags(), mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new AssetsService.ImageAvailableListener(), mHandler);
        //log relevent information
        Log.e(TAG, "createVirtualDisplay: " + mWidth + " " + mHeight + " " + mDensity);
    }


    private void showOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.screenshot_button, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 200;

        windowManager.addView(overlayView, params);
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - touchX);
                        params.y = initialY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(overlayView, params);
                        return true;

                    default:
                        return false;
                }
            }
        });
        ImageView blinkingLight = overlayView.findViewById(R.id.blinking_light);
        AnimationDrawable animationDrawable = (AnimationDrawable) blinkingLight.getDrawable();
        animationDrawable.start();
        ImageButton arrowButton = overlayView.findViewById(R.id.arrow_button);
        arrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpandCollapse();
                windowManager.updateViewLayout(overlayView, params);
            }
        });
        ImageButton closeButton = overlayView.findViewById(R.id.stop_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay();
            }
        });
        imageView = overlayView.findViewById(R.id.screenshot_image_view);
        ImageButton cameraButton = overlayView.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                windowManager.removeView(overlayView);

//                Handler handler = new Handler(Looper.getMainLooper());
//                // Delay after click capture button by 1 seconds
//                handler.postDelayed(() -> {
//                    captureScreenshot();
//                    windowManager.addView(overlayView, params);
//
//                }, 100);

                long startTime = System.nanoTime(); // Record start time for logging

                Choreographer.getInstance().postFrameCallback(frameTimeNanos -> {
                    // Calculate the frame callback delay
                    long initialDelay = (System.nanoTime() - startTime) / 1_000_000; // Convert to milliseconds

                    // Ensure a total delay of 100ms
                    long remainingDelay = Math.max(100 - initialDelay, 0); // Adjust to account for the frame time already elapsed

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        Log.d("FinalDelay", "Total delay: " + (initialDelay + remainingDelay) + "ms");
                        MediaPlayer mediaPlayer = MediaPlayer.create(contexts, R.raw.camera);
                        mediaPlayer.setOnCompletionListener(mp -> mp.release()); // Release the MediaPlayer once the sound is done
                        mediaPlayer.start();
                        captureScreenshot(); // Capture screenshot
                        windowManager.addView(overlayView, params); // Re-add overlay
                    }, remainingDelay);
                });

            }
        });
    }

    private void captureScreenshot() {
        FileOutputStream fos = null;
        Bitmap bitmap = null;
        String fileName = "";
        if (mImageReader == null) {
            Log.e("Screenshot", "ImageReader is not initialized");
            return;
        }
        try (Image image = mImageReader.acquireLatestImage()) {
            if (image != null) {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * mWidth;
                //bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
//                bitmap = Bitmap.createBitmap(mWidth , mHeight, Bitmap.Config.ARGB_8888);

                //bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);

//                bitmap.copyPixelsFromBuffer(buffer);


                bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                int[] pixels = new int[mWidth * mHeight];
                buffer.position(0);

                // Manually copy pixels, row by row
                for (int row = 0; row < mHeight; row++) {
                    for (int col = 0; col < mWidth; col++) {
                        int pixelIndex = row * mWidth + col;
                        int bufferIndex = row * rowStride + col * pixelStride;
                        buffer.position(bufferIndex);
                        int r = (buffer.get() & 0xFF);
                        int g = (buffer.get() & 0xFF);
                        int b = (buffer.get() & 0xFF);
                        int a = (buffer.get() & 0xFF);
                        pixels[pixelIndex] = (a << 24) | (r << 16) | (g << 8) | b;
                    }
                }

                // Populate the Bitmap
                bitmap.setPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);


                // Set local date time
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    fileName = mStoreDir + "/myscreen_" + localDateTime.format(formatter) + ".png";
                    images.add(fileName);
                    //imageGalleryAdapter.setImages(images);
                }

                // write bitmap to a file
                fos = new FileOutputStream(fileName);
                //imageGalleryAdapter.addItem(fileName);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

            if (bitmap != null) {
                bitmap.recycle();
            }

        }
    }

    private void toggleExpandCollapse() {
        LinearLayout content = overlayView.findViewById(R.id.overlay_content);
        ImageButton arrowButton = overlayView.findViewById(R.id.arrow_button);

        if (isExpanded) {
            // Collapse
            content.setVisibility(View.GONE);
            arrowButton.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        } else {
            // Expand
            content.setVisibility(View.VISIBLE);
            arrowButton.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        }

        isExpanded = !isExpanded;
    }

    private void removeOverlay() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            stopProjection(); // Stop the MediaProjection and release resources
            // Call stopSelf to stop the service
            stopSelf();

        }
    }
}
