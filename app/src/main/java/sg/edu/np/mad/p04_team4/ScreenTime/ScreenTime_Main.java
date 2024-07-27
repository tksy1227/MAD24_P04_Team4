package sg.edu.np.mad.p04_team4.ScreenTime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;
import sg.edu.np.mad.p04_team4.Home.HomeActivity;
import sg.edu.np.mad.p04_team4.R;

public class ScreenTime_Main extends AppCompatActivity {
    private ListView lvYesterday, lvToday;
    private ScreenTimeAdapter yesterdayAdapter, todayAdapter;
    private List<ScreenTimeEntry> yesterdayData = new ArrayList<>();
    private List<ScreenTimeEntry> todayData = new ArrayList<>();
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_time);

        // Apply the selected theme to the root view
        View rootView = findViewById(R.id.main); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Disable the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Handle custom back button click
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the HomeActivity
                Intent intent = new Intent(ScreenTime_Main.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Initialize ListViews and adapters for yesterday's and today's screen time data
        lvYesterday = findViewById(R.id.lvYesterday);
        lvToday = findViewById(R.id.lvToday);

        yesterdayAdapter = new ScreenTimeAdapter(this, yesterdayData);
        todayAdapter = new ScreenTimeAdapter(this, todayData);

        lvYesterday.setAdapter(yesterdayAdapter);
        lvToday.setAdapter(todayAdapter);

        // Initialize the PieChart
        pieChart = findViewById(R.id.pieChart);

        // Display screen time data
        displayScreenTimeData();

        // Start the foreground service to track screen time
        Intent serviceIntent = new Intent(this, ScreenTimeService.class);
        startService(serviceIntent);
    }

    // Method to retrieve and display screen time data from Firebase
    private void displayScreenTimeData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, getString(R.string.user_not_authenticated2), Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String yesterday = getYesterdayDate();

        DatabaseReference todayRef = FirebaseDatabase.getInstance().getReference("screenTime").child(userId).child(today);
        DatabaseReference yesterdayRef = FirebaseDatabase.getInstance().getReference("screenTime").child(userId).child(yesterday);

        // Retrieve today's data
        todayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todayData.clear();
                List<PieEntry> pieEntries = new ArrayList<>();
                for (DataSnapshot featureSnapshot : snapshot.getChildren()) {
                    String featureName = featureSnapshot.getKey();
                    Object value = featureSnapshot.getValue();

                    if (value instanceof Long) {
                        Long duration = (Long) value;
                        todayData.add(new ScreenTimeEntry(featureName, duration));
                        pieEntries.add(new PieEntry(duration, featureName));
                        Log.d("ScreenTime_Main", "Feature: " + featureName + ", Duration: " + duration);
                    } else if (value instanceof Map) {
                        // Handle nested map
                        Map<String, Object> map = (Map<String, Object>) value;
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            String subFeatureName = featureName + " - " + entry.getKey();
                            Object subValue = entry.getValue();
                            if (subValue instanceof Long) {
                                Long duration = (Long) subValue;
                                todayData.add(new ScreenTimeEntry(subFeatureName, duration));
                                pieEntries.add(new PieEntry(duration, subFeatureName));
                                Log.d("ScreenTime_Main", "SubFeature: " + subFeatureName + ", Duration: " + duration);
                            } else {
                                Log.e("ScreenTime_Main", "Unexpected data type in nested map: " + subValue.getClass().getName());
                            }
                        }
                    } else {
                        Log.e("ScreenTime_Main", "Unexpected data type: " + value.getClass().getName());
                    }
                }
                todayAdapter.notifyDataSetChanged();
                updatePieChart(pieEntries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        // Retrieve yesterday's data
        yesterdayRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yesterdayData.clear();
                for (DataSnapshot featureSnapshot : snapshot.getChildren()) {
                    String featureName = featureSnapshot.getKey();
                    Object value = featureSnapshot.getValue();
                    Log.d("ScreenTime_Main", "Processing feature: " + featureName + " with value: " + value);
                    processFeatureData(featureName, value, yesterdayData, null);
                }
                yesterdayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    // Process feature data for screen time and add to the list
    private void processFeatureData(String featureName, Object value, List<ScreenTimeEntry> dataList, List<PieEntry> pieEntries) {
        if (value instanceof Long) {
            Long duration = (Long) value;
            dataList.add(new ScreenTimeEntry(featureName, duration));
            if (pieEntries != null) {
                pieEntries.add(new PieEntry(duration, featureName));
            }
        } else if (value instanceof Map) {
            Log.e("ScreenTime_Main", "Expected Long but got Map. Data: " + value);
            // Handle nested Map
            Map<String, Object> map = (Map<String, Object>) value;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() instanceof Long) {
                    Long duration = (Long) entry.getValue();
                    dataList.add(new ScreenTimeEntry(featureName + " - " + entry.getKey(), duration));
                    if (pieEntries != null) {
                        pieEntries.add(new PieEntry(duration, featureName + " - " + entry.getKey()));
                    }
                } else if (entry.getValue() instanceof Integer) {
                    Long duration = ((Integer) entry.getValue()).longValue();
                    dataList.add(new ScreenTimeEntry(featureName + " - " + entry.getKey(), duration));
                    if (pieEntries != null) {
                        pieEntries.add(new PieEntry(duration, featureName + " - " + entry.getKey()));
                    }
                } else if (entry.getValue() instanceof Map) {
                    // Recursively handle nested Map
                    Log.d("ScreenTime_Main", "Nested Map detected for: " + featureName + " - " + entry.getKey());
                    processFeatureData(featureName + " - " + entry.getKey(), entry.getValue(), dataList, pieEntries);
                } else {
                    Log.e("ScreenTime_Main", "Nested unexpected data type: " + entry.getValue().getClass().getName());
                }
            }
        } else if (value instanceof Integer) {
            Long duration = ((Integer) value).longValue();
            dataList.add(new ScreenTimeEntry(featureName, duration));
            if (pieEntries != null) {
                pieEntries.add(new PieEntry(duration, featureName));
            }
        } else {
            Log.e("ScreenTime_Main", "Unexpected data type: " + value.getClass().getName());
        }
    }

    // Update the PieChart with the screen time data
    private void updatePieChart(List<PieEntry> pieEntries) {
        PieDataSet dataSet = new PieDataSet(pieEntries, "Screen Time");

        // Define 11 different colors for the PieChart
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#F44336")); // Red
        colors.add(Color.parseColor("#E91E63")); // Pink
        colors.add(Color.parseColor("#9C27B0")); // Purple
        colors.add(Color.parseColor("#673AB7")); // Deep Purple
        colors.add(Color.parseColor("#3F51B5")); // Indigo
        colors.add(Color.parseColor("#2196F3")); // Blue
        colors.add(Color.parseColor("#03A9F4")); // Light Blue
        colors.add(Color.parseColor("#00BCD4")); // Cyan
        colors.add(Color.parseColor("#009688")); // Teal
        colors.add(Color.parseColor("#4CAF50")); // Green
        colors.add(Color.parseColor("#8BC34A")); // Light Green

        dataSet.setColors(colors);
        dataSet.setDrawValues(false); // Hide the time values on the pie chart

        // Ensure entry label text color is black
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Customize legend
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL); // Display legend items vertically
        legend.setDrawInside(false);
        legend.setTextSize(14f);
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(10f);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(7f);

        pieChart.getDescription().setEnabled(false); // Remove description label
        pieChart.invalidate(); // Refresh the chart
    }

    // Get yesterday's date in the specified format
    private String getYesterdayDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Navigate back to the HomeActivity when the up button is pressed
        Intent intent = new Intent(ScreenTime_Main.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to the HomeActivity when the back button is pressed
        Intent intent = new Intent(ScreenTime_Main.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
