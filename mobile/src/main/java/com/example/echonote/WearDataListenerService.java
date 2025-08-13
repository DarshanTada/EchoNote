package com.example.echonote;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class WearDataListenerService extends WearableListenerService {

    private static final String TAG = "WearDataListener";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if ("/mp4_file".equals(item.getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(item);
                    Asset audioAsset = dataMapItem.getDataMap().getAsset("mp4_asset");
                    String fileName = dataMapItem.getDataMap().getString("file_name");

                    // Always save as .mp3 regardless of original extension
                    if (fileName != null) {
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex != -1) {
                            fileName = fileName.substring(0, dotIndex) + ".mp3";
                        } else {
                            fileName = fileName + ".mp3";
                        }
                    }

                    Log.d(TAG, "Received file: " + fileName);

                    if (audioAsset != null) {
                        saveAssetToFile(audioAsset, fileName);
                    }
                }
            }
        }
    }

    private void saveAssetToFile(Asset asset, String fileName) {
        if (asset == null) {
            Log.w(TAG, "Asset is null");
            return;
        }

        try {
            InputStream inputStream = Wearable.getDataClient(this).getFdForAsset(asset)
                    .getResult().getInputStream();

            if (inputStream == null) {
                Log.e(TAG, "InputStream for asset is null");
                return;
            }

            File dir = getExternalFilesDir(null);
            File folder = new File(dir, "EchoNoteAudio");
            if (!folder.exists()) folder.mkdirs();

            File outFile = new File(folder, fileName);
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
                Log.d(TAG, "File saved: " + outFile.getAbsolutePath());
            }

            // Notify RecordingListActivity UI to refresh
            Intent intent = new Intent("com.example.echonote.NEW_RECORDING_RECEIVED");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        } catch (Exception e) {
            Log.e(TAG, "Failed to save asset to file", e);
        }
    }
}
