package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import sg.edu.np.mad.p04_team4.R;

public class DailyRewardDialogFragment extends DialogFragment {

    private LinearLayout daysLayout;
    private Button claimButton;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "DailyRewardPrefs";
    private static final int REWARD_AMOUNT = 20;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    private static final String TAG = "DailyRewardDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_daily_reward, container, false);

        daysLayout = view.findViewById(R.id.daysLayout);
        claimButton = view.findViewById(R.id.claimButton);
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "User ID: " + userId);
            updateLoginStreak();
            setupDaysGrid(view);
            setupClaimButton(view);
            checkClaimStatus(view);
        } else {
            // Handle the case where the user is not logged in
            Toast.makeText(getActivity(), "Please log in to claim rewards", Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return view;
    }

    private void updateLoginStreak() {
        Calendar today = Calendar.getInstance();
        long lastLoginMillis = prefs.getLong(userId + "_LastLoginDate", 0);
        Calendar lastLogin = Calendar.getInstance();
        lastLogin.setTimeInMillis(lastLoginMillis);

        int currentStreak = prefs.getInt(userId + "_CurrentStreak", 0);
        Log.d(TAG, "Last Login: " + lastLoginMillis + ", Current Streak: " + currentStreak);

        // Check if today is the same as the last login day
        if (isSameDay(lastLogin, today)) {
            Log.d(TAG, "Same day, not resetting claim status.");
        } else {
            Log.d(TAG, "New day detected.");

            // Calculate the difference in days between today and the last login date
            long diffInMillis = today.getTimeInMillis() - lastLogin.getTimeInMillis();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (lastLoginMillis == 0 || diffInDays > 1) {
                // Reset streak to 1 if it's the first login or if the user missed a day
                currentStreak = 1;
            } else {
                // Increment the streak
                currentStreak++;
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(userId + "_ClaimStatus", false);
            editor.apply();
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(userId + "_LastLoginDate", today.getTimeInMillis());
        editor.putInt(userId + "_CurrentStreak", currentStreak);
        editor.apply();

        Log.d(TAG, "Updated Last Login: " + today.getTimeInMillis() + ", Updated Streak: " + currentStreak);
    }

    private boolean isSameDay(Calendar lastLogin, Calendar today) {
        return lastLogin.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && lastLogin.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    private void setupDaysGrid(View view) {
        int currentStreak = prefs.getInt(userId + "_CurrentStreak", 1);
        int startDay = Math.max(1, currentStreak - 6);

        int[] dayIds = {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7};
        int[] tickIds = {R.id.tick1, R.id.tick2, R.id.tick3, R.id.tick4, R.id.tick5, R.id.tick6, R.id.tick7};

        for (int i = 0; i < dayIds.length; i++) {
            int day = startDay + i;
            TextView dayView = view.findViewById(dayIds[i]);
            ImageView tickView = view.findViewById(tickIds[i]);

            dayView.setText("Day " + day);

            if (day < currentStreak) {
                dayView.setBackgroundResource(R.drawable.day_background_checked);
                dayView.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.white));
                tickView.setVisibility(View.VISIBLE);
            } else if (day == currentStreak) {
                if (prefs.getBoolean(userId + "_ClaimStatus", false)) {
                    dayView.setBackgroundResource(R.drawable.day_background_checked);
                    dayView.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.white));
                    tickView.setVisibility(View.VISIBLE);
                } else {
                    dayView.setBackgroundResource(R.drawable.day_background_current);
                    dayView.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.black));
                    tickView.setVisibility(View.GONE);
                }
            } else {
                dayView.setBackgroundResource(R.drawable.day_background_unchecked);
                dayView.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.darker_gray));
                tickView.setVisibility(View.GONE);
            }
        }
    }

    private void setupClaimButton(View view) {
        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimReward(view);
            }
        });
    }

    private void checkClaimStatus(View view) {
        boolean claimed = prefs.getBoolean(userId + "_ClaimStatus", false);
        Log.d(TAG, "Claim Status for user " + userId + ": " + claimed);
        if (claimed) {
            claimButton.setEnabled(false);
            claimButton.setText("Claimed");
            updateCurrentDayBox(view);
        }
    }

    private void claimReward(View view) {
        DatabaseReference userCoinsRef = mDatabase.child("users").child(userId).child("friendCoins");
        userCoinsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentCoins = dataSnapshot.getValue(Integer.class);
                if (currentCoins == null) {
                    currentCoins = 0;
                }
                int newCoins = currentCoins + REWARD_AMOUNT;
                userCoinsRef.setValue(newCoins)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getActivity(), "Reward claimed! +20 FriendCoins", Toast.LENGTH_SHORT).show();
                            claimButton.setEnabled(false);
                            claimButton.setText("Claimed");

                            // Update SharedPreferences to reflect the claim status
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(userId + "_ClaimStatus", true);
                            editor.apply();

                            Log.d(TAG, "Claim status updated to true for user " + userId);

                            updateLoginStreak();
                            setupDaysGrid(view);
                            updateCurrentDayBox(view);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Failed to claim reward. Please try again.", Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCurrentDayBox(View view) {
        int currentStreak = prefs.getInt(userId + "_CurrentStreak", 1);
        int startDay = Math.max(1, currentStreak - 6);
        int currentDayIndex = currentStreak - startDay;

        int[] dayIds = {R.id.day1, R.id.day2, R.id.day3, R.id.day4, R.id.day5, R.id.day6, R.id.day7};
        int[] tickIds = {R.id.tick1, R.id.tick2, R.id.tick3, R.id.tick4, R.id.tick5, R.id.tick6, R.id.tick7};
        if (currentDayIndex >= 0 && currentDayIndex < dayIds.length) {
            TextView currentDayView = view.findViewById(dayIds[currentDayIndex]);
            ImageView currentTickView = view.findViewById(tickIds[currentDayIndex]);

            currentDayView.setBackgroundResource(R.drawable.day_background_checked);
            currentDayView.setTextColor(ContextCompat.getColor(requireActivity(), android.R.color.white));
            currentDayView.setPadding(8, 8, 8, 8);  // Reset padding to ensure consistency
            currentTickView.setVisibility(View.VISIBLE);

            Log.d(TAG, "Updated current day box: Day " + (currentDayIndex + 1));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}

