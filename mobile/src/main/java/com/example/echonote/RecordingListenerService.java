package com.example.echonote;

import android.util.Log;

import com.example.shared.RecordModel;
import com.example.shared.RecordStorageHelper;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class RecordingListenerService extends WearableListenerService {

    private static final String TAG = "RecordingListenerService";
    private static final String RECORDING_PATH = "/recording_data";
    private static final String MP4_PATH = "/mp4_file";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        try {
            for (DataEvent event : dataEvents) {
                if (event.getType() == DataEvent.TYPE_CHANGED) {
                    String path = event.getDataItem().getUri().getPath();
                    Log.d(TAG, "onDataChanged: path=" + path);

                    if (RECORDING_PATH.equals(path)) {
                        DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        final String title = dataMap.getString("title");
                        final String fileName = dataMap.getString("fileName");
                        final long timestamp = dataMap.getLong("timestamp");
                        final Asset audioAsset = dataMap.getAsset("audio");

                        if (audioAsset == null) {
                            Log.e(TAG, "No audio asset found in DataMap");
                            continue;
                        }

                        // Latest API: getFdForAsset returns ParcelFileDescriptor
                        Wearable.getDataClient(this).getFdForAsset(audioAsset)
                                .addOnSuccessListener(parcelFileDescriptor -> {
                                    if (parcelFileDescriptor == null) {
                                        Log.e(TAG, "ParcelFileDescriptor is null");
                                        return;
                                    }

                                    File dir = getExternalFilesDir(null);
                                    File folder = new File(dir, "EchoNoteAudio");
                                    if (!folder.exists()) {
                                        folder.mkdirs();
                                    }
                                    File outFile = new File(folder, fileName);

                                    Log.d(TAG, "Saving audio asset to: " + outFile.getAbsolutePath());

                                    try (
                                            InputStream assetInputStream = parcelFileDescriptor.getInputStream();
                                            FileOutputStream fos = new FileOutputStream(outFile)
                                    ) {
                                        byte[] buffer = new byte[8192];
                                        int read;
                                        while ((read = assetInputStream.read(buffer)) != -1) {
                                            fos.write(buffer, 0, read);
                                        }
                                        fos.flush();

                                        Log.d(TAG, "Audio file saved: " + outFile.getAbsolutePath());

                                        // Do not try to get data from path, just save the file

                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to write asset to file", e);
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Failed to get asset ParcelFileDescriptor", e));
                    } else if (MP4_PATH.equals(path)) {
                        DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                        final String fileName = dataMap.getString("file_name");
                        final long timestamp = dataMap.getLong("timestamp");
                        final Asset mp4Asset = dataMap.getAsset("mp4_asset");

                        if (mp4Asset == null) {
                            Log.e(TAG, "No mp4 asset found in DataMap");
                            continue;
                        }

                        Wearable.getDataClient(this).getFdForAsset(mp4Asset)
                                .addOnSuccessListener(parcelFileDescriptor -> {
                                    if (parcelFileDescriptor == null) {
                                        Log.e(TAG, "ParcelFileDescriptor is null");
                                        return;
                                    }
                                    File dir = getExternalFilesDir(null);
                                    File folder = new File(dir, "EchoNoteAudio");
                                    if (!folder.exists()) {
                                        folder.mkdirs();
                                    }
                                    File outFile = new File(folder, fileName);
                                    Log.d(TAG, "Saving mp4 asset to: " + outFile.getAbsolutePath());
                                    try (
                                            InputStream assetInputStream = parcelFileDescriptor.getInputStream();
                                            FileOutputStream fos = new FileOutputStream(outFile)
                                    ) {
                                        byte[] buffer = new byte[8192];
                                        int read;
                                        while ((read = assetInputStream.read(buffer)) != -1) {
                                            fos.write(buffer, 0, read);
                                        }
                                        fos.flush();
                                        Log.d(TAG, "MP4 file saved: " + outFile.getAbsolutePath());
                                        // Save RecordModel for this mp4 file so it appears in the list, use fileName as title
                                        RecordModel recordModel = new RecordModel(fileName, outFile.getAbsolutePath(), timestamp);
                                        RecordStorageHelper.saveRecord(RecordingListenerService.this, recordModel);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Failed to write mp4 asset to file", e);
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Failed to get mp4 asset ParcelFileDescriptor", e));
                    }
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                    Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onDataChanged failed", e);
        } finally {
            dataEvents.release();
        }
    }
}
