package com.example.echonote;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileSenderHelper {

    private static final String TAG = "FileSenderHelper";

    public static void sendFileToMobile(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "File does not exist: " + filePath);
            return;
        }

        Asset asset = createAssetFromFile(file);
        if (asset == null) {
            Log.e(TAG, "Failed to create asset from file");
            return;
        }

        PutDataMapRequest dataMapRequest = PutDataMapRequest.create("/mp4_file_transfer");
        DataMap dataMap = dataMapRequest.getDataMap();
        dataMap.putAsset("mp4_asset", asset);
        dataMap.putString("fileName", file.getName());
        dataMap.putLong("timestamp", System.currentTimeMillis());

        PutDataRequest request = dataMapRequest.asPutDataRequest();
        // Set urgent to prioritize transfer
        request.setUrgent();

        Wearable.getDataClient(context).putDataItem(request)
                .addOnSuccessListener(dataItem -> Log.d(TAG, "File sent successfully: " + file.getName()))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to send file", e));
    }

    private static Asset createAssetFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            int read = fis.read(fileBytes);
            if (read != file.length()) {
                Log.w(TAG, "Warning: file read size mismatch");
            }
            return Asset.createFromBytes(fileBytes);
        } catch (IOException e) {
            Log.e(TAG, "Error reading file for asset", e);
            return null;
        }
    }
}
