package com.example.photosproject.model;

import java.io.Serializable;

/**
 * Represents a tag with a name and value pair for a photo.
 * A tag is a combination of tag name and tag value (e.g., "location", "New Brunswick").
 */
public class Tag implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String value;
    
    /**
     * Creates a new Tag with the specified name and value.
     * 
     * @param name the tag name (e.g., "location", "person")
     * @param value the tag value (e.g., "New Brunswick", "John")
     */
    public Tag(String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    /**
     * Gets the tag name.
     * 
     * @return the tag name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the tag name.
     * 
     * @param name the tag name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the tag value.
     * 
     * @return the tag value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Sets the tag value.
     * 
     * @param value the tag value
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Checks if this tag equals another object.
     * Two tags are equal if they have the same name and value.
     * 
     * @param obj the object to compare
     * @return true if the tags are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tag tag = (Tag) obj;
        return name.equalsIgnoreCase(tag.name) && value.equalsIgnoreCase(tag.value);
    }
    
    /**
     * Returns a hash code for this tag.
     * 
     * @return hash code based on name and value
     */
    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode() + value.toLowerCase().hashCode();
    }
    
    /**
     * Returns a string representation of this tag.
     * 
     * @return string in format "name: value"
     */
    @Override
    public String toString() {
        return name + ": " + value;
    }
}
