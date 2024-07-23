package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import sg.edu.np.mad.p04_team4.Chat.Message;
import sg.edu.np.mad.p04_team4.Chat.StickerAdapter;
import sg.edu.np.mad.p04_team4.Chat.StickerMessage;
import sg.edu.np.mad.p04_team4.R;

public class StickerPackDialogFragment extends DialogFragment {

    private static final String TAG = "StickerPackDialog";
    private FirebaseAuth mAuth;
    private RecyclerView stickerRecyclerView;
    private StickerAdapter stickerAdapter;
    private List<String> stickerPaths;

    private String packName;
    private String chatRoomId;
    private LinearLayoutManager layoutManager;

    public StickerPackDialogFragment(String packName, String chatRoomId) {
        this.packName = packName;
        this.chatRoomId = chatRoomId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sticker_pack, container, false);

        mAuth = FirebaseAuth.getInstance();

        stickerRecyclerView = view.findViewById(R.id.stickerRecyclerView);
        TextView packNameTextView = view.findViewById(R.id.stickerPackTitle);
        ImageView backArrow = view.findViewById(R.id.backArrow);

        packNameTextView.setText(packName);

        backArrow.setOnClickListener(v -> dismiss());

        stickerPaths = new ArrayList<>();
        stickerAdapter = new StickerAdapter(stickerPaths, getContext(), url -> sendStickerMessage(url));

        // Set up LinearLayoutManager for smooth scrolling
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        stickerRecyclerView.setLayoutManager(layoutManager);
        stickerRecyclerView.setAdapter(stickerAdapter);

        // Start the RecyclerView in the middle
        stickerRecyclerView.scrollToPosition(Integer.MAX_VALUE / 2);

        stickerRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                    if (firstVisibleItemPosition <= 1) {
                        stickerRecyclerView.scrollToPosition(stickerAdapter.getItemCount() / 2);
                    } else if (lastVisibleItemPosition >= stickerAdapter.getItemCount() - 2) {
                        stickerRecyclerView.scrollToPosition(stickerAdapter.getItemCount() / 2);
                    }
                }
            }
        });

        switch (packName) {
            case "Cat Sticker Pack":
                loadCatStickers();
                break;
            case "Dead by Daylight Sticker Pack":
                loadDeadByDaylightStickers();
                break;
            case "Alpha Wolf Sticker Pack":
                loadAlphaWolfStickers();
                break;
            case "Monkey Sticker Pack":
                loadMonkeyStickers();
                break;
            case "Skibidi Toilet Sticker Pack":
                loadSkibidiStickers();
                break;
            // Add more packs here if needed
        }

        return view;
    }

    private void loadCatStickers() {
        stickerPaths.add(getResourceUri(R.drawable.cat_angry).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_bored).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_crying).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_facepalm).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_happy).toString());
        stickerPaths.add(getResourceUri(R.drawable.cat_sleep).toString());
        stickerAdapter.notifyDataSetChanged();
    }

    private void loadDeadByDaylightStickers() {
        stickerPaths.add(getResourceUri(R.drawable.claudette).toString());
        stickerPaths.add(getResourceUri(R.drawable.dwight).toString());
        stickerPaths.add(getResourceUri(R.drawable.ghostface).toString());
        stickerPaths.add(getResourceUri(R.drawable.legion).toString());
        stickerPaths.add(getResourceUri(R.drawable.sadako).toString());
        stickerAdapter.notifyDataSetChanged();
    }

    private void loadAlphaWolfStickers() {
        stickerPaths.add(getResourceUri(R.drawable.wolf_grin).toString());
        stickerPaths.add(getResourceUri(R.drawable.wolf_growl).toString());
        stickerPaths.add(getResourceUri(R.drawable.wolf_howl).toString());
        stickerPaths.add(getResourceUri(R.drawable.wolf_pack).toString());
        stickerAdapter.notifyDataSetChanged();
    }

    private void loadMonkeyStickers() {
        stickerPaths.add(getResourceUri(R.drawable.monkey_angry).toString());
        stickerPaths.add(getResourceUri(R.drawable.monkey_fp).toString());
        stickerPaths.add(getResourceUri(R.drawable.monkey_happy).toString());
        stickerPaths.add(getResourceUri(R.drawable.monkey_no).toString());
        stickerPaths.add(getResourceUri(R.drawable.monkey_yes).toString());
        stickerPaths.add(getResourceUri(R.drawable.monkey_sad).toString());
        stickerAdapter.notifyDataSetChanged();
    }

    private void loadSkibidiStickers() {
        stickerPaths.add(getResourceUri(R.drawable.skibidi_angry).toString());
        stickerPaths.add(getResourceUri(R.drawable.skibidi_confused).toString());
        stickerPaths.add(getResourceUri(R.drawable.skibidi_happy).toString());
        stickerPaths.add(getResourceUri(R.drawable.skibidi_mindblown).toString());
        stickerPaths.add(getResourceUri(R.drawable.skibidi_sad).toString());
        stickerAdapter.notifyDataSetChanged();
    }

    private Uri getResourceUri(int resourceId) {
        return Uri.parse("android.resource://" + getContext().getPackageName() + "/" + resourceId);
    }

    private void sendStickerMessage(String stickerPath) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            long currentTime = System.currentTimeMillis();
            Message message = new StickerMessage(currentTime, currentTime, stickerPath, userId);
            DatabaseReference userChatsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("chats").child(chatRoomId).child("messages");
            userChatsRef.push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Sticker message sent successfully");
                } else {
                    Log.e(TAG, "Failed to send sticker message", task.getException());
                }
            });
        } else {
            Log.e(TAG, "No user is currently signed in");
        }
    }
}
