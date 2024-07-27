package sg.edu.np.mad.p04_team4.Home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply the theme
        View rootView = findViewById(android.R.id.content);
        ThemeUtils.applyTheme(this, rootView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-apply the theme to handle theme changes while the app is running
        View rootView = findViewById(android.R.id.content);
        ThemeUtils.applyTheme(this, rootView);
    }
}
