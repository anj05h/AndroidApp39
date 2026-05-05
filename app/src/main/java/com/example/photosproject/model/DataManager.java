package com.example.photosproject.model;

import android.content.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to manage the list of albums and handle persistence.
 */
public class DataManager {
    
    private static final String DATA_FILE = "photos_data.ser";
    private static DataManager instance;
    
    private List<Album> albums;
    
    private DataManager() {
        albums = new ArrayList<>();
    }
    
    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
    
    public List<Album> getAlbums() {
        return albums;
    }
    
    public void addAlbum(Album album) {
        albums.add(album);
    }
    
    public void removeAlbum(Album album) {
        albums.remove(album);
    }
    
    public boolean albumExists(String name) {
        for (Album a : albums) {
            if (a.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Album getAlbumByName(String name) {
        for (Album a : albums) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }
    
    public void save(Context context) {
        try (FileOutputStream fos = context.openFileOutput(DATA_FILE, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(albums);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    public void load(Context context) {
        try (FileInputStream fis = context.openFileInput(DATA_FILE);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            albums = (List<Album>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // If file doesn't exist or error, start with empty list
            albums = new ArrayList<>();
        }
    }
}
