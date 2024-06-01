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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;
    private Context context;

    public ChatAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.chatName.setText(chat.getName());
        holder.chatMessage.setText(chat.getLastMessage());
        holder.chatTime.setText(chat.getTime());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Chat_Main.class);
            intent.putExtra("chat_name", chat.getName());
            context.startActivity(intent);
        });

        holder.buttonDeleteChat.setOnClickListener(v -> {
            ((ChatHomeActivity) context).deleteChat(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatName, chatMessage, chatTime;
        ImageButton buttonDeleteChat;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.chat_name);
            chatMessage = itemView.findViewById(R.id.chat_message);
            chatTime = itemView.findViewById(R.id.chat_time);
            buttonDeleteChat = itemView.findViewById(R.id.button_delete_chat);
        }
    }
}
