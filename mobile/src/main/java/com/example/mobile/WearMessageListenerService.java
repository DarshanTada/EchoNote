package com.example.mobile;

import android.util.Log;
import android.widget.Toast;

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

            // Add note to MOM list
            MOMNote note = new MOMNote(
                    recognizedText,
                    "Speech from watch",
                    "",
                    "",
                    System.currentTimeMillis(),
                    "Voice",
                    false,
                    false // isSummaryPending
            );
            MOMListActivity.addNote(note);

            // Optional toast (runs in background thread so use handler)
            new android.os.Handler(getMainLooper()).post(() ->
                    Toast.makeText(getApplicationContext(), "Note received from watch!", Toast.LENGTH_SHORT).show()
            );
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}
