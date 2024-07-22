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
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import sg.edu.np.mad.p04_team4.HomeActivity;
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

        lvYesterday = findViewById(R.id.lvYesterday);
        lvToday = findViewById(R.id.lvToday);

        yesterdayAdapter = new ScreenTimeAdapter(this, yesterdayData);
        todayAdapter = new ScreenTimeAdapter(this, todayData);

        lvYesterday.setAdapter(yesterdayAdapter);
        lvToday.setAdapter(todayAdapter);

        pieChart = findViewById(R.id.pieChart);

        displayScreenTimeData();

        // Start the foreground service
        Intent serviceIntent = new Intent(this, ScreenTimeService.class);
        startService(serviceIntent);
    }

    private void displayScreenTimeData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
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

    private void updatePieChart(List<PieEntry> pieEntries) {
        PieDataSet dataSet = new PieDataSet(pieEntries, "Screen Time");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setDrawValues(false); // Remove the time text from the pie chart
        dataSet.setValueTextColor(Color.BLACK); // Set the label text color to black
        dataSet.setValueTextSize(16f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false); // Remove description label
        pieChart.invalidate(); // Refresh the chart
    }

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
