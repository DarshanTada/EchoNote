package com.example.mobile;

public class MOMNote {
    private String title;
    private String summary;
    private String actionPoints;
    private String minutes;
    private long timestamp;
    private String tag;
    private boolean isRead;
    private boolean isSummaryPending;

    public MOMNote(String title, String summary, String actionPoints, String minutes, long timestamp, String tag, boolean isRead, boolean isSummaryPending) {
        this.title = title;
        this.summary = summary;
        this.actionPoints = actionPoints;
        this.minutes = minutes;
        this.timestamp = timestamp;
        this.tag = tag;
        this.isRead = isRead;
        this.isSummaryPending = isSummaryPending;
    }

    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getActionPoints() { return actionPoints; }
    public String getMinutes() { return minutes; }
    public long getTimestamp() { return timestamp; }
    public String getTag() { return tag; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { this.isRead = read; }
    public boolean isSummaryPending() { return isSummaryPending; }
    public void setSummaryPending(boolean pending) { this.isSummaryPending = pending; }
}
