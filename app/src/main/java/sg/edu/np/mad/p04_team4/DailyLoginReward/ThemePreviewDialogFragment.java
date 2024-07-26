package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.np.mad.p04_team4.R;

public class ThemePreviewDialogFragment extends DialogFragment {

    private String themeTitle;
    private int themeImageResId;
    private int themeCost;
    private String userId;
    private DatabaseReference userCoinsRef;

    public ThemePreviewDialogFragment(String themeTitle, int themeImageResId, int themeCost, String userId) {
        this.themeTitle = themeTitle;
        this.themeImageResId = themeImageResId;
        this.themeCost = themeCost;
        this.userId = userId;
        this.userCoinsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("friendCoins");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_theme_preview, container, false);

        TextView themeTitleView = view.findViewById(R.id.themeTitle);
        themeTitleView.setText(themeTitle);

        ImageView themeImage = view.findViewById(R.id.themePreviewImage);
        themeImage.setImageResource(themeImageResId);

        ImageView closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> dismiss());

        Button buyButton = view.findViewById(R.id.buyButton);
        buyButton.setOnClickListener(v -> showConfirmationDialog());

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(requireActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Make Purchase?")
                .setMessage("Do you want to buy this theme for " + themeCost + " coins?")
                .setPositiveButton("YES", (dialog, which) -> handlePurchase())
                .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void handlePurchase() {
        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentCoins = dataSnapshot.getValue(Integer.class);
                if (currentCoins != null && currentCoins >= themeCost) {
                    int newBalance = currentCoins - themeCost;
                    userCoinsRef.setValue(newBalance)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getActivity(), themeTitle + " purchased!", Toast.LENGTH_SHORT).show();
                                if (getActivity() instanceof ShopActivity) {
                                    ((ShopActivity) getActivity()).updateCoinDisplay(newBalance);
                                }
                                dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), "Purchase failed. Please try again.", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getActivity(), "Not enough coins to purchase " + themeTitle, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to complete purchase. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
