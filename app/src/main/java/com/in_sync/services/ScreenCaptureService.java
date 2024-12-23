package com.in_sync.services;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
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
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.util.Pair;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.google.gson.Gson;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Type;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.reflect.TypeToken;
import com.in_sync.R;
import com.in_sync.actions.Action;
import com.in_sync.actions.definition.ActionDef;
import com.in_sync.activities.MainActivity;
import com.in_sync.helpers.NotificationUtils;
import com.in_sync.models.Coordinate;
import com.in_sync.models.Sequence;
import com.in_sync.models.Step;
import com.in_sync.models.TreeNode;

public class ScreenCaptureService extends AccessibilityService {

    private static final String TAG = "ScreenCaptureService";
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
    private OrientationChangeCallback mOrientationChangeCallback;
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
    private Queue<com.in_sync.models.Action> actionQueue = new LinkedList<>();
    private static Context contexts;

    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, START);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        contexts = context;
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
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
            try{
                com.in_sync.models.Action newAction = action.actionHandler(reader, ScreenCaptureService.this, mWidth, mHeight, imageView, appOpened, source, sequence, currentAction);
                if(Objects.equals(newAction.getActionType(), ActionDef.END_RUN)){
                    Log.e(TAG, "onImageAvailable: Endrun exe" );
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        // For Android 12 (API 31) and above
                        VibratorManager vibratorManager = (VibratorManager) contexts.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
                        Vibrator vibrator = vibratorManager.getDefaultVibrator();
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)); // Vibrate for 500ms
                    } else {
                        // For Android 11 (API 30) and below
                        Vibrator vibrator = (Vibrator) contexts.getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(500); // Vibrate for 500ms
                    }
                    stopProjection();
                    removeOverlay();
                    sequence.clearActions();
                }
                else if (newAction == null) {
                    currentAction = newAction;
                    Log.e(TAG, "onImageAvailable: StopProjection");
                    stopProjection();
                } else {
                    currentAction = newAction;
                }
                try (Image image = mImageReader.acquireLatestImage()) {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        appOpened = event.getText().toString().trim();
        source = event.getSource();
        // If = action = paste, then paste the content from the clipboard
        Log.e(TAG, "onAccessibilityEvent: " + appOpened);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
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
        if (intent != null) {
            json = intent.getExtras().get("json").toString();
            try{
                bindStep(json);
                showOverlay();
            }catch (Exception e) {
                Toast.makeText(contexts, "Invalid JSON", Toast.LENGTH_SHORT).show();
                stopSelf();
                stopProjection();
                removeOverlay();
                e.printStackTrace();
            }

        }

        currentAction = null;
        Log.e(TAG, "onStartCommand Services started");
        action = new Action(getApplicationContext(), ScreenCaptureService.this);
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
                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
                createVirtualDisplay();
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }
                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
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
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
        //log relevent information
        Log.e(TAG, "createVirtualDisplay: " + mWidth + " " + mHeight + " " + mDensity);
    }

    private void bindStep(String json) {
        Gson gson = new Gson();
        Type actionListType = new TypeToken<List<com.in_sync.models.Action>>() {}.getType();
        List<com.in_sync.models.Action> actionsList = gson.fromJson(json, actionListType);
        actionQueue.clear();
        tranferListToQueue(actionsList);
        Log.e(TAG, "QUEUE SIZE: " + actionQueue.size());
        for (com.in_sync.models.Action action: actionQueue) {
            Log.e(TAG, "QUEUE DATA: "+action.getIndex()+ " ACTION:" + action.getActionType());
        }

        sequence = new Sequence(actionQueue, contexts);
    }

    public  void tranferListToQueue (List<com.in_sync.models.Action> actions){
        for (com.in_sync.models.Action singleAction:actions) {
            if(singleAction.getActionType().equals(ActionDef.FOR)) {
                for (int i = 0; i < singleAction.getTimes(); i++) {
                    tranferListToQueue(singleAction.getExecuteActions());
                }
            }else{
                actionQueue.add(singleAction);
            }
        }
    }



    private void showOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        overlayView = inflater.inflate(R.layout.overlay_layout, null);

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