package com.example.shared;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RecordStorageHelper {
    private static final String RECORDS_FILE = "records.dat";

    private static java.io.File getRecordsFile(Context context) {
        java.io.File dir = context.getExternalFilesDir(null);
        java.io.File folder = new java.io.File(dir, "EchoNoteAudio");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return new java.io.File(folder, RECORDS_FILE);
    }

    public static void saveRecord(Context context, RecordModel record) {
        List<RecordModel> records = loadRecords(context);
        records.add(record);
        java.io.File file = getRecordsFile(context);
        Log.d("RecordStorageHelper", "Saving records.dat to: " + file.getAbsolutePath() + ", total records: " + records.size());
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(records);
        } catch (Exception e) {
            Log.e("RecordStorageHelper", "Error saving records.dat: " + e.getMessage(), e);
        }
    }

    public static List<RecordModel> loadRecords(Context context) {
        java.io.File file = getRecordsFile(context);
        Log.d("RecordStorageHelper", "Loading records.dat from: " + file.getAbsolutePath());
        if (!file.exists())  {
            return new ArrayList<>();
        } else {
            Log.d("RecordStorageHelper", "records.dat exists, size: " + file.length() + " bytes");
        }
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            List<RecordModel> loaded = (List<RecordModel>) ois.readObject();
            Log.d("RecordStorageHelper", "Loaded records count: " + loaded.size());
            return loaded;
        } catch (Exception e) {
            Log.e("RecordStorageHelper", "Error loading records.dat: " + e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
