package com.example.echonote;

import android.util.Log;

import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

/**
 * Service to listen for messages from a connected Wear OS device.
 * Specifically receives MOMNoteModel data and saves it locally.
 */
public class MOMNoteReceiveService extends WearableListenerService {

    // Tag used for logging
    private static final String TAG = "MOMNoteReceiveService";

    // Path to identify incoming MOM note messages
    private static final String MESSAGE_PATH = "/mom_note_model";

    /**
     * Called when a message is received from the Wearable API
     * @param messageEvent - contains the path and data of the message
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        // Check if the message path matches the expected MOM note path
        if (MESSAGE_PATH.equals(messageEvent.getPath())) {

            // Convert message data (byte[]) to String JSON
            String momJson = new String(messageEvent.getData());
            Log.d(TAG, "Received MOMNoteModel: " + momJson);

            try {
                // Parse JSON into a MOMNoteModel object using Gson
                MOMNoteModel noteModel = new Gson().fromJson(momJson, MOMNoteModel.class);

                // Save the received note locally using SharedPreferences
                SharedPrefUtils.saveNote(this, noteModel);

            } catch (Exception e) {
                // Log error if parsing fails
                Log.e(TAG, "Error parsing MOMNoteModel", e);
            }

        } else {
            // If message path does not match, pass to superclass for default handling
            super.onMessageReceived(messageEvent);
        }
    }
}
