package sg.edu.np.mad.p04_team4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_TEXT_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_TEXT_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_IMAGE_MESSAGE_SENT = 3;
    private static final int VIEW_TYPE_IMAGE_MESSAGE_RECEIVED = 4;

    private List<Message> messages; // List of messages
    private Context context; // Context of the activity/fragment

    // Constructor for the adapter, initializing the context and message list
    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (message instanceof TextMessage) {
            // Determine if the text message was sent or received
            return message.getUserId().equals(currentUserId) ? VIEW_TYPE_TEXT_MESSAGE_SENT : VIEW_TYPE_TEXT_MESSAGE_RECEIVED;
        } else if (message instanceof ImageMessage) {
            // Determine if the image message was sent or received
            return message.getUserId().equals(currentUserId) ? VIEW_TYPE_IMAGE_MESSAGE_SENT : VIEW_TYPE_IMAGE_MESSAGE_RECEIVED;
        }
        return -1; // Invalid view type
    }

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
            return new ImageMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_IMAGE_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
            return new ImageMessageViewHolder(view);
        }
        return null; // Return null if no valid view type is found
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // Bind the appropriate message to the holder based on its type
        if (holder instanceof TextMessageViewHolder) {
            ((TextMessageViewHolder) holder).bind((TextMessage) messages.get(position), getItemViewType(position));
        } else if (holder instanceof ImageMessageViewHolder) {
            ((ImageMessageViewHolder) holder).bind((ImageMessage) messages.get(position), getItemViewType(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size(); // Return the size of the message list
    }

    // ViewHolder class for text messages
    class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;

        TextMessageViewHolder(View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage); // Initialize the text view
        }

        void bind(TextMessage message, int viewType) {
            textViewMessage.setText(message.getText()); // Set the text message
        }
    }

    // ViewHolder class for image messages
    class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMessage;

        ImageMessageViewHolder(View itemView) {
            super(itemView);
            imageViewMessage = itemView.findViewById(R.id.imageViewMessage); // Initialize the image view
        }

        void bind(ImageMessage message, int viewType) {
            Picasso.get().load(message.getImageUrl()).into(imageViewMessage); // Load the image into the image view using Picasso
        }
    }
}