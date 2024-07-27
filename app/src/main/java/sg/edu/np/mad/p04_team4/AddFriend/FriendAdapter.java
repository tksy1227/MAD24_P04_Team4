package sg.edu.np.mad.p04_team4.AddFriend;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private final List<Friend> friendList;

    public FriendAdapter(List<Friend> friendList) {
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.nameTextView.setText(friend.getName());
        holder.birthdayTextView.setText(friend.getBirthday());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView birthdayTextView;

        FriendViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.friendName);
            birthdayTextView = itemView.findViewById(R.id.friendBirthday);
        }
    }
}
