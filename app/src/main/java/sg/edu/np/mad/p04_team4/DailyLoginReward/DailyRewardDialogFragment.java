package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    // UI components
    private LinearLayout daysLayout;
    private Button claimButton;

    // SharedPreferences for storing user data
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "DailyRewardPrefs";

    // Reward amount for daily login
    private static final int REWARD_AMOUNT = 20;

    // Firebase authentication and database references
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;

    private static final String TAG = "DailyRewardDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the dialog
        View view = inflater.inflate(R.layout.activity_daily_reward, container, false);

        // Initialize UI components
        daysLayout = view.findViewById(R.id.daysLayout);
        claimButton = view.findViewById(R.id.claimButton);

        // Initialize SharedPreferences
        prefs = requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d(TAG, "User ID: " + userId);

            // Update the login streak for the user
            updateLoginStreak();

            // Set up the days grid and claim button
            setupDaysGrid(view);
            setupClaimButton(view);

            // Check if the reward has already been claimed for today
            checkClaimStatus(view);
        } else {
            // If the user is not logged in, show a message and close the dialog
            Toast.makeText(getActivity(), getString(R.string.please_login_to_claim), Toast.LENGTH_SHORT).show();
            dismiss();
        }

        return view;
    }

    // Update the login streak of the user
    private void updateLoginStreak() {
        Calendar today = Calendar.getInstance();
        long lastLoginMillis = prefs.getLong(userId + "_LastLoginDate", 0);
        Calendar lastLogin = Calendar.getInstance();
        lastLogin.setTimeInMillis(lastLoginMillis);

        int currentStreak = prefs.getInt(userId + "_CurrentStreak", 0);
        Log.d(TAG, "Last Login: " + lastLoginMillis + ", Current Streak: " + currentStreak);

        if (!isSameDay(lastLogin, today)) {
            Log.d(TAG, "New day detected.");

            long diffInMillis = today.getTimeInMillis() - lastLogin.getTimeInMillis();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (lastLoginMillis == 0 || diffInDays > 2) {
                // Reset streak if more than 2 days have passed
                currentStreak = 1;
            } else {
                currentStreak++;
            }

            // Update SharedPreferences with new values
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(userId + "_ClaimStatus", false);
            editor.putLong(userId + "_LastLoginDate", today.getTimeInMillis());
            editor.putInt(userId + "_CurrentStreak", currentStreak);
            editor.apply();

            Log.d(TAG, "Updated Last Login: " + today.getTimeInMillis() + ", Updated Streak: " + currentStreak);
        }
    }

    // Check if the reward dialog should be shown
    public static boolean shouldShowReward(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return false;

        String userId = currentUser.getUid();
        boolean claimed = prefs.getBoolean(userId + "_ClaimStatus", false);
        long lastLoginMillis = prefs.getLong(userId + "_LastLoginDate", 0);
        Calendar lastLogin = Calendar.getInstance();
        lastLogin.setTimeInMillis(lastLoginMillis);
        Calendar today = Calendar.getInstance();

        // Show reward if it has not been claimed today
        return !claimed || !isSameDay(lastLogin, today);
    }

    // Check if two Calendar instances represent the same day
    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    // Set up the grid showing the user's login streak
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

    // Set up the claim button and its click listener
    private void setupClaimButton(View view) {
        claimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                claimReward(view);
            }
        });
    }

    // Check if the reward has already been claimed and update the UI accordingly
    private void checkClaimStatus(View view) {
        boolean claimed = prefs.getBoolean(userId + "_ClaimStatus", false);
        Log.d(TAG, "Claim Status for user " + userId + ": " + claimed);
        if (claimed) {
            claimButton.setEnabled(false);
            claimButton.setText("Claimed");
            updateCurrentDayBox(view);
        } else {
            claimButton.setEnabled(true);
            claimButton.setText("Claim");
        }
    }

    // Claim the reward and update the user's coin balance in the database
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
                            Toast.makeText(getActivity(), getString(R.string.reward_claimed), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getActivity(), getString(R.string.failed_to_claim), Toast.LENGTH_SHORT).show();
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), getString(R.string.error_claim) + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Update the current day box in the UI after claiming the reward
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
            // Set the dialog window layout and background
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
    }
}