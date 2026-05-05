package com.example.photosproject.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Represents a photo with file path, caption, date taken, and tags.
 * The date is derived from the file's last modification date.
 */
public class Photo implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String filePath;
    private String caption;
    private Calendar dateTaken;
    private List<Tag> tags;
    
    /**
     * Creates a new Photo with the specified file path.
     * The date is automatically set from the file's last modification date.
     * 
     * @param filePath the absolute path to the photo file
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        this.caption = "";
        this.tags = new ArrayList<>();
        
        // Set date from file's last modification date
        File file = new File(filePath);
        if (file.exists()) {
            this.dateTaken = Calendar.getInstance();
            this.dateTaken.setTimeInMillis(file.lastModified());
            this.dateTaken.set(Calendar.MILLISECOND, 0);
        } else {
            // Fallback if file doesn't exist (shouldn't happen normally)
            this.dateTaken = Calendar.getInstance();
            this.dateTaken.set(Calendar.MILLISECOND, 0);
        }
    }
    
    /**
     * Gets the file path of this photo.
     * 
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }
    
    /**
     * Gets the caption of this photo.
     * 
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }
    
    /**
     * Sets the caption of this photo.
     * 
     * @param caption the caption
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }
    
    /**
     * Gets the date this photo was taken (file modification date).
     * 
     * @return the date taken
     */
    public Calendar getDateTaken() {
        return dateTaken;
    }
    
    /**
     * Gets the list of tags for this photo.
     * 
     * @return the list of tags
     */
    public List<Tag> getTags() {
        return tags;
    }
    
    /**
     * Adds a tag to this photo if it doesn't already exist.
     * 
     * @param tag the tag to add
     * @return true if the tag was added, false if it already exists
     */
    public boolean addTag(Tag tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a tag from this photo.
     * 
     * @param tag the tag to remove
     * @return true if the tag was removed, false if it didn't exist
     */
    public boolean removeTag(Tag tag) {
        return tags.remove(tag);
    }
    
    /**
     * Checks if this photo has a specific tag.
     * 
     * @param tag the tag to check
     * @return true if the photo has the tag, false otherwise
     */
    public boolean hasTag(Tag tag) {
        return tags.contains(tag);
    }
    
    /**
     * Checks if this photo equals another object.
     * Two photos are equal if they have the same file path.
     * 
     * @param obj the object to compare
     * @return true if the photos are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Photo photo = (Photo) obj;
        return filePath.equals(photo.filePath);
    }
    
    /**
     * Returns a hash code for this photo.
     * 
     * @return hash code based on file path
     */
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
    
    /**
     * Returns a string representation of this photo.
     * 
     * @return the caption or file name if no caption
     */
    @Override
    public String toString() {
        if (caption != null && !caption.isEmpty()) {
            return caption;
        }
        return new File(filePath).getName();
    }
}
