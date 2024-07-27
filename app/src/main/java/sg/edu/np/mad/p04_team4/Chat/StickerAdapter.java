package sg.edu.np.mad.p04_team4.Chat;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.StickerViewHolder> {

    private List<Uri> stickerPaths; // List of sticker URIs
    private Context context; // Context for accessing resources and layout inflater
    private OnStickerClickListener onStickerClickListener; // Listener for sticker click events
    private boolean isResourceId; // Flag to indicate if the URIs are resource IDs
    private static final String TAG = "StickerAdapter"; // Tag for logging

    // Interface for handling sticker click events
    public interface OnStickerClickListener {
        void onStickerClick(Uri stickerPath);
    }

    // Constructor to initialize the adapter with sticker paths, context, click listener, and resource ID flag
    public StickerAdapter(List<Uri> stickerPaths, Context context, OnStickerClickListener onStickerClickListener, boolean isResourceId) {
        this.stickerPaths = stickerPaths;
        this.context = context;
        this.onStickerClickListener = onStickerClickListener;
        this.isResourceId = isResourceId;
    }

    // Inflate the item layout and create the ViewHolder
    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sticker, parent, false);
        return new StickerViewHolder(view);
    }

    // Bind the data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        Uri stickerUri = stickerPaths.get(position);
        Log.d(TAG, "Loading sticker: " + stickerUri);

        // Load the sticker image using Picasso
        if (isResourceId) {
            Picasso.get().load(stickerUri).into(holder.stickerImageView);
        } else {
            Picasso.get().load(stickerUri).into(holder.stickerImageView);
        }

        // Set the click listener for the item view
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Sticker clicked: " + stickerUri);  // Log sticker click
            onStickerClickListener.onStickerClick(stickerUri);
        });
    }

    // Return the number of items in the adapter
    @Override
    public int getItemCount() {
        return stickerPaths.size(); // Return the actual number of sticker paths
    }

    // ViewHolder class for holding the sticker image view
    static class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView; // ImageView for displaying the sticker

        // Constructor to initialize the ImageView
        StickerViewHolder(View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
        }
    }
}
