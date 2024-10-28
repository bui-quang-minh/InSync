//package com.in_sync.services;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.Notification;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.PixelFormat;
//import android.hardware.display.DisplayManager;
//import android.hardware.display.VirtualDisplay;
//import android.media.Image;
//import android.media.ImageReader;
//import android.media.projection.MediaProjection;
//import android.media.projection.MediaProjectionManager;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Looper;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Display;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.OrientationEventListener;
//import android.view.Surface;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.view.accessibility.AccessibilityEvent;
//import android.widget.ImageButton;
//
//import androidx.annotation.RequiresApi;
//import androidx.core.util.Pair;
//
//import com.in_sync.R;
//import com.in_sync.activities.ScreenshotPermissionActivity;
//import com.in_sync.adapters.ImageGalleryAdapter;
//import com.in_sync.file.FileSystem;
//import com.in_sync.helpers.NotificationUtils;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.sql.Time;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Objects;
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class ScreenshotService extends AccessibilityService {
//
//    private static final String TAG = "ScreenshotService";
//    private static final String RESULT_CODE = "RESULT_CODE";
//    private static final String DATA = "DATA";
//    private static final String ACTION = "ACTION";
//    private static final String START = "START";
//    private static final String STOP = "STOP";
//    private static final String SCREENCAP_NAME = "screencap";
//    private MediaProjection mMediaProjection;
//    private String mStoreDir;
//    private ImageReader mImageReader;
//    private Handler mHandler;
//    private Display mDisplay;
//    private VirtualDisplay mVirtualDisplay;
//    private int mDensity;
//    private int mWidth;
//    private int mHeight;
//    private int mRotation;
//    private WindowManager windowManager;
//    private WindowManager.LayoutParams floatWindowLayoutParam;
//    private View viewGroup;
//    private OrientationChangeCallback mOrientationChangeCallback;
//    private ImageGalleryAdapter imageGalleryAdapter;
//    private List<String> images;
//    private static Context contexts;
//    private Intent startIntent;
//    private Context startContext;
//    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
//        Intent intent = new Intent(context, ScreenshotService.class);
//        intent.putExtra(ACTION, START);
//        intent.putExtra(RESULT_CODE, resultCode);
//        intent.putExtra(DATA, data);
//        contexts = context;
//        return intent;
//    }
//
//    public static Intent getStopIntent(Context context) {
//        Intent intent = new Intent(context, ScreenshotService.class);
//        intent.putExtra(ACTION, STOP);
//        return intent;
//    }
//
//    private static boolean isStartCommand(Intent intent) {
//        return intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
//                && intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), START);
//    }
//
//    private static boolean isStopCommand(Intent intent) {
//        return intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), STOP);
//    }
//
//    private static int getVirtualDisplayFlags() {
//        return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
//    }
//
//    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
//        @Override
//        public void onImageAvailable(ImageReader reader) {
//        }
//    }
//
//    private class OrientationChangeCallback extends OrientationEventListener {
//
//        OrientationChangeCallback(Context context) {
//            super(context);
//        }
//        @Override
//        public void onOrientationChanged(int orientation) {
//            final int rotation = mDisplay.getRotation();
//            if (rotation != mRotation) {
//                mRotation = rotation;
//                try {
//                    // Clean up existing virtual display and image reader
//                    if (mVirtualDisplay != null) {
//                        mVirtualDisplay.release();
//                        mVirtualDisplay = null;
//                    }
//                    if (mImageReader != null) {
//                        mImageReader.setOnImageAvailableListener(null, null);
//                        mImageReader = null;
//                    }
//                    // Delay recreation slightly to avoid rapid re-triggering
//                    mHandler.postDelayed(() -> {
//                        try {
//                            createVirtualDisplay();
//                        } catch (Exception e) {
//                            // Use the Activity context for the AlertDialog
//                            removeOverlay();
//                            Log.e(TAG, "onOrientationChanged: " + e);
//                            new Handler(Looper.getMainLooper()).post(() -> {
//                                AlertDialog alertDialog = new AlertDialog.Builder(contexts)
//                                        .setTitle("Warning")
//                                        .setMessage("Detect orientation change! Please restart the service." + e)
//                                        .setNeutralButton("OK", (dialog, which) -> {
//                                            dialog.dismiss();
//                                        })
//                                        .create();
//                                alertDialog.show();
//                            });
//                        }
//                    }, 0);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//    private class MediaProjectionStopCallback extends MediaProjection.Callback {
//        @Override
//        public void onStop() {
//            Log.e(TAG, "stopping projection.");
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mVirtualDisplay != null) mVirtualDisplay.release();
//                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
//                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
//                    mMediaProjection.unregisterCallback(ScreenshotService.MediaProjectionStopCallback.this);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//
//    }
//
//    @Override
//    public void onInterrupt() {
//
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // create store dir
//        File externalFilesDir = getExternalFilesDir(null);
//        if (externalFilesDir != null) {
//            mStoreDir = externalFilesDir.getAbsolutePath() + "/screenshots/";
//            File storeDirectory = new File(mStoreDir);
//
//            if (!storeDirectory.exists()) {
//                boolean success = storeDirectory.mkdirs();
//                if (!success) {
//                    Log.e(TAG, "failed to create file storage directory.");
//                    stopSelf();
//                }
//            }
//        } else {
//            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
//            stopSelf();
//        }
//
//        images = FileSystem.getFileName(this);
//        imageGalleryAdapter = new ImageGalleryAdapter(this, images);
//        Log.e(TAG, "onCreate: sevices started");
//        // start capture handling thread
//        new Thread() {
//            @Override
//            public void run() {
//                Looper.prepare();
//                mHandler = new Handler();
//                Looper.loop();
//            }
//        }.start();
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        showOverlay();
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
//                AccessibilityEvent.TYPE_VIEW_FOCUSED;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
//        info.notificationTimeout = 100;
//        this.setServiceInfo(info);
//        if (isStartCommand(intent)) {
//            // create notification
//            Pair<Integer, Notification> notification = NotificationUtils.getNotification(this);
//            startForeground(notification.first, notification.second);
//            // start projection
//            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
//            Intent data = intent.getParcelableExtra(DATA);
//            startIntent = intent;
//            startProjection(resultCode, data);
//        } else if (isStopCommand(intent)) {
//            stopSelf();
//        } else {
//            stopSelf();
//        }
//
//        return START_NOT_STICKY;
//    }
//
//    private void startProjection(int resultCode, Intent data) {
//        startContext = this;
//        MediaProjectionManager mpManager =
//                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        if (mMediaProjection != null) {
//            mMediaProjection.stop();
//            mMediaProjection = null;
//        }
//        mMediaProjection = mpManager.getMediaProjection(resultCode, data);
//        if (mMediaProjection != null) {
//            // display metrics
//            mDensity = Resources.getSystem().getDisplayMetrics().densityDpi;
//            WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//            mDisplay = windowManager.getDefaultDisplay();
//
//            // Register callbacks
//            mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
//
//            mOrientationChangeCallback = new OrientationChangeCallback(this);
//            if (mOrientationChangeCallback.canDetectOrientation()) {
//                mOrientationChangeCallback.enable();
//            }
//            createVirtualDisplay();
//        }
//    }
//
//
//    private void newProjection(int resultCode, Intent data) {
//        startContext = this;
//        MediaProjectionManager mpManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
//        mMediaProjection = null;
//        if (mMediaProjection == null) {
//            mMediaProjection = mpManager.getMediaProjection(resultCode, data);
//            if (mMediaProjection != null) {
//                // display metrics
//                mDensity = Resources.getSystem().getDisplayMetrics().densityDpi;
//                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                mDisplay = windowManager.getDefaultDisplay();
//                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
//
//                mOrientationChangeCallback = new OrientationChangeCallback(this);
//                if (mOrientationChangeCallback.canDetectOrientation()) {
//                    mOrientationChangeCallback.enable();
//                }
//                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
//                createVirtualDisplay();
//            }
//        }
//    }
//    private void stopProjection() {
//        if (mHandler != null) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (mMediaProjection != null) {
//                        mMediaProjection.stop();
//                    }
//                    if (mVirtualDisplay != null) {
//                        mVirtualDisplay.release();
//                        mVirtualDisplay = null; // Clear the reference
//                    }
//                    if (mImageReader != null) {
//                        mImageReader.setOnImageAvailableListener(null, null);
//                        mImageReader.close();
//                        mImageReader = null; // Clear the reference
//                    }
//                    if (mOrientationChangeCallback != null) {
//                        mOrientationChangeCallback.disable();
//                        mOrientationChangeCallback = null; // Clear the reference
//                    }
//                }
//            });
//        }
//    }
//
//    @SuppressLint("WrongConstant")
//    private void createVirtualDisplay() {
//        // get width and height
//        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
//        mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//
//        // start capture reader
//        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
//        mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight,
//                mDensity, getVirtualDisplayFlags(), mImageReader.getSurface(), null, mHandler);
//        mImageReader.setOnImageAvailableListener(new ScreenshotService.ImageAvailableListener(), mHandler);
//        //log relevent information
//        Log.e(TAG, "createVirtualDisplay: " + mWidth + " " + mHeight + " " + mDensity);
//    }
//    private void showOverlay() {
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        viewGroup = layoutInflater.inflate(R.layout.screenshot_button, null);
//
//        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.WRAP_CONTENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT);
//
//        params.gravity = Gravity.TOP | Gravity.LEFT;
//        params.x = 0;
//        params.y = 200;
//        windowManager.addView(viewGroup, params);
//        viewGroup.setOnTouchListener(new View.OnTouchListener() {
//            private int initialX, initialY;
//            private float touchX, touchY;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        initialX = params.x;
//                        initialY = params.y;
//                        touchX = event.getRawX();
//                        touchY = event.getRawY();
//                        return true;
//
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = initialX + (int) (event.getRawX() - touchX);
//                        params.y = initialY + (int) (event.getRawY() - touchY);
//                        windowManager.updateViewLayout(viewGroup, params);
//                        return true;
//
//                    default:
//                        return false;
//                }
//            }
//        });
//
//        ImageButton captureButton = viewGroup.findViewById(R.id.screenshot_button);
//        ImageButton stopButton = viewGroup.findViewById(R.id.screenshot_stop_button);
//
//        stopButton.setOnClickListener((view) -> {
//            Intent intent = new Intent(this, ScreenshotService.class);
//            stopService(intent);
//            windowManager.removeView(viewGroup);
//        });
//
//        captureButton.setOnClickListener((view) -> {
//            windowManager.removeView(viewGroup);
//            Handler handler = new Handler(Looper.getMainLooper());
//            // Delay after click capture button by 1 seconds
//            handler.postDelayed(() -> {
//                captureScreenshot();
//                windowManager.addView(viewGroup, params);
//            }, 200);
//        });
//    }
//
//    private void removeOverlay() {
//        if (viewGroup != null) {
//            windowManager.removeView(viewGroup);
//            stopProjection(); // Stop the MediaProjection and release resources
//            // Call stopSelf to stop the service
//            stopSelf();
//
//        }
//    }
//    /**
//     * @author Dương Thành Luân
//     * @date 14/08/2024
//     * @desc notice the latest image which is sent by Media Projector
//     */
//    private void captureScreenshot() {
//        FileOutputStream fos = null;
//        Bitmap bitmap = null;
//        String fileName = "";
//        if (mImageReader == null) {
//            Log.e("Screenshot", "ImageReader is not initialized");
//            return;
//        }
//        try (Image image = mImageReader.acquireLatestImage()) {
//            if (image != null) {
//                Image.Plane[] planes = image.getPlanes();
//                ByteBuffer buffer = planes[0].getBuffer();
//                int pixelStride = planes[0].getPixelStride();
//                int rowStride = planes[0].getRowStride();
//                int rowPadding = rowStride - pixelStride * mWidth;
//                // create bitmap
//                bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
//                bitmap.copyPixelsFromBuffer(buffer);
//                // Set local date time
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    LocalDateTime localDateTime = LocalDateTime.now();
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                    fileName = mStoreDir + "/myscreen_" + localDateTime.format(formatter) + ".png";
//                    images.add(fileName);
//                    //imageGalleryAdapter.setImages(images);
//                }
//
//                // write bitmap to a file
//                fos = new FileOutputStream(fileName);
//                //imageGalleryAdapter.addItem(fileName);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException ioe) {
//                    ioe.printStackTrace();
//                }
//            }
//
//            if (bitmap != null) {
//                bitmap.recycle();
//            }
//
//        }
//    }
//
//}
