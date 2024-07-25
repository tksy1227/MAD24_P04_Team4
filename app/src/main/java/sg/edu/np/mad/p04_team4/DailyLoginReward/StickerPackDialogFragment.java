package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.p04_team4.Chat.ChatDetailActivity;
import sg.edu.np.mad.p04_team4.Chat.StickerAdapter;
import sg.edu.np.mad.p04_team4.R;

public class StickerPackDialogFragment extends DialogFragment {

    private static final String TAG = "StickerPackDialog";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private RecyclerView stickerRecyclerView;
    private StickerAdapter stickerAdapter;
    private List<String> stickerPaths;
    private Button btnBuyStickerPack;
    private TextView coinAmountTextView;
    private LinearLayoutManager layoutManager;

    private String packName;
    private String userId;
    private String chatRoomId;
    private int packCost;
    private int currentCoins;

    public StickerPackDialogFragment(String packName, String userId, String chatRoomId, int packCost) {
        this.packName = packName;
        this.userId = userId;
        this.chatRoomId = chatRoomId;
        this.packCost = packCost;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sticker_pack, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        stickerRecyclerView = view.findViewById(R.id.stickerRecyclerView);
        TextView packNameTextView = view.findViewById(R.id.stickerPackTitle);
        ImageView backArrow = view.findViewById(R.id.backArrow);
        btnBuyStickerPack = view.findViewById(R.id.btnBuyStickerPack);
        coinAmountTextView = getActivity().findViewById(R.id.coinAmount);

        packNameTextView.setText(packName);

        backArrow.setOnClickListener(v -> dismiss());

        stickerPaths = new ArrayList<>();
        stickerAdapter = new StickerAdapter(stickerPaths, getContext(), url -> {
            sendStickerMessage(url);
            dismiss();
        });

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        stickerRecyclerView.setLayoutManager(layoutManager);
        stickerRecyclerView.setAdapter(stickerAdapter);

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

        fetchStickersFromFirebase();

        btnBuyStickerPack.setOnClickListener(v -> showPurchaseConfirmationDialog());

        return view;
    }

    private void fetchStickersFromFirebase() {
        DatabaseReference stickersRef = mDatabase.child("stickers").child(packName);
        stickersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stickerPaths.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String stickerUrl = snapshot.getValue(String.class);
                    stickerPaths.add(stickerUrl);
                }
                if (stickerPaths.isEmpty()) {
                    loadLocalStickers();
                } else {
                    stickerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to fetch stickers", databaseError.toException());
                loadLocalStickers();
            }
        });
    }

    private void loadLocalStickers() {
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
    }

    private void showPurchaseConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Make Purchase?")
                .setMessage("Do you want to buy this sticker pack for " + packCost + " coins?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        buyStickerPack();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void buyStickerPack() {
        if (currentCoins >= packCost) {
            currentCoins -= packCost;
            DatabaseReference userCoinsRef = mDatabase.child("users").child(userId).child("friendCoins");
            userCoinsRef.setValue(currentCoins).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    savePurchasedStickerPack();
                    coinAmountTextView.setText(String.valueOf(currentCoins));
                    Toast.makeText(getContext(), "Sticker Pack Purchased!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Failed to update coins", task.getException());
                    Toast.makeText(getContext(), "Purchase failed, please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Not enough coins to buy this sticker pack", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePurchasedStickerPack() {
        DatabaseReference userPacksRef = mDatabase.child("users").child(userId).child("purchasedStickerPacks").child(packName);
        Map<String, Object> stickerPack = new HashMap<>();
        for (String stickerPath : stickerPaths) {
            stickerPack.put(stickerPath, true);  // Store sticker paths with a boolean to indicate ownership
        }
        userPacksRef.setValue(stickerPack).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Sticker pack saved to user's purchased packs");
            } else {
                Log.e(TAG, "Failed to save sticker pack", task.getException());
            }
        });
    }

    private void sendStickerMessage(String stickerPath) {
        ChatDetailActivity chatDetailActivity = (ChatDetailActivity) getActivity();
        if (chatDetailActivity != null) {
            chatDetailActivity.sendStickerMessage(stickerPath);
        }
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
}
