package com.example.mobile;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.wearable.WearableListenerService;

public class AudioReceiveService extends WearableListenerService {
    @Override
    public void onMessageReceived(com.google.android.gms.wearable.MessageEvent messageEvent) {
        Log.d("AudioReceiveService", "onMessageReceived called");
        if ("/recognized_text".equals(messageEvent.getPath())) {
            String recognizedText = new String(messageEvent.getData());
            Log.d("AudioReceiveService", "Received recognized text: " + recognizedText);
            // Show notification
            Log.d("AudioReceiveService", "Calling showSummaryPendingNotification");
            showSummaryPendingNotification();
            Log.d("AudioReceiveService", "Notification should be shown");
            // Add note with pending summary
            MOMListActivity.addNote(new MOMNote(
                "New Note", // title
                "", // summary (empty while pending)
                "", // actionPoints
                recognizedText, // minutes (raw text)
                System.currentTimeMillis(),
                "", // tag
                false, // isRead
                true // isSummaryPending
            ));
            GeminiTranscribeAndSummarize.start(AudioReceiveService.this, recognizedText);
        }
    }

    private void showSummaryPendingNotification() {
        String channelId = "summary_pending_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Summary Pending", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this, MOMListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setContentTitle("EchoNote")
            .setContentText("Summary is being generated for your new note.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);
        notificationManager.notify(1001, builder.build());
    }
}
