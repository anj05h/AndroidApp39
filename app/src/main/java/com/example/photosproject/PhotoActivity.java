package com.example.photosproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosproject.model.Album;
import com.example.photosproject.model.DataManager;
import com.example.photosproject.model.Photo;
import com.example.photosproject.model.Tag;
import java.io.File;
import java.util.List;

public class PhotoActivity extends AppCompatActivity implements TagAdapter.OnTagDeleteListener {

    private ImageView fullImageView;
    private RecyclerView tagsRecyclerView;
    private TagAdapter tagAdapter;
    private DataManager dataManager;
    private Album currentAlbum;
    private Photo currentPhoto;
    private int currentPhotoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        String albumName = getIntent().getStringExtra("albumName");
        String photoPath = getIntent().getStringExtra("photoPath");

        if (albumName == null || photoPath == null) {
            finish();
            return;
        }

        dataManager = DataManager.getInstance();
        currentAlbum = dataManager.getAlbumByName(albumName);
        if (currentAlbum == null) {
            finish();
            return;
        }

        // Find photo index
        List<Photo> photos = currentAlbum.getPhotos();
        currentPhotoIndex = -1;
        for (int i = 0; i < photos.size(); i++) {
            if (photos.get(i).getFilePath().equals(photoPath)) {
                currentPhotoIndex = i;
                break;
            }
        }

        if (currentPhotoIndex == -1) {
            finish();
            return;
        }
        currentPhoto = photos.get(currentPhotoIndex);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Photo");

        fullImageView = findViewById(R.id.fullImageView);
        tagsRecyclerView = findViewById(R.id.tagsRecyclerView);
        tagsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tagAdapter = new TagAdapter(currentPhoto.getTags(), this);
        tagsRecyclerView.setAdapter(tagAdapter);

        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button addTagButton = findViewById(R.id.addTagButton);

        prevButton.setOnClickListener(v -> showPreviousPhoto());
        nextButton.setOnClickListener(v -> showNextPhoto());
        addTagButton.setOnClickListener(v -> showAddTagDialog());

        displayPhoto();
    }

    private void displayPhoto() {
        File imgFile = new File(currentPhoto.getFilePath());
        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            fullImageView.setImageBitmap(bitmap);
        }
        tagAdapter.setTags(currentPhoto.getTags());
    }

    private void showPreviousPhoto() {
        if (currentPhotoIndex > 0) {
            currentPhotoIndex--;
            currentPhoto = currentAlbum.getPhotos().get(currentPhotoIndex);
            displayPhoto();
        } else {
            Toast.makeText(this, "First photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNextPhoto() {
        if (currentPhotoIndex < currentAlbum.getPhotos().size() - 1) {
            currentPhotoIndex++;
            currentPhoto = currentAlbum.getPhotos().get(currentPhotoIndex);
            displayPhoto();
        } else {
            Toast.makeText(this, "Last photo", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Tag");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        final Spinner typeSpinner = new Spinner(this);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"person", "location"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);
        layout.addView(typeSpinner);

        final EditText valueInput = new EditText(this);
        valueInput.setHint("Value");
        layout.addView(valueInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String type = typeSpinner.getSelectedItem().toString();
            String value = valueInput.getText().toString().trim();

            if (value.isEmpty()) {
                Toast.makeText(this, "Value cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Tag newTag = new Tag(type, value);
            if (currentPhoto.addTag(newTag)) {
                dataManager.save(this);
                tagAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Tag already exists", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onTagDelete(Tag tag) {
        currentPhoto.removeTag(tag);
        dataManager.save(this);
        tagAdapter.notifyDataSetChanged();
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
