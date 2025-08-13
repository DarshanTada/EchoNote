package com.example.mobile;

/**
 * Model class representing a single MOM (Minutes of Meeting) note.
 * Contains information about the note's title, summary, action points,
 * raw minutes text, timestamp, tag, read status, and whether the summary is pending.
 */
public class MOMNote {

    // Title of the note
    private String title;

    // Summary of the note (can be empty if pending)
    private String summary;

    // Action points derived from the meeting (optional)
    private String actionPoints;

    // Raw text of the meeting minutes
    private String minutes;

    // Timestamp when the note was created (milliseconds since epoch)
    private long timestamp;

    // Tag/category for the note (optional)
    private String tag;

    // Flag indicating whether the note has been read by the user
    private boolean isRead;

    // Flag indicating whether the note summary is pending and needs user attention
    private boolean isSummaryPending;

    /**
     * Constructor to create a new MOMNote instance.
     *
     * @param title            Title of the note
     * @param summary          Summary text
     * @param actionPoints     Action points from the note
     * @param minutes          Raw minutes text
     * @param timestamp        Time when the note was created
     * @param tag              Tag/category for the note
     * @param isRead           Whether the note has been read
     * @param isSummaryPending Whether the note summary is pending
     */
    public MOMNote(String title, String summary, String actionPoints, String minutes,
                   long timestamp, String tag, boolean isRead, boolean isSummaryPending) {
        this.title = title;
        this.summary = summary;
        this.actionPoints = actionPoints;
        this.minutes = minutes;
        this.timestamp = timestamp;
        this.tag = tag;
        this.isRead = isRead;
        this.isSummaryPending = isSummaryPending;
    }

    // Getters for each field

    /** @return the note's title */
    public String getTitle() { return title; }

    /** @return the note's summary */
    public String getSummary() { return summary; }

    /** @return the note's action points */
    public String getActionPoints() { return actionPoints; }

    /** @return the note's raw minutes text */
    public String getMinutes() { return minutes; }

    /** @return the note's timestamp in milliseconds */
    public long getTimestamp() { return timestamp; }

    /** @return the note's tag */
    public String getTag() { return tag; }

    /** @return true if the note has been read */
    public boolean isRead() { return isRead; }

    /** Set the read status of the note */
    public void setRead(boolean read) { this.isRead = read; }

    /** @return true if the note summary is pending */
    public boolean isSummaryPending() { return isSummaryPending; }

    /** Set the summary pending status of the note */
    public void setSummaryPending(boolean pending) { this.isSummaryPending = pending; }
}
