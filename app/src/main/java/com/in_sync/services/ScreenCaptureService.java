package com.in_sync.services;


import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.reflect.TypeToken;
import com.in_sync.R;
import com.in_sync.actions.Action;
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
    private String appOpened="";
    private Action action;
    private Sequence sequence;
    private static int IMAGES_PRODUCED;
    private int ACCURACY_POINT;
    private Coordinate prev_point = new Coordinate(0, 0);
    private AccessibilityNodeInfo source;
    private List<com.in_sync.models.Action> flattenedActions;

    private com.in_sync.models.Action currentAction = null;
    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, START);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, ScreenCaptureService.class);
        intent.putExtra(ACTION, STOP);
        return intent;
    }

    private static boolean isStartCommand(Intent intent) {

        boolean res = intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                && intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), START);
        return res;
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
            com.in_sync.models.Action newAction = action.actionHandler(steps, reader, ScreenCaptureService.this, mWidth, mHeight, imageView, appOpened, source, sequence,currentAction, flattenedActions);
            if(newAction.getIndex() + 1 == flattenedActions.size()){
                currentAction = newAction;
                Log.e(TAG, "onImageAvailable: StopProjection" );
                stopProjection();
            }else{
                currentAction = newAction;
            }
            try (Image image = mImageReader.acquireLatestImage()) {}catch (Exception e){
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
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
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
        Log.e(TAG, "onAccessibilityEvent: "+ appOpened);
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
        json = intent.getExtras().get("json").toString();
        steps = bindStep(json);
        showOverlay();
        Log.e(TAG, "onStartCommand Services started");
        action = new Action(getApplicationContext(), ScreenCaptureService.this);
        if (intent != null) {
            json = intent.getExtras().get("json").toString();
            steps = bindStep(json);
        }
        if (isStartCommand(intent)) {
            Pair<Integer, Notification> notification = NotificationUtils.getNotification(this);
            startForeground(notification.first, notification.second);
            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra(DATA);
            startProjection(resultCode, data);
        } else if (isStopCommand(intent)) {
            stopProjection();
            stopSelf();
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startProjection(int resultCode, Intent data) {
        MediaProjectionManager mpManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
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
    }

    private Step[] bindStep(String json) {
        Gson gson = new Gson();
        Type actionListType = new TypeToken<List<com.in_sync.models.Action>>() {}.getType();
        List<com.in_sync.models.Action> actionsList = gson.fromJson(json, actionListType);
        flattenedActions = new ArrayList<com.in_sync.models.Action>();
        com.in_sync.models.Action startAction = new com.in_sync.models.Action();
        startAction.setIndex(0);
        flattenedActions.add(startAction);
        for (com.in_sync.models.Action action : flattenActions(actionsList)){
            flattenedActions.add(action);
        }
        TreeNode root = buildTree(flattenedActions);
        sequence = new Sequence(actionsList, flattenedActions, root);
        return gson.fromJson(json, Step[].class);
    }
    private static TreeNode buildTree(List<com.in_sync.models.Action> actions) {
        Map<Integer, TreeNode> nodeMap = new HashMap<>();
        TreeNode root = null;

        // Create nodes and map them by index
        for (com.in_sync.models.Action action : actions) {
            TreeNode node = new TreeNode(action);
            nodeMap.put(action.index, node);
        }
        TreeNode rootNode = new TreeNode(new com.in_sync.models.Action());
        root = rootNode;
        nodeMap.put(0, rootNode);
        // Establish parent-child relationships
        for (TreeNode node : nodeMap.values()) {
            com.in_sync.models.Action action = node.action;
            if(action.index!=0) {
                TreeNode parentNode = nodeMap.get(action.parent);
                if (parentNode != null) {
                    parentNode.addChild(node);
                }
            }
        }

        return root;
    }


    private static List<com.in_sync.models.Action> flattenActions(List<com.in_sync.models.Action> actions) {
        return actions.stream()
                .flatMap(action -> Stream.concat(
                        Stream.of(action),
                        Stream.concat(
                                action.isTrue != null ? flattenActions(action.isTrue).stream() : Stream.empty(),
                                action.isFalse != null ? flattenActions(action.isFalse).stream() : Stream.empty()
                        )
                ))
                .collect(Collectors.toList());
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

        ImageButton closeButton = overlayView.findViewById(R.id.stop_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay();
            }
        });
        imageView = overlayView.findViewById(R.id.screenshot_image_view);
    }

    private void removeOverlay() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            stopProjection();
            overlayView = null;
        }
    }
    private void pasteFromClipboard(String content) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        clipboard.setPrimaryClip(clipData);
        int supportedActions = source.getActions();
        boolean isSupported = (supportedActions & AccessibilityNodeInfoCompat.ACTION_PASTE) == AccessibilityNodeInfoCompat.ACTION_PASTE;
        if (isSupported) {
            source.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE);

        }
        Log.e("Error", String.format("AccessibilityNodeInfoCompat.ACTION_PASTE %1$s supported", isSupported ? "is" : "is NOT"));

    }
}