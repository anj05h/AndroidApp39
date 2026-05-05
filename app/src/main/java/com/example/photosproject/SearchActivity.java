package com.example.photosproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosproject.model.Album;
import com.example.photosproject.model.DataManager;
import com.example.photosproject.model.Photo;
import com.example.photosproject.model.Tag;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity implements PhotoAdapter.OnItemClickListener {

    private AutoCompleteTextView personInput;
    private AutoCompleteTextView locationInput;
    private RadioButton andRadio;
    private RecyclerView resultsRecyclerView;
    private PhotoAdapter adapter;
    private DataManager dataManager;
    private List<Photo> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataManager = DataManager.getInstance();
        searchResults = new ArrayList<>();

        personInput = findViewById(R.id.personInput);
        locationInput = findViewById(R.id.locationInput);
        andRadio = findViewById(R.id.andRadio);
        Button searchButton = findViewById(R.id.searchButton);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);

        resultsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PhotoAdapter(searchResults, this);
        resultsRecyclerView.setAdapter(adapter);

        setupAutoComplete();

        searchButton.setOnClickListener(v -> performSearch());
    }

    private void setupAutoComplete() {
        Set<String> locations = new HashSet<>();
        Set<String> people = new HashSet<>();
        
        for (Album album : dataManager.getAlbums()) {
            for (Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    if (tag.getName().equalsIgnoreCase("location")) {
                        locations.add(tag.getValue());
                    } else if (tag.getName().equalsIgnoreCase("person")) {
                        people.add(tag.getValue());
                    }
                }
            }
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(locations));
        locationInput.setAdapter(locationAdapter);

        ArrayAdapter<String> personAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(people));
        personInput.setAdapter(personAdapter);
    }

    private void performSearch() {
        String personQuery = personInput.getText().toString().trim().toLowerCase();
        String locationQuery = locationInput.getText().toString().trim().toLowerCase();
        boolean isAnd = andRadio.isChecked();

        if (personQuery.isEmpty() && locationQuery.isEmpty()) {
            Toast.makeText(this, "Please enter at least one tag", Toast.LENGTH_SHORT).show();
            return;
        }

        searchResults.clear();
        List<Album> albums = dataManager.getAlbums();

        for (Album album : albums) {
            for (Photo photo : album.getPhotos()) {
                boolean personMatch = false;
                boolean locationMatch = false;

                // Check person match
                if (!personQuery.isEmpty()) {
                    for (Tag tag : photo.getTags()) {
                        if (tag.getName().equalsIgnoreCase("person") &&
                                tag.getValue().toLowerCase().startsWith(personQuery)) { // "matches should now allow auto completion" - strictly speaking this usually applies to input, but "matches should include photos taken in New York... if New is typed" implies prefix match for search too?
                                // Prompt says: "when searching by location, if "New" is typed, matches should include photos taken in New York... The user can then pick from this auto-completed list."
                                // This implies the auto-complete helps pick the FULL value.
                                // But "matches should now allow auto completion... In other words, all locations that start with the typed text."
                                // If the user picks from list, the input becomes "New York".
                                // But if they just type "New" and hit search?
                                // "Matches are case insensitive... new york is the same as nEw YOrk".
                                // I'll assume exact match (case-insensitive) if they pick from list, or prefix match if they type partial?
                                // The prompt says "The user can then pick from this auto-completed list."
                                // It implies the search is based on the value in the box.
                                // If I type "New", and don't pick, should it find "New York"?
                                // "In other words, all locations that start with the typed text." -> This sounds like prefix match logic for the SEARCH itself.
                                // I will implement prefix match for both to be safe and more powerful.
                            personMatch = true;
                            break;
                        }
                    }
                }

                // Check location match
                if (!locationQuery.isEmpty()) {
                    for (Tag tag : photo.getTags()) {
                        if (tag.getName().equalsIgnoreCase("location") &&
                                tag.getValue().toLowerCase().startsWith(locationQuery)) {
                            locationMatch = true;
                            break;
                        }
                    }
                }

                boolean match = false;
                if (!personQuery.isEmpty() && !locationQuery.isEmpty()) {
                    if (isAnd) {
                        match = personMatch && locationMatch;
                    } else {
                        match = personMatch || locationMatch;
                    }
                } else if (!personQuery.isEmpty()) {
                    match = personMatch;
                } else if (!locationQuery.isEmpty()) {
                    match = locationMatch;
                }

                if (match) {
                    // Avoid duplicates if photo is in multiple albums?
                    // "Since there is no functionality to copy photos from one album to another, you can treat photos in different albums as independent"
                    // So just add it.
                    searchResults.add(photo);
                }
            }
        }

        adapter.notifyDataSetChanged();
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No photos found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(Photo photo) {
        // Find which album this photo belongs to
        String albumName = null;
        for (Album album : dataManager.getAlbums()) {
            if (album.getPhotos().contains(photo)) {
                albumName = album.getName();
                break;
            }
        }

        if (albumName != null) {
            Intent intent = new Intent(this, PhotoActivity.class);
            intent.putExtra("albumName", albumName);
            intent.putExtra("photoPath", photo.getFilePath());
            startActivity(intent);
        }
    }

    @Override
    public void onPhotoOptionsClick(Photo photo, View view) {
        // No options in search results
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
