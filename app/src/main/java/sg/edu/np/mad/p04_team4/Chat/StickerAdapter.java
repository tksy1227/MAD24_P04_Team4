package sg.edu.np.mad.p04_team4.Chat;

import android.content.Context;
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

    private List<String> stickerPaths;
    private Context context;
    private OnStickerClickListener onStickerClickListener;
    private static final int MULTIPLIER = 1000; // Multiplier to create a large list

    public interface OnStickerClickListener {
        void onStickerClick(String stickerPath);
    }

    public StickerAdapter(List<String> stickerPaths, Context context, OnStickerClickListener onStickerClickListener) {
        this.stickerPaths = stickerPaths;
        this.context = context;
        this.onStickerClickListener = onStickerClickListener;
    }

    @NonNull
    @Override
    public StickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sticker, parent, false);
        return new StickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StickerViewHolder holder, int position) {
        int actualPosition = position % stickerPaths.size(); // Calculate actual position in original list
        String stickerPath = stickerPaths.get(actualPosition);
        Picasso.get().load(stickerPath).into(holder.stickerImageView);
        holder.itemView.setOnClickListener(v -> onStickerClickListener.onStickerClick(stickerPath));
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE; // Return a very large number of items to create the illusion of infinite scrolling
    }

    static class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        StickerViewHolder(View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
        }
    }
}
