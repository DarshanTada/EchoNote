package com.example.shared;

import java.io.Serializable;

public class RecordModel implements Serializable {
    private String title;
    private String filePath;
    private long timestamp;

    public RecordModel(String title, String filePath, long timestamp) {
        this.title = title;
        this.filePath = filePath;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return title + " (" + timestamp + ")";
    }
}
