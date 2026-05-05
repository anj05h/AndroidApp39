package com.example.photosproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosproject.model.Album;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Album album);
        void onAlbumOptionsClick(Album album, View view);
    }

    private List<Album> albums;
    private OnItemClickListener listener;

    public AlbumAdapter(List<Album> albums, OnItemClickListener listener) {
        this.albums = albums;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albums.get(position);
        holder.bind(album, listener);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
        notifyDataSetChanged();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private TextView infoText;
        private View moreButton;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.albumNameText);
            infoText = itemView.findViewById(R.id.albumInfoText);
            moreButton = itemView.findViewById(R.id.albumMoreButton);
        }

        public void bind(final Album album, final OnItemClickListener listener) {
            nameText.setText(album.getName());
            
            String info = album.getPhotoCount() + " photos";
            if (album.getPhotoCount() > 0) {
                info += " (" + album.getDateRange() + ")";
            }
            infoText.setText(info);

            itemView.setOnClickListener(v -> listener.onItemClick(album));
            moreButton.setOnClickListener(v -> listener.onAlbumOptionsClick(album, v));
        }
    }
}
