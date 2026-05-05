package com.example.photosproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.photosproject.model.Tag;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    public interface OnTagDeleteListener {
        void onTagDelete(Tag tag);
    }

    private List<Tag> tags;
    private OnTagDeleteListener listener;

    public TagAdapter(List<Tag> tags, OnTagDeleteListener listener) {
        this.tags = tags;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tags.get(position);
        holder.bind(tag, listener);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        private TextView nameText;
        private ImageButton deleteButton;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.tagNameText);
            deleteButton = itemView.findViewById(R.id.deleteTagButton);
        }

        public void bind(final Tag tag, final OnTagDeleteListener listener) {
            nameText.setText(tag.toString());
            deleteButton.setOnClickListener(v -> listener.onTagDelete(tag));
        }
    }
}
