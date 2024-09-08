package com.in_sync.helpers;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.IOException;
import java.util.UUID;

import lombok.Getter;

public class DeviceInfoUtils {

    // Custom class to store storage info as long
    @Getter
    public static class StorageInfo {
        private long totalStorage;
        private long usedStorage;

        public StorageInfo(long totalStorage, long usedStorage) {
            this.totalStorage = totalStorage;
            this.usedStorage = usedStorage;
        }

    }

    public static StorageInfo getStorageDetails(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

            try {
                // Get default storage UUID
                UUID defaultUuid = storageManager.getUuidForPath(Environment.getDataDirectory());

                // Get StorageStatsManager
                StorageStatsManager storageStatsManager = (StorageStatsManager) context.getSystemService(Context.STORAGE_STATS_SERVICE);

                // Get the total and used storage space in bytes
                long totalBytes = storageStatsManager.getTotalBytes(defaultUuid);
                long freeBytes = storageStatsManager.getFreeBytes(defaultUuid);
                long usedBytes = totalBytes - freeBytes;
                // Convert bytes to gigabytes (GB)
                long totalStorageGB = bytesToGB(totalBytes);
                long usedStorageGB = bytesToGB(usedBytes);

                // Return new StorageInfo object
                return new StorageInfo(totalStorageGB, usedStorageGB);

            } catch (IOException e) {
                e.printStackTrace();
                return new StorageInfo(-1, -1); // Error handling
            }
        } else {
            // Fallback for older Android versions
            StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
            long blockSize = statFs.getBlockSizeLong();
            long totalBlocks = statFs.getBlockCountLong();
            long availableBlocks = statFs.getAvailableBlocksLong();
            long totalStorageBytes = totalBlocks * blockSize; // Bytes
            long usedStorageBytes = (totalBlocks - availableBlocks) * blockSize; // Bytes

            // Convert bytes to gigabytes (GB)
            long totalStorageGB = bytesToGB(totalStorageBytes);
            long usedStorageGB = bytesToGB(usedStorageBytes);

            // Return new StorageInfo object
            return new StorageInfo(totalStorageGB, usedStorageGB);
        }
    }

    private static long bytesToGB(long bytes) {
        return bytes / (1000 * 1000 * 1000); // This code works but I don't know why
        //return bytes / (1024 * 1024 * 1024); // Maybe the manufacturer want to make pi = 3

    }
}
