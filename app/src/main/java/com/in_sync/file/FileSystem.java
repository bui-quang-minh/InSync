package com.in_sync.file;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileSystem {
    public static List<String> getFileName(Context context) {
        String FOLDER_PATH = Objects.requireNonNull(context.getExternalFilesDir(null)).getAbsolutePath() + "/screenshots/";
        List<String> filesName = new ArrayList<>();
        try {
            File folder = new File(FOLDER_PATH);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles((file) -> file.getName().endsWith(".png"));
                if (files != null) {
                    for (File file : files) {
                        if (file.length() > 0)
                            filesName.add(FOLDER_PATH + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "ERROR: CAN NOT READ IMAGE FOLDER", Toast.LENGTH_SHORT).show();
        }

        return filesName;
    }
}
