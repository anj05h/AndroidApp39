package com.example.photosproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosproject.model.Album;
import com.example.photosproject.model.DataManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.widget.PopupMenu;

public class MainActivity extends AppCompatActivity implements AlbumAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AlbumAdapter adapter;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dataManager = DataManager.getInstance();
        dataManager.load(this);

        recyclerView = findViewById(R.id.albumsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlbumAdapter(dataManager.getAlbums(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.addAlbumFab);
        fab.setOnClickListener(view -> showCreateAlbumDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh list in case changes happened elsewhere
        adapter.setAlbums(dataManager.getAlbums());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Album album) {
        Intent intent = new Intent(this, AlbumActivity.class);
        intent.putExtra("albumName", album.getName());
        startActivity(intent);
    }

    @Override
    public void onAlbumOptionsClick(Album album, View view) {
        showAlbumPopupMenu(album, view);
    }

    private void showCreateAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Album");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String albumName = input.getText().toString().trim();
            if (albumName.isEmpty()) {
                Toast.makeText(this, "Album name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dataManager.albumExists(albumName)) {
                Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            Album newAlbum = new Album(albumName);
            dataManager.addAlbum(newAlbum);
            dataManager.save(this);
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAlbumPopupMenu(final Album album, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add(Menu.NONE, 0, 0, "Rename");
        popup.getMenu().add(Menu.NONE, 1, 1, "Delete");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    showRenameAlbumDialog(album);
                    return true;
                case 1:
                    showDeleteAlbumConfirmation(album);
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    private void showRenameAlbumDialog(final Album album) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(album.getName());
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newName.equals(album.getName())) {
                return; // No change
            }
            if (dataManager.albumExists(newName)) {
                Toast.makeText(this, "Album with this name already exists", Toast.LENGTH_SHORT).show();
                return;
            }

            album.setName(newName);
            dataManager.save(this);
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteAlbumConfirmation(final Album album) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Album")
                .setMessage("Are you sure you want to delete '" + album.getName() + "'?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dataManager.removeAlbum(album);
                    dataManager.save(this);
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
