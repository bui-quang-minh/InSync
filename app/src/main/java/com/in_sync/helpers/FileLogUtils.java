package com.in_sync.helpers;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogUtils {
    private static final String TAG = "FileLogUtils";
    private static final String DIRECTORY_NAME = "Log_InSysnc";

// Kiểm tra và yêu cầu quyền trong Activity của bạn
    public static void writeLogToFile(String text) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now  = new Date();
        String fileName = format.format(now) + "logdata.log";
        if (isExternalStorageWritable()) {
            // Lấy đường dẫn thư mục ngoài (External Storage Directory)
            File rootDirectory = Environment.getExternalStorageDirectory();
            File directory = new File(rootDirectory, DIRECTORY_NAME);

            // Tạo thư mục nếu chưa tồn tại
            if (!directory.exists()) {
                directory.mkdirs();
                Log.d(TAG, "Thư mục đã được tạo thành công!");
            }

            // Tạo file trong thư mục
            File file = new File(directory, fileName);

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                // Mở file để ghi tiếp nội dung
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
                writer.append(text);
                writer.newLine();
                writer.close();

                Log.d(TAG, "Nội dung đã được ghi vào file thành công!");
                Log.d(TAG, "File được lưu tại: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Lỗi khi ghi vào file: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "External storage không sẵn sàng để ghi.");
        }
    }
    public static String readLogFromFile() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String fileName = format.format(now) + "logdata.log";
        StringBuilder content = new StringBuilder();

        if (isExternalStorageWritable()) {
            // Lấy đường dẫn thư mục ngoài (External Storage Directory)
            File rootDirectory = Environment.getExternalStorageDirectory();
            File directory = new File(rootDirectory, DIRECTORY_NAME);
            File file = new File(directory, fileName);

            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    InputStreamReader isr = new InputStreamReader(fis);
                    BufferedReader reader = new BufferedReader(isr);
                    String line;

                    // Đọc từng dòng trong file và ghi vào log
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                        Log.d(TAG, "File content: " + line);

                    }
                    reader.close();
                    Log.d(TAG, "Đọc nội dung file thành công!");
                    return content.toString();


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Lỗi khi đọc nội dung file: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "File không tồn tại.");
                return  "File don't exist.";
            }
        } else {
            Log.e(TAG, "External storage không sẵn sàng để đọc.");
            return  "External storage don't ready read.";
        }
        return content.toString();
    }

    // Kiểm tra nếu External Storage có thể ghi
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
