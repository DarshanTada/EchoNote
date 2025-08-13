package com.example.mobile;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearMessageListenerService extends WearableListenerService {

    private static final String TAG = "WearMsgListenerService";
    private static final String MESSAGE_PATH = "/recognized_text";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (MESSAGE_PATH.equals(messageEvent.getPath())) {
            String recognizedText = new String(messageEvent.getData());
            Log.d(TAG, "Received from watch: " + recognizedText);

            // Create MOM note
            MOMNote note = new MOMNote(
                    recognizedText,
                    "Speech from watch",
                    "",
                    "",
                    System.currentTimeMillis(),
                    "Voice",
                    false,
                    false
            );

            // Add note to static list
            MOMListActivity.addNote(note);

            // Broadcast update to MOMListActivity
            Intent intent = new Intent("com.example.mobile.MOM_UPDATE");
            intent.putExtra("new_note_text", recognizedText);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            // Show a toast on main thread
            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(),
                            "Note received from watch!",
                            Toast.LENGTH_SHORT).show()
            );

        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
