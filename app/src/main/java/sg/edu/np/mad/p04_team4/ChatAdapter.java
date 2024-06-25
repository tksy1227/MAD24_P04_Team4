package sg.edu.np.mad.p04_team4;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList; // List of chats to display
    private Context context; // Context of the activity/fragment

    // Constructor for the adapter, initializing the context and chat list
    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each chat item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Get the chat object at the current position
        Chat chat = chatList.get(position);
        // Set the chat details to the respective views
        holder.chatName.setText(chat.getName());
        holder.chatMessage.setText(chat.getLastMessage());
        // Convert the time to a readable format
        long timeInMillis = Long.parseLong(chat.getTime());
        String formattedTime = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(new Date(timeInMillis));
        holder.chatTime.setText(formattedTime);

        // Set click listener for the chat item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat_Main.class);
            intent.putExtra("chat_name", chat.getName()); // Pass chat name to the Chat_Main activity
            intent.putExtra("chat_room_id", chat.getKey()); // Pass the chat room ID
            context.startActivity(intent); // Start the Chat_Main activity
        });

        // Set click listener for the delete button
        holder.buttonDeleteChat.setOnClickListener(v -> {
            ((ChatHomeActivity) context).deleteChat(holder.getAdapterPosition()); // Call deleteChat method in ChatHomeActivity
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size(); // Return the size of the chat list
    }

    // ViewHolder class to hold the views for each chat item
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, chatMessage, chatTime;
        ImageButton buttonDeleteChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            chatName = itemView.findViewById(R.id.chat_name);
            chatMessage = itemView.findViewById(R.id.chat_message);
            chatTime = itemView.findViewById(R.id.chat_time);
            buttonDeleteChat = itemView.findViewById(R.id.button_delete_chat);
        }
    }
}

