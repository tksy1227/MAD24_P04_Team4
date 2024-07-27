package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.R;

public class ThemeUtils {

    // Method to apply the selected theme to the given root view
    public static void applyTheme(Context context, View rootView) {
        // Check for null context or rootView
        if (context == null || rootView == null) return;

        // Get shared preferences for the app
        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        // Get the current logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Retrieve the selected theme for the current user
            String selectedTheme = sharedPreferences.getString("selectedTheme_" + userId, "default");

            Drawable background;
            // Select the background drawable based on the selected theme
            switch (selectedTheme) {
                case "Fluid Harmony":
                    background = context.getDrawable(R.drawable.theme_1);
                    break;
                case "Blue Blossom":
                    background = context.getDrawable(R.drawable.theme_2);
                    break;
                case "Playful Safari":
                    background = context.getDrawable(R.drawable.theme_3);
                    break;
                case "default":
                default:
                    background = null; // No specific background for the default case
                    break;
            }

            // Apply the selected background to the root view
            if (background != null) {
                rootView.setBackground(background);
            } else {
                // Set default background color if no specific theme is selected
                rootView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            }
        }
    }
}
