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

    private List<Uri> stickerPaths;
    private Context context;
    private OnStickerClickListener onStickerClickListener;
    private boolean isResourceId;
    private static final String TAG = "StickerAdapter";

    public interface OnStickerClickListener {
        void onStickerClick(Uri stickerPath);
    }

    public StickerAdapter(List<Uri> stickerPaths, Context context, OnStickerClickListener onStickerClickListener, boolean isResourceId) {
        this.stickerPaths = stickerPaths;
        this.context = context;
        this.onStickerClickListener = onStickerClickListener;
        this.isResourceId = isResourceId;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sticker, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        Uri stickerUri = stickerPaths.get(position);
        Log.d(TAG, "Loading sticker: " + stickerUri);

        if (isResourceId) {
            // Handle as URI using Picasso
            Picasso.get().load(stickerUri).into(holder.stickerImageView);
        } else {
            // If it's not a resource ID, it should be handled as a URL
            Picasso.get().load(stickerUri).into(holder.stickerImageView);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Sticker clicked: " + stickerUri);  // Log sticker click
            onStickerClickListener.onStickerClick(stickerUri);
        });
    }

    @Override
    public int getItemCount() {
        return stickerPaths.size(); // Return the actual number of sticker paths
    }

    static class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        StickerViewHolder(View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
        }
    }
}
