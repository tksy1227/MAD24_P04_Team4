package sg.edu.np.mad.p04_team4.Chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // View types for different message types and sender/receiver
    private static final int VIEW_TYPE_TEXT_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_TEXT_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_MESSAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_MESSAGE_RECEIVED = 4;
    private static final int VIEW_TYPE_STICKER_MESSAGE_SENT = 5;
    private static final int VIEW_TYPE_STICKER_MESSAGE_RECEIVED = 6;

    private static final String TAG = "MessageAdapter";

    private List<Message> messages; // List of messages
    private Context context; // Context for accessing resources

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the view type based on message type and sender
        Message message = messages.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (message instanceof TextMessage) {
            return message.getUserId().equals(currentUserId) ? VIEW_TYPE_TEXT_MESSAGE_SENT : VIEW_TYPE_TEXT_MESSAGE_RECEIVED;
        } else if (message instanceof ImageMessage) {
            return message.getUserId().equals(currentUserId) ? VIEW_TYPE_IMAGE_MESSAGE_SENT : VIEW_TYPE_IMAGE_MESSAGE_RECEIVED;
        } else if (message instanceof StickerMessage) {
            return message.getUserId().equals(currentUserId) ? VIEW_TYPE_STICKER_MESSAGE_SENT : VIEW_TYPE_STICKER_MESSAGE_RECEIVED;
        }
        return -1; // Invalid view type
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the appropriate layout based on the view type
        if (viewType == VIEW_TYPE_TEXT_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message_sent, parent, false);
            return new TextMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_TEXT_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message_received, parent, false);
            return new TextMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message_sent, parent, false);
            return new ImageMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message_received, parent, false);
            return new ImageMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_STICKER_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_message_sent, parent, false);
            return new StickerMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_STICKER_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker_message_received, parent, false);
            return new StickerMessageViewHolder(view);
        }
        return null; // Return null if no valid view type is found
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Bind the data to the view holder based on the message type
        if (holder instanceof TextMessageViewHolder) {
            ((TextMessageViewHolder) holder).bind((TextMessage) messages.get(position), getItemViewType(position));
        } else if (holder instanceof ImageMessageViewHolder) {
            ((ImageMessageViewHolder) holder).bind((ImageMessage) messages.get(position), getItemViewType(position));
        } else if (holder instanceof StickerMessageViewHolder) {
            ((StickerMessageViewHolder) holder).bind((StickerMessage) messages.get(position), getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size(); // Return the total number of messages
    }

    // ViewHolder for text messages
    class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        TextMessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }

        void bind(TextMessage message, int viewType) {
            textViewMessage.setText(message.getText());
        }
    }

    // ViewHolder for image messages
    class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMessage;

        ImageMessageViewHolder(View itemView) {
            super(itemView);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage);
        }

        void bind(ImageMessage message, int viewType) {
            Picasso.get().load(message.getImageUrl()).into(imageViewMessage);
        }
    }

    // ViewHolder for sticker messages
    class StickerMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView stickerImageView;

        StickerMessageViewHolder(View itemView) {
            super(itemView);
            stickerImageView = itemView.findViewById(R.id.stickerImageView);
        }

        void bind(StickerMessage message, int viewType) {
            String stickerUrl = message.getStickerUrl();
            Log.d(TAG, "Binding sticker message with URL: " + stickerUrl);

            try {
                Picasso.get().load(stickerUrl).into(stickerImageView);
            } catch (Exception e) {
                Log.e(TAG, "Error loading sticker: " + stickerUrl, e);
            }
        }
    }
}
