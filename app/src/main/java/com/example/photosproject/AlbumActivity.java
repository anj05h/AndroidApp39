package com.example.photosproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosproject.model.Album;
import com.example.photosproject.model.DataManager;
import com.example.photosproject.model.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.widget.PopupMenu;

public class AlbumActivity extends AppCompatActivity implements PhotoAdapter.OnItemClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;
    
    private RecyclerView recyclerView;
    private PhotoAdapter adapter;
    private DataManager dataManager;
    private Album currentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        String albumName = getIntent().getStringExtra("albumName");
        if (albumName == null) {
            finish();
            return;
        }

        dataManager = DataManager.getInstance();
        currentAlbum = dataManager.getAlbumByName(albumName);
        if (currentAlbum == null) {
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentAlbum.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.photosRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        adapter = new PhotoAdapter(currentAlbum.getPhotos(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addPhotoFab);
        fab.setOnClickListener(view -> openFileChooser());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setPhotos(currentAlbum.getPhotos());
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_import_examples) {
            importExamplePhotos();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFileChooser() {
        // Use ACTION_OPEN_DOCUMENT to open the system file picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void importExamplePhotos() {
        try {
            String[] files = getAssets().list("");
            int count = 0;
            for (String filename : files) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")) {
                    File destFile = new File(getFilesDir(), filename);
                    if (!destFile.exists()) {
                        try (InputStream is = getAssets().open(filename);
                             FileOutputStream fos = new FileOutputStream(destFile)) {
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = is.read(buffer)) > 0) {
                                fos.write(buffer, 0, length);
                            }
                        }
                        
                        Photo newPhoto = new Photo(destFile.getAbsolutePath());
                        if (currentAlbum.addPhoto(newPhoto)) {
                            count++;
                        }
                    } else {
                        // File exists, just add reference if not in album
                        Photo newPhoto = new Photo(destFile.getAbsolutePath());
                        if (currentAlbum.addPhoto(newPhoto)) {
                            count++;
                        }
                    }
                }
            }
            
            if (count > 0) {
                dataManager.save(this);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Imported " + count + " photos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No new photos to import", Toast.LENGTH_SHORT).show();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error importing photos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            addPhotoToAlbum(imageUri);
        }
    }

    private void addPhotoToAlbum(Uri imageUri) {
        try {
            // Copy file to internal storage to ensure we have a valid file path and persistence
            String fileName = getFileName(imageUri);
            File destFile = new File(getFilesDir(), fileName);
            
            // Handle duplicate names by appending timestamp
            if (destFile.exists()) {
                String name = fileName.substring(0, fileName.lastIndexOf('.'));
                String ext = fileName.substring(fileName.lastIndexOf('.'));
                destFile = new File(getFilesDir(), name + "_" + System.currentTimeMillis() + ext);
            }

            try (InputStream is = getContentResolver().openInputStream(imageUri);
                 FileOutputStream fos = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            }

            Photo newPhoto = new Photo(destFile.getAbsolutePath());
            if (currentAlbum.addPhoto(newPhoto)) {
                dataManager.save(this);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Photo already in album", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error adding photo", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onItemClick(Photo photo) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("albumName", currentAlbum.getName());
        intent.putExtra("photoPath", photo.getFilePath());
        startActivity(intent);
    }

    @Override
    public void onPhotoOptionsClick(Photo photo, android.view.View view) {
        showPhotoPopupMenu(photo, view);
    }

    private void showPhotoPopupMenu(final Photo photo, android.view.View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add(android.view.Menu.NONE, 0, 0, "Move to Album");
        popup.getMenu().add(android.view.Menu.NONE, 1, 1, "Remove");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    showMovePhotoDialog(photo);
                    return true;
                case 1:
                    showRemovePhotoConfirmation(photo);
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    private void showMovePhotoDialog(final Photo photo) {
        List<Album> allAlbums = dataManager.getAlbums();
        List<String> albumNames = new ArrayList<>();
        final List<Album> targetAlbums = new ArrayList<>();

        for (Album a : allAlbums) {
            if (!a.getName().equals(currentAlbum.getName())) {
                albumNames.add(a.getName());
                targetAlbums.add(a);
            }
        }

        if (albumNames.isEmpty()) {
            Toast.makeText(this, "No other albums to move to", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Move to Album");
        builder.setItems(albumNames.toArray(new CharSequence[0]), (dialog, which) -> {
            Album targetAlbum = targetAlbums.get(which);
            
            // Add to target
            if (targetAlbum.addPhoto(photo)) {
                // Remove from current
                currentAlbum.removePhoto(photo);
                dataManager.save(this);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Moved to " + targetAlbum.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Photo already exists in " + targetAlbum.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void showRemovePhotoConfirmation(final Photo photo) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Photo")
                .setMessage("Remove this photo from the album?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    currentAlbum.removePhoto(photo);
                    dataManager.save(this);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
