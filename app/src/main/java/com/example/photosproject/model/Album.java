package com.example.photosproject.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Represents an album containing photos.
 * An album has a name and a list of photos.
 */
public class Album implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private List<Photo> photos;
    
    /**
     * Creates a new Album with the specified name.
     * 
     * @param name the album name
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    /**
     * Gets the album name.
     * 
     * @return the album name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the album name.
     * 
     * @param name the album name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the list of photos in this album.
     * 
     * @return the list of photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }
    
    /**
     * Gets the number of photos in this album.
     * 
     * @return the photo count
     */
    public int getPhotoCount() {
        return photos.size();
    }
    
    /**
     * Adds a photo to this album if it doesn't already exist.
     * 
     * @param photo the photo to add
     * @return true if the photo was added, false if it already exists
     */
    public boolean addPhoto(Photo photo) {
        if (!photos.contains(photo)) {
            photos.add(photo);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a photo from this album.
     * 
     * @param photo the photo to remove
     * @return true if the photo was removed, false if it didn't exist
     */
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }
    
    /**
     * Checks if this album contains a specific photo.
     * 
     * @param photo the photo to check
     * @return true if the album contains the photo, false otherwise
     */
    public boolean containsPhoto(Photo photo) {
        return photos.contains(photo);
    }
    
    /**
     * Gets the earliest date among all photos in this album.
     * 
     * @return the earliest date, or null if the album is empty
     */
    public Calendar getEarliestDate() {
        if (photos.isEmpty()) return null;
        
        Calendar earliest = photos.get(0).getDateTaken();
        for (Photo photo : photos) {
            if (photo.getDateTaken().before(earliest)) {
                earliest = photo.getDateTaken();
            }
        }
        return earliest;
    }
    
    /**
     * Gets the latest date among all photos in this album.
     * 
     * @return the latest date, or null if the album is empty
     */
    public Calendar getLatestDate() {
        if (photos.isEmpty()) return null;
        
        Calendar latest = photos.get(0).getDateTaken();
        for (Photo photo : photos) {
            if (photo.getDateTaken().after(latest)) {
                latest = photo.getDateTaken();
            }
        }
        return latest;
    }
    
    /**
     * Gets the date range of photos in this album as a formatted string.
     * 
     * @return the date range string, or "No photos" if the album is empty
     */
    public String getDateRange() {
        if (photos.isEmpty()) {
            return "No photos";
        }
        
        Calendar earliest = getEarliestDate();
        Calendar latest = getLatestDate();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
        
        if (earliest.equals(latest)) {
            return sdf.format(earliest.getTime());
        } else {
            return sdf.format(earliest.getTime()) + " - " + sdf.format(latest.getTime());
        }
    }
    
    /**
     * Returns a string representation of this album.
     * 
     * @return the album name
     */
    @Override
    public String toString() {
        return name;
    }
}
