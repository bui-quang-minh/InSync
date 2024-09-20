package com.in_sync.actions;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.media.Image;
import android.media.ImageReader;
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
        } else if (currentAction.getIndex() == -1) {
            Log.e(TAG, "All actions have been executed. Projection Stop");
            return currentAction;
        } else {
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
                    handleDelayAction(currentAction, sequence);
                    return sequence.getActionByIndex(index);

            }
        }
        return currentAction;
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
        Log.e(TAG, mmr.maxVal + "  sfasdf");
        if (mmr.maxVal >= 0.60) {
            if (Imgproc.TM_CCOEFF_NORMED == Imgproc.TM_SQDIFF || Imgproc.TM_CCOEFF_NORMED == Imgproc.TM_SQDIFF_NORMED) {
                matchLoc = mmr.minLoc;
            } else {
                matchLoc = mmr.maxLoc;
            }
            Imgproc.rectangle(mat, matchLoc, new Point(matchLoc.x + template.cols(), matchLoc.y + template.rows()), new Scalar(255, 0, 0), 2);
            Bitmap outputBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, outputBitmap);

            IMAGES_PRODUCED++;
            if (IMAGES_PRODUCED == 1 || (prev_point.getX() == (float) matchLoc.x && prev_point.getY() == (float) matchLoc.y)) {
                ACCURACY_POINT++;
                prev_point.setX((float) matchLoc.x);
                prev_point.setY((float) matchLoc.y);
            }
            if ((prev_point.getX() != (float) matchLoc.x || prev_point.getY() != (float) matchLoc.y)) {
                ACCURACY_POINT = 0;
                prev_point.setX((float) matchLoc.x);
                prev_point.setY((float) matchLoc.y);
            }
            if (ACCURACY_POINT >= 3) {
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
            }
        } else {
            Log.e(TAG, "No image match found");
        }
        Log.e(TAG, "captured image: " + IMAGES_PRODUCED + " current step: " + index + " Accuray Point: " + mmr.maxVal + " prev_point: " + prev_point.getX() + " " + prev_point.getY() + " accuracy: " + ACCURACY_POINT + " ImageSize: " + bmp.getWidth() + " " + bmp.getHeight());
        return sequence.traverseAction(false, currentAction);
    }

    private void handleDelayAction(com.in_sync.models.Action currentAction, Sequence sequence) {
        Log.e(TAG, "Trạng thái chờ: " + isDelay);
        if (!isDelay) {
            isDelay = true;
            int delayTime = currentAction.getDuration(); // Giả sử getDuration() trả về thời gian chờ bằng mili giây

            // Tạo một Handler để xử lý việc chờ
            new Handler().postDelayed(() -> {
                // Gọi sequence.traverseAction và nhận giá trị trả về
                com.in_sync.models.Action resultAction = sequence.traverseAction(true, currentAction);
                // Sau khi chờ xong, cập nhật trạng thái và trả về kết quả qua callback
                isDelay = false;
                Log.e(TAG, "Cập nhật trạng thái chờ đợi: " + isDelay);
                int tempIndex = resultAction.getIndex();
                if(tempIndex > index) {
                 while(tempIndex > index){
                     index++;
                 }
                }else{
                    while(tempIndex < index){
                        index--;
                    }
                }

            }, delayTime);
        }

    }


    public interface ActionCallback {
        void onActionCompleted(com.in_sync.models.Action action);
    }

}
