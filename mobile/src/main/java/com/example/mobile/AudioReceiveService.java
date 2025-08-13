// app/src/main/java/com/example/mobile/AudioReceiveService.java
package com.example.mobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class AudioReceiveService extends WearableListenerService {

    private static final String TAG = "AudioReceiveService";
    private static final String MESSAGE_PATH = "/recognized_text";
    private static final String CHANNEL_ID = "summary_pending_channel";
    public static final String ACTION_MOM_UPDATE = "com.example.mobile.MOM_UPDATE";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived called");
        if (MESSAGE_PATH.equals(messageEvent.getPath())) {
            String recognizedText = new String(messageEvent.getData());
            Log.d(TAG, "Received recognized text: " + recognizedText);

            // 1) Add to list (pending summary)
            MOMListActivity.addNote(new MOMNote(
                    "New Note",              // title
                    "",                      // summary (empty while pending)
                    "",                      // actionPoints
                    recognizedText,          // minutes (raw text)
                    System.currentTimeMillis(),
                    "",                      // tag
                    false,                   // isRead
                    true                     // isSummaryPending
            ));

            // 2) Tell the list screen to refresh if it's visible
            Intent listUpdate = new Intent(ACTION_MOM_UPDATE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(listUpdate);

            // 3) Show a notification that opens EditSummaryActivity when tapped
            showSummaryPendingNotification(recognizedText);

            // 4) (Optional) kick off your background summarize job if you have one
            // GeminiTranscribeAndSummarize.start(AudioReceiveService.this, recognizedText);
        } else {
            super.onMessageReceived(messageEvent);
        }
    }

    private void showSummaryPendingNotification(String recognizedText) {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, "Summary Pending", NotificationManager.IMPORTANCE_DEFAULT)
            );
        }

        Intent intent = new Intent(this, EditSummaryActivity.class);
        intent.putExtra("minutes", recognizedText);
        // unique request code per notification
        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setContentTitle("EchoNote")
                .setContentText("Tap to edit your new note.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        nm.notify(1001, builder.build());
    }
}
