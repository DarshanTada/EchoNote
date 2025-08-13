// package: com.example.mobile
package com.example.echonote; // or your shared package name

import android.util.Log;

import com.example.shared.RecordModel;
import com.example.shared.RecordStorageHelper;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import com.google.android.gms.wearable.ChannelClient;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class RecordingChannelService extends WearableListenerService {

    private static final String TAG = "RecordingChannelSvc";
    private static final String CHANNEL_PATH = "/recording_channel";

    @Override
    public void onChannelOpened(ChannelClient.Channel channel) {
        Log.d(TAG, "onChannelOpened: path=" + channel.getPath() + " node=" + channel.getNodeId());
        if (!CHANNEL_PATH.equals(channel.getPath())) {
            Log.d(TAG, "Ignoring channel with different path: " + channel.getPath());
            return;
        }

        Wearable.getChannelClient(this)
                .getInputStream(channel)
                .addOnSuccessListener(inputStream -> {
                    // 'inputStream' is already the stream you read from
                    File dir = getExternalFilesDir(null);
                    File folder = new File(dir, "EchoNoteAudio");
                    if (!folder.exists()) folder.mkdirs();

                    File outFile = new File(folder, "received_" + System.currentTimeMillis() + ".txt"); // For testing, save as .txt
                    Log.d(TAG, "Saving incoming stream to: " + outFile.getAbsolutePath());

                    new Thread(() -> {
                        long startTime = System.currentTimeMillis();
                        long totalBytes = 0;
                        try (FileOutputStream fos = new FileOutputStream(outFile)) {
                            byte[] buffer = new byte[65536];
                            int read;
                            Log.d(TAG, "Begin reading from inputStream...");
                            while ((read = inputStream.read(buffer)) != -1) {
                                fos.write(buffer, 0, read);
                                totalBytes += read;
                            }
                            fos.flush();
                            long endTime = System.currentTimeMillis();
                            Log.d(TAG, "Finished reading inputStream. Total bytes: " + totalBytes + ", Time: " + (endTime - startTime) + " ms");

                            // Only create a new RecordModel with the mobile's local file path
                            RecordModel record = new RecordModel(outFile.getName(), outFile.getAbsolutePath(), System.currentTimeMillis());
                            RecordStorageHelper.saveRecord(this, record);
                            Log.d(TAG, "Saved new record to records.dat: " + outFile.getAbsolutePath());

                            int count = RecordStorageHelper.loadRecords(this).size();
                            Log.d(TAG, "Saved file and record. Total saved records: " + count);

                        } catch (Exception e) {
                            Log.e(TAG, "Error writing incoming channel to file", e);
                        } finally {
                            try {
                                inputStream.close();
                                Wearable.getChannelClient(this).close(channel);
                            } catch (Exception ignored) {}
                        }
                    }).start();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get input stream for channel", e));
    }


    @Override
    public void onChannelClosed(ChannelClient.Channel channel, int closeReason, int appSpecificErrorCode) {
        Log.d(TAG, "onChannelClosed: " + channel.getPath() + " reason=" + closeReason);
        super.onChannelClosed(channel, closeReason, appSpecificErrorCode);
    }
}
