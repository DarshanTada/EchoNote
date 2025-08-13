package com.example.echonote;

import android.util.Log;

import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;

public class MOMNoteReceiveService extends WearableListenerService {
    private static final String TAG = "MOMNoteReceiveService";
    private static final String MESSAGE_PATH = "/mom_note_model";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (MESSAGE_PATH.equals(messageEvent.getPath())) {
            String momJson = new String(messageEvent.getData());
            Log.d(TAG, "Received MOMNoteModel: " + momJson);
            try {
                MOMNoteModel noteModel = new Gson().fromJson(momJson, MOMNoteModel.class);
                SharedPrefUtils.saveNote(this, noteModel);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing MOMNoteModel", e);
            }
        } else {
            super.onMessageReceived(messageEvent);
        }
    }
}

