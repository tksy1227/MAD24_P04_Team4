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

    private List<Integer> stickerResIds;
    private Context context;
    private OnStickerClickListener onStickerClickListener;

    public interface OnStickerClickListener {
        void onStickerClick(int resId);
    }

    public StickerAdapter(List<Integer> stickerResIds, Context context, OnStickerClickListener onStickerClickListener) {
        this.stickerResIds = stickerResIds;
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
        int resId = stickerResIds.get(position);
        holder.stickerImageView.setImageResource(resId);
        holder.itemView.setOnClickListener(v -> onStickerClickListener.onStickerClick(resId));
    }

    @Override
    public int getItemCount() {
        return stickerResIds.size();
    }

    static class StickerViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        StickerViewHolder(View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
        }
    }
}
