package com.example.shared;

import java.io.Serializable;

public class MOM implements Serializable {
    private String title;
    private String date;
    private String tag;
    private boolean read;
    private String audioFilePath;
    private String transcript;

    public MOM(String title, String date, String tag, boolean read) {
        this.title = title;
        this.date = date;
        this.tag = tag;
        this.read = read;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }
}
