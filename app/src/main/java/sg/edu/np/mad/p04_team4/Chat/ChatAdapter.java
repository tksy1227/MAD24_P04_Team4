package sg.edu.np.mad.p04_team4.Chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import sg.edu.np.mad.p04_team4.R;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private static final String TAG = "ChatAdapter";
    private List<Chat> chatList;
    private List<Chat> chatListFull; // A copy of the full chat list
    private Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference userChatsRef;

    private static final int TYPE_TEXT = 0;
    private static final int TYPE_STICKER = 1;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.chatListFull = new ArrayList<>(chatList); // Make a copy of the full chat list

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("chats");
            loadChats();
        } else {
            Log.e(TAG, "No authenticated user found.");
        }
    }

    private void loadChats() {
        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    Chat chat = chatSnapshot.getValue(Chat.class);
                    if (chat != null) {
                        chat.setKey(chatSnapshot.getKey());
                        chatList.add(chat);
                    }
                }
                chatListFull = new ArrayList<>(chatList);
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load chats", error.toException());
            }
        });
    }

    public void setChatListFull(List<Chat> chatListFull) {
        this.chatListFull = chatListFull;
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = chatList.get(position);
        if (chat.getLastMessage().startsWith("[Sticker:")) {
            return TYPE_STICKER;
        } else {
            return TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TEXT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
            return new TextChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sticker, parent, false);
            return new StickerChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        if (holder.getItemViewType() == TYPE_TEXT) {
            ((TextChatViewHolder) holder).bind(chat);
        } else {
            ((StickerChatViewHolder) holder).bind(chat);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public Filter getFilter() {
        return chatFilter;
    }

    private Filter chatFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Chat> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(chatListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Chat chat : chatListFull) {
                    if (chat.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(chat);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;

            // Log the results of filtering
            Log.d(TAG, "Filtered results: " + filteredList.size());

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chatList.clear();
            if (results.values != null) {
                chatList.addAll((List) results.values);
            }

            // Log the contents of the filtered list
            Log.d(TAG, "Published results: " + chatList.size());

            notifyDataSetChanged();
        }
    };

    public class TextChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, chatMessage, chatTime;
        ImageButton buttonDeleteChat;

        public TextChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chat_name);
            chatMessage = itemView.findViewById(R.id.chat_message);
            chatTime = itemView.findViewById(R.id.chat_time);
            buttonDeleteChat = itemView.findViewById(R.id.button_delete_chat);
        }

        public void bind(Chat chat) {
            chatName.setText(chat.getName());
            chatMessage.setText(chat.getLastMessage());

            try {
                long timeInMillis = Long.parseLong(chat.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Singapore");
                sdf.setTimeZone(localTimeZone);
                String formattedTime = sdf.format(new Date(timeInMillis));
                chatTime.setText(formattedTime);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid time format: " + chat.getTime(), e);
                chatTime.setText("Unknown time"); // Set a default value in case of error
            } catch (NullPointerException e) {
                Log.e(TAG, "Time is null for chat: " + chat.getName(), e);
                chatTime.setText("Unknown time"); // Set a default value in case of null
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Chat_Main.class);
                intent.putExtra("chat_name", chat.getName());
                intent.putExtra("chat_room_id", chat.getKey());
                context.startActivity(intent);
            });

            buttonDeleteChat.setOnClickListener(v -> {
                ((ChatHomeActivity) context).deleteChat(getAdapterPosition());
            });
        }
    }

    public class StickerChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, chatTime;
        ImageView chatSticker;
        ImageButton buttonDeleteChat;

        public StickerChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chat_name);
            chatTime = itemView.findViewById(R.id.chat_time);
            chatSticker = itemView.findViewById(R.id.stickerImageView);
            buttonDeleteChat = itemView.findViewById(R.id.button_delete_chat);
        }

        public void bind(Chat chat) {
            chatName.setText(chat.getName());

            String stickerPath = chat.getLastMessage().substring(9, chat.getLastMessage().length() - 1);
            Bitmap bitmap = BitmapFactory.decodeFile(stickerPath);
            chatSticker.setImageBitmap(bitmap);

            try {
                long timeInMillis = Long.parseLong(chat.getTime());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                TimeZone localTimeZone = TimeZone.getTimeZone("Asia/Singapore");
                sdf.setTimeZone(localTimeZone);
                String formattedTime = sdf.format(new Date(timeInMillis));
                chatTime.setText(formattedTime);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Invalid time format: " + chat.getTime(), e);
                chatTime.setText("Unknown time"); // Set a default value in case of error
            } catch (NullPointerException e) {
                Log.e(TAG, "Time is null for chat: " + chat.getName(), e);
                chatTime.setText("Unknown time"); // Set a default value in case of null
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, Chat_Main.class);
                intent.putExtra("chat_name", chat.getName());
                intent.putExtra("chat_room_id", chat.getKey());
                context.startActivity(intent);
            });

            buttonDeleteChat.setOnClickListener(v -> {
                ((ChatHomeActivity) context).deleteChat(getAdapterPosition());
            });
        }
    }
}
