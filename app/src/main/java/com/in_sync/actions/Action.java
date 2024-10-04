package com.in_sync.actions;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.in_sync.actions.definition.ActionDef;
import com.in_sync.models.Coordinate;
import com.in_sync.models.Sequence;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.net.URL;
import java.nio.ByteBuffer;


public class Action extends ActionDef {
    private int index = 0;
    private int count = 0;
    private static int IMAGES_PRODUCED;
    private int ACCURACY_POINT;
    private Coordinate prev_point = new Coordinate(0, 0);
    private static final String TAG = Action.class.getSimpleName();
    private Context context;
    public static String appOpenedResend;
    private AccessibilityService accessibilityService;
    private boolean isDelay = false; // Biến cờ để kiểm soát việc chờ đợi


    public Action(Context context, AccessibilityService accessibilityService) {
        this.context = context;
        this.accessibilityService = accessibilityService;
    }

    public static void clickAction(float x, float y, int duration, int tries, AccessibilityService accessibilityService) {
        Path path = new Path();
        path.moveTo(x, y);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
        boolean result = accessibilityService.dispatchGesture(builder.build(), new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                Log.e(TAG, "Gesture completed");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                Log.e(TAG, "Gesture cancelled");
            }

        }, null);
        Log.e(TAG, "Gesture Result: " + result);
    }

    public com.in_sync.models.Action actionHandler(ImageReader mImageReader, AccessibilityService accessibilityService, int mWidth, int mHeight,
                                                   android.widget.ImageView imageView, String appOpened, AccessibilityNodeInfo source, Sequence sequence,
                                                   com.in_sync.models.Action currentAction) {

        if (currentAction == null) {
            currentAction = new com.in_sync.models.Action();
            currentAction = sequence.getFirstAction();
            return currentAction;
        }
//       else if (currentAction == null) {
//            Log.e(TAG, "All actions have been executed. Projection Stop");
//            return currentAction;
//        }
        else {
            switch (currentAction.getActionType()) {
                case Action.CLICK:
                    Bitmap bitmap = null;
                    try (Image image = mImageReader.acquireLatestImage()) {
                        if (image != null) {
                            //
                            //GET SCREEN BITMAP
                            //
                            bitmap = getScreenBitmap(image);
                            Mat mat = createMatFromBitmap(bitmap);
                            Utils.bitmapToMat(bitmap, mat);
                            //
                            //GET TEMPLATE BITMAP
                            //
                            Bitmap bmp = getBitmapFromURL(currentAction.getOn());
                            Mat template = createMatFromBitmap(bmp);
                            Utils.bitmapToMat(bmp, template);
                            imageView.post(() -> {
                                // Update UI elements here, e.g.:
                                imageView.setImageBitmap(bmp);
                            });
                            //
                            //PERFORM TEMPLATE MATCHING
                            //
                            Mat result = createMatFromBitmap(bitmap);
                            Imgproc.matchTemplate(mat, template, result, Imgproc.TM_CCOEFF_NORMED);
                            //
                            // Find the location of the best match
                            //
                            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
                            Point matchLoc = new Point();
                            return processTemplateMatchingResult(mmr, mat, template, imageView, bmp, index, accessibilityService, matchLoc, currentAction, sequence);

                        }
                    } catch (Exception e) {
                        sequence.traverseAction(false, currentAction);
                        e.printStackTrace();
                    }
                    break;
                case ActionDef.DELAY:
                    return handleDelayAction(currentAction, sequence);
                case ActionDef.SWIPE:
                    if (currentAction.getOn().equals("UP")) {
                        return Action.swipeUpAction(mWidth, mHeight, currentAction.getDuration(), currentAction.getTries(), accessibilityService, sequence, currentAction);
                    } else if (currentAction.getOn().equals("DOWN")) {
                        return Action.swipeDownAction(mWidth, mHeight, currentAction.getDuration(), currentAction.getTries(), accessibilityService, sequence, currentAction);
                    } else if (currentAction.getOn().equals("LEFT")) {
                        return Action.swipeLeftAction(mWidth, mHeight, currentAction.getDuration(), currentAction.getTries(), accessibilityService, sequence, currentAction);
                    } else if (currentAction.getOn().equals("RIGHT")) {
                        return Action.swipeRightAction(mWidth, mHeight, currentAction.getDuration(), currentAction.getTries(), accessibilityService, sequence, currentAction);
                    }
                case ActionDef.ZOOM:
                    if (currentAction.getOn().equals("IN")) {
                        return Action.zoomIn(mWidth, mHeight, currentAction.getDuration(), currentAction.getTries(), accessibilityService, sequence, currentAction);
                    } else if (currentAction.getOn().equals("OUT")) {
                        return Action.zoomOut(mWidth, mHeight, currentAction.getDuration(), currentAction.getTries(), accessibilityService, sequence, currentAction);
                    }
                case ActionDef.OPEN_APP:
                    return Action.openApp(currentAction.getOn(), accessibilityService, sequence, currentAction);
                case ActionDef.ROTATE:
                    return Action.rotationAction(mWidth, mHeight, accessibilityService, sequence, currentAction);
            }
        }
        return currentAction;
    }

    private static com.in_sync.models.Action openApp(String on, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        if (on != null && !on.isEmpty()) {
            // Create an intent to open the app using the package name from the "on" field
            try {
                Intent launchIntent = new Intent(Intent.ACTION_VIEW);
                //Flag_activity_new_task
                launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                launchIntent.setData(Uri.parse("android-app://".concat(on)));
                accessibilityService.startActivity(launchIntent);
                return sequence.traverseAction(true, currentAction);
            } catch (Exception e) {
                Log.e("OpenAppAction", "Error launching app: " + e.getMessage());
                return sequence.traverseAction(false, currentAction);
            }
        } else {
            Log.e("Action", "Package name is empty or null.");
            return sequence.traverseAction(false, currentAction);
        }
    }

    private static com.in_sync.models.Action zoomOut(int mWidth, int mHeight, int duration, int tries, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        int centerX = mWidth / 2;
        int centerY = mHeight / 2;

        // Define starting positions (fingers start apart)
        Path path1 = new Path();
        path1.moveTo(centerX - 200, centerY);  // First finger starts to the left
        Path path2 = new Path();
        path2.moveTo(centerX + 200, centerY);  // Second finger starts to the right

        // Fingers move towards the center
        path1.lineTo(centerX - 50, centerY);
        path2.lineTo(centerX + 50, centerY);

        // Perform the gesture using two fingers
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path1, 0, duration));
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path2, 0, duration));

        // Dispatch the gesture
        boolean result = accessibilityService.dispatchGesture(gestureBuilder.build(), null, null);

        // Log the result
        if (result) {
            Log.e(TAG, "Zoom out gesture succeeded");
            return sequence.traverseAction(true, currentAction);
        } else {
            Log.e(TAG, "Zoom out gesture failed");
            return sequence.traverseAction(false, currentAction);
        }
    }

    private static com.in_sync.models.Action zoomIn(int mWidth, int mHeight, int duration, int tries, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        // Define the center of the screen
        int centerX = mWidth / 2;
        int centerY = mHeight / 2;

        // Define starting positions (fingers start close together)
        Path path1 = new Path();
        path1.moveTo(centerX - 50, centerY);  // First finger starts close to the center
        Path path2 = new Path();
        path2.moveTo(centerX + 50, centerY);  // Second finger starts close to the center

        // Fingers move farther apart
        path1.lineTo(centerX - 200, centerY);
        path2.lineTo(centerX + 200, centerY);

        // Perform the gesture using two fingers
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path1, 0, duration));
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path2, 0, duration));

        // Dispatch the gesture
        boolean result = accessibilityService.dispatchGesture(gestureBuilder.build(), null, null);

        // Log the result
        if (result) {
            Log.e(TAG, "Zoom in gesture succeeded");
            return sequence.traverseAction(true, currentAction);
        } else {
            Log.e(TAG, "Zoom in gesture failed");
            return sequence.traverseAction(false, currentAction);
        }
    }


    private static com.in_sync.models.Action swipeRightAction(int mWidth, int mHeight, int duration, int tries, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        Path path = new Path();
        path.moveTo(mWidth * 0.2f, mHeight / 2f);  // Start from left center
        path.lineTo(mWidth * 0.8f, mHeight / 2f);  // Move to right center
        boolean res = performSwipe(path, duration, tries, accessibilityService);
        return sequence.traverseAction(res, currentAction);
    }


    private static com.in_sync.models.Action swipeLeftAction(int mWidth, int mHeight, int duration, int tries, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        Path path = new Path();
        path.moveTo(mWidth * 0.8f, mHeight / 2f);  // Start from right center
        path.lineTo(mWidth * 0.2f, mHeight / 2f);  // Move to left center
        boolean res = performSwipe(path, duration, tries, accessibilityService);
        return sequence.traverseAction(res, currentAction);
    }

    private static com.in_sync.models.Action swipeDownAction(int mWidth, int mHeight, int duration, int tries, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        Path path = new Path();
        path.moveTo(mWidth / 2f, mHeight * 0.2f);  // Start from top center
        path.lineTo(mWidth / 2f, mHeight * 0.8f);  // Move to bottom center
        boolean res = performSwipe(path, duration, tries, accessibilityService);
        return sequence.traverseAction(res, currentAction);
    }

    private static com.in_sync.models.Action swipeUpAction(int mWidth, int mHeight, int duration, int tries, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        Path path = new Path();
        path.moveTo(mWidth / 2f, mHeight * 0.8f);  // Start from bottom center
        path.lineTo(mWidth / 2f, mHeight * 0.2f);  // Move to top center
        boolean res = performSwipe(path, duration, tries, accessibilityService);
        return sequence.traverseAction(res, currentAction);
    }

    private static boolean performSwipe(Path path, int duration, int tries, AccessibilityService accessibilityService) {
        GestureDescription.Builder builder = new GestureDescription.Builder();
        builder.addStroke(new GestureDescription.StrokeDescription(path, 0, duration));
        boolean result = accessibilityService.dispatchGesture(builder.build(), null, null);
        if (result) {
            Log.e(TAG, "Swipe gesture succeeded");
            return true;
        } else {
            Log.e(TAG, "Swipe gesture failed on attempt: ");
        }
        return false;
    }

    public static Bitmap getBitmapFromURL(String on) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new URL(on).openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getScreenBitmap(Image image) {
        Bitmap bitmap = null;
        if (image != null) {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * image.getWidth();
            bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            image.close();
        }
        return bitmap;
    }

    public static Mat createMatFromBitmap(Bitmap bitmap) {
        return new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
    }


    private void pasteFromClipboard(String content, AccessibilityNodeInfo source) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        clipboard.setPrimaryClip(clipData);
        int supportedActions = source.getActions();
        boolean isSupported = (supportedActions & AccessibilityNodeInfoCompat.ACTION_PASTE) == AccessibilityNodeInfoCompat.ACTION_PASTE;
        if (isSupported) {
            source.performAction(AccessibilityNodeInfoCompat.ACTION_PASTE);
        }
        Log.e("Error", String.format("AccessibilityNodeInfoCompat.ACTION_PASTE %1$s supported", isSupported ? "is" : "is NOT"));

    }

    public com.in_sync.models.Action processTemplateMatchingResult(Core.MinMaxLocResult mmr, Mat mat, Mat template, android.widget.ImageView imageView, Bitmap bmp, int index, AccessibilityService accessibilityService, Point matchLoc, com.in_sync.models.Action currentAction, Sequence sequence) {
        Log.e(TAG, mmr.maxVal + " accurate value");
        if (mmr.maxVal >= 0.70) {
            if (Imgproc.TM_CCOEFF_NORMED == Imgproc.TM_SQDIFF || Imgproc.TM_CCOEFF_NORMED == Imgproc.TM_SQDIFF_NORMED) {
                matchLoc = mmr.minLoc;
            } else {
                matchLoc = mmr.maxLoc;
            }
            Imgproc.rectangle(mat, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(255, 0, 0), 2);
            Bitmap outputBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, outputBitmap);
            Action.clickAction((float) matchLoc.x + (float) bmp.getWidth() / 2,
                    (float) matchLoc.y + (float) bmp.getHeight() / 2,
                    currentAction.getDuration(),
                    currentAction.getTries(),
                    accessibilityService);
            ACCURACY_POINT = 0;
            IMAGES_PRODUCED = 0;

            Log.e("Source", "click condition is true");
            com.in_sync.models.Action resultAction = sequence.traverseAction(true, currentAction);
            this.index = resultAction.getIndex();
            Log.e("No image match found", this.index + " ");
            return resultAction;

        } else {
            Log.e(TAG, "No image match found");
        }
        Log.e(TAG, "captured image: " + IMAGES_PRODUCED + " current step: " + index + " Accuray Point: " + mmr.maxVal + " prev_point: " + prev_point.getX() + " " + prev_point.getY() + " accuracy: " + ACCURACY_POINT + " ImageSize: " + bmp.getWidth() + " " + bmp.getHeight());
        return sequence.traverseAction(false, currentAction);
    }

    private static com.in_sync.models.Action rotationAction(int mWidth, int mHeight, AccessibilityService accessibilityService, Sequence sequence, com.in_sync.models.Action currentAction) {
        boolean result = false;

        int degree = currentAction.getDegrees();
        int duration = currentAction.getDuration();
        String orientation = currentAction.getOn();
        int count = degree / 90;
        int degreeRemain = degree % 90;
        if(degreeRemain == 0){
            count = (int)degree / 90;
            for(int i = 0; i < count; i++){
                result = rotationWithDegree(mWidth, mHeight, accessibilityService, duration, 90, orientation);
            }
        }else{
            for(int i = 0; i < count; i++){
                result = rotationWithDegree(mWidth, mHeight, accessibilityService, duration, 90, orientation);
            }
            result = rotationWithDegree(mWidth, mHeight, accessibilityService, duration, degreeRemain, orientation);
        }




        if (result) {
            Log.e(TAG, "rotation gesture succeeded");
            return sequence.traverseAction(true, currentAction);
        } else {
            Log.e(TAG, "rotation gesture failed");
            return sequence.traverseAction(false, currentAction);
        }
    }

    private static boolean rotationWithDegree(int mWidth, int mHeight, AccessibilityService accessibilityService,int durationAction, int degreeAction, String orientationAction) {
        int centerX = mWidth / 2;
        int centerY = mHeight / 2;
        // if flag  equas 1 is rotation right and -1 is rotation left
        boolean result = false;


        double angleRadians = Math.toRadians(degreeAction);
        int lengthPathX = (int)(Math.sin(angleRadians) * centerX);
        int lengthMoveY = (int)Math.sqrt(Math.pow(centerX, 2) - Math.pow(lengthPathX, 2));
        if (orientationAction.equals("RIGHT")) {
            // Tạo Path cho ngón tay đứng yên
            Path stationaryPath = new Path();
            stationaryPath.moveTo(centerX, centerY);

            // Tạo Path cho ngón tay di chuyển thuận chiều kim đồng hồ
            Path movingPath = new Path();
            movingPath.moveTo(centerX - lengthPathX, lengthMoveY);
            movingPath.lineTo(centerX + lengthPathX, lengthMoveY);

            // Perform the gesture using two fingers
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(stationaryPath, 0, durationAction));
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(movingPath, 0, durationAction));

            // Dispatch the gesture
            result = accessibilityService.dispatchGesture(gestureBuilder.build(), null, null);

            // Log the result

        } else {
            Path stationaryPath = new Path();
            stationaryPath.moveTo(centerX, centerY);

            // Tạo Path cho ngón tay di chuyển ngược chiều kim đồng hồ
            Path movingPath = new Path();
            movingPath.moveTo(centerX + lengthPathX, lengthMoveY);
            movingPath.lineTo(centerX - lengthPathX, lengthMoveY);

            // Perform the gesture using two fingers
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(stationaryPath, 0, durationAction));
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(movingPath, 0, durationAction));

            // Dispatch the gesture
            result = accessibilityService.dispatchGesture(gestureBuilder.build(), null, null);
        }
        return result;
    }
    private com.in_sync.models.Action handleDelayAction(com.in_sync.models.Action currentAction, Sequence sequence) {
        Log.e(TAG, "Trạng thái chờ: " + currentAction.getIndex() + " Status: index" + " " + isDelay);
        if (!isDelay) {
            isDelay = true;
            int delayTime = currentAction.getDuration(); // Giả sử getDuration() trả về thời gian chờ bằng mili giây
            // Tạo một Handler để xử lý việc chờ
            new Handler().postDelayed(() -> {
                count++;
                isDelay = false;
                Log.e(TAG, "Cập nhật trạng thái chờ đợi: " + isDelay);
            }, delayTime);
        }
        if (count != 0) {
            count = 0;
            return sequence.traverseAction(true, currentAction);
        } else {
            return sequence.traverseAction(false, currentAction);
        }
    }


    public interface ActionCallback {
        void onActionCompleted(com.in_sync.models.Action action);
    }

}
