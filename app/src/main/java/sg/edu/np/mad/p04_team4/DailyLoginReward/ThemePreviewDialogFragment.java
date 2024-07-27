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
    private DatabaseReference purchasedThemesRef;
    private PurchaseListener purchaseListener;
    private boolean isPurchased;

    public ThemePreviewDialogFragment(String themeTitle, int themeImageResId, int themeCost, String userId) {
        this.themeTitle = themeTitle;
        this.themeImageResId = themeImageResId;
        this.themeCost = themeCost;
        this.userId = userId;
        this.userCoinsRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("friendCoins");
        this.purchasedThemesRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("purchasedThemes");
    }

    public void setPurchaseListener(PurchaseListener purchaseListener) {
        this.purchaseListener = purchaseListener;
    }

    public void setPurchased(boolean isPurchased) {
        this.isPurchased = isPurchased;
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
        if (isPurchased) {
            buyButton.setVisibility(View.GONE);
        } else {
            buyButton.setOnClickListener(v -> showConfirmationDialog());
        }

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
        builder.setTitle(getString(R.string.make_purchase))
                .setMessage(getString(R.string.buy_theme) + themeCost + getString(R.string.buy_sticker2))
                .setPositiveButton(getString(R.string.buy_yes), (dialog, which) -> handlePurchase())
                .setNegativeButton(getString(R.string.buy_no), (dialog, which) -> dialog.dismiss())
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
                                purchasedThemesRef.child(themeTitle).setValue(true)
                                        .addOnSuccessListener(aVoid2 -> {
                                            Toast.makeText(getActivity(), themeTitle + getString(R.string.purchased_theme), Toast.LENGTH_SHORT).show();
                                            if (getActivity() instanceof ShopActivity) {
                                                ((ShopActivity) getActivity()).updateCoinDisplay(newBalance);
                                                ((ShopActivity) getActivity()).applyTheme(themeTitle, themeCost);
                                            }
                                            dismiss();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(getActivity(), getString(R.string.failed_to_save), Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(getActivity(), getString(R.string.purchase_failed), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getActivity(), getString(R.string.not_enough_coins_theme) + themeTitle, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), getString(R.string.failed_to_complete_purchase), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface PurchaseListener {
        void onPurchase();
    }
}
