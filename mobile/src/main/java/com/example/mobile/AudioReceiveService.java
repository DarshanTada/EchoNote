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

import com.example.mobile.EditSummary.EditSummaryActivity;
import com.example.mobile.MOMNewList.MOMListActivity;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Service that listens for messages from a Wear OS device.
 * Specifically receives recognized audio text from the watch and adds it as a pending MOM note.
 */
public class AudioReceiveService extends WearableListenerService {

    private static final String TAG = "AudioReceiveService";

    // Path used to filter messages from the wearable
    private static final String MESSAGE_PATH = "/recognized_text";

    // Notification channel ID for pending summary notifications
    private static final String CHANNEL_ID = "summary_pending_channel";

    // Broadcast action to notify the MOM list screen to refresh
    public static final String ACTION_MOM_UPDATE = "com.example.mobile.MOM_UPDATE";

    /**
     * Called when a message is received from the wearable device.
     * @param messageEvent the message data sent from the wearable
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived called");

        // Only handle messages with the specific path
        if (MESSAGE_PATH.equals(messageEvent.getPath())) {

            // Convert the received byte array to String
            String recognizedText = new String(messageEvent.getData());
            Log.d(TAG, "Received recognized text: " + recognizedText);

            // 1) Add a new MOMNote to the list as pending
            MOMListActivity.addNote(new MOMNote(
                    "New Note",              // title placeholder
                    "",                      // summary is empty while pending
                    "",                      // actionPoints placeholder
                    recognizedText,          // minutes: the raw recognized text
                    System.currentTimeMillis(), // timestamp
                    "",                      // tag placeholder
                    false,                   // isRead: not read yet
                    true                     // isSummaryPending: true since user can edit
            ));

            // 2) Broadcast an update to the MOMListActivity so it can refresh the UI
            Intent listUpdate = new Intent(ACTION_MOM_UPDATE);
            LocalBroadcastManager.getInstance(this).sendBroadcast(listUpdate);

            // 3) Show a notification so the user can tap to edit the new note immediately
            showSummaryPendingNotification(recognizedText);

            // 4) Optional: start a background summarize job (commented out)
            // GeminiTranscribeAndSummarize.start(AudioReceiveService.this, recognizedText);

        } else {
            // If the path does not match, call the superclass method
            super.onMessageReceived(messageEvent);
        }
    }

    /**
     * Shows a notification indicating a new pending summary note.
     * Clicking the notification opens EditSummaryActivity with the recognized text prefilled.
     * @param recognizedText the raw text received from the wearable
     */
    private void showSummaryPendingNotification(String recognizedText) {

        // Get the NotificationManager
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android O and above, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            nm.createNotificationChannel(
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Summary Pending",
                            NotificationManager.IMPORTANCE_DEFAULT
                    )
            );
        }

        // Intent to open EditSummaryActivity when notification is tapped
        Intent intent = new Intent(this, EditSummaryActivity.class);
        intent.putExtra("minutes", recognizedText);

        // Create a unique PendingIntent for this notification
        int requestCode = (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_popup_sync) // default sync icon
                .setContentTitle("EchoNote")                   // app name in notification
                .setContentText("Tap to edit your new note.") // notification message
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)              // tap action
                .setAutoCancel(true);                          // dismiss when tapped

        // Show the notification
        nm.notify(1001, builder.build());
    }
}
