package sg.edu.np.mad.p04_team4.DailyLoginReward;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import sg.edu.np.mad.p04_team4.R;

public class ThemeUtils {
    public static void applyTheme(Context context, View rootView) {
        if (context == null || rootView == null) return;

        SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String selectedTheme = sharedPreferences.getString("selectedTheme_" + userId, "default");

            Drawable background;
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

            if (background != null) {
                rootView.setBackground(background);
            } else {
                rootView.setBackgroundColor(context.getResources().getColor(android.R.color.white)); // Default color
            }
        }
    }
}
