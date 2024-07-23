package sg.edu.np.mad.p04_team4.HabitTracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class Chartview extends AppCompatActivity {
    private BarChart chart;
    private Spinner spinnerTimeGroup;
    private HashMap<String, String> optionIdentifierMap;

    //private SeekBar seekBarX, seekBarY;
    //private TextView tvX, tvY;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chartview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chart = findViewById(R.id.chart1);
        TextView title = findViewById(R.id.textViewToolbarTitle);
        ImageButton backbutton = findViewById(R.id.backButton);
        Button add_data = findViewById(R.id.button2);
        Button change_chart = findViewById(R.id.button1);
        Button insights = findViewById(R.id.button3);
        spinnerTimeGroup = findViewById(R.id.spinnerTimeGroup);
        final String[] identifier = new String[1];
        insights.setText("Insights");
        change_chart.setText("Line chart");

        //retrieve habit reference from previous activity
        String habit = getIntent().getStringExtra("habit");
        String option = getIntent().getStringExtra("option");
        if (habit == null) {
            //do error handling
            Log.e("passing", "Habit not passed");
            // go back
            Intent back = new Intent(Chartview.this, selectHabit.class);
            startActivity(back);
        }
        if (option == null) {
            //do error handling
            Log.e("passing", "option not passed, setting option as 1");
            option = "1";
        }
        else
        {
            Log.d("passing", "Good pass, option: "+option);
            Log.d("passing", "Good pass, habit: "+habit);
        }

        initializeOptionIdentifierMap();


        // Set up the Spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_group_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeGroup.setAdapter(adapter);
        chartDBhandler dbHandler = new chartDBhandler(Chartview.this);
        ArrayList<chartData> raw_data = dbHandler.getDataByHabit(habit);
        int size = raw_data.size();
        Log.d("size", String.valueOf(size));
        if(size>200){
            spinnerTimeGroup.setSelection(3);
            Toast.makeText(this, "Number of entries more than 200, grouping data by month", Toast.LENGTH_SHORT).show();
        }
        else if (size>50){
            spinnerTimeGroup.setSelection(1);
            Toast.makeText(this, "Number of entries more than 50, grouping data by week", Toast.LENGTH_SHORT).show();
        }
        else{

            spinnerTimeGroup.setSelection(Integer.parseInt(option) - 1);
        }
        // Handle spinner item selections
        spinnerTimeGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                identifier[0] = optionIdentifierMap.get(selectedOption);
                Log.d("spinner", "option: " + selectedOption + " selected with identifier: " + identifier[0]);
                // Handle the selected identifier
                setupChart(habit, identifier[0],raw_data);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //setupChart(habit,"Day");
                // Do nothing
            }
        });


        title.setText("Chart " + habit + "!!!! :3");


        //setupChart(habit,"day");
        //going to add data activity
        add_data.setOnClickListener(v -> {
            Intent intentToCFromD = new Intent(Chartview.this, Datainput.class);
            intentToCFromD.putExtra("origin", "B"); // Pass origin identifier
            intentToCFromD.putExtra("habit", habit);
            startActivity(intentToCFromD);
        });
        change_chart.setOnClickListener(v->{
            Intent change = new Intent(Chartview.this, lineChartview.class);
            change.putExtra("habit",habit);
            change.putExtra("option", identifier[0]);
            startActivity(change);
        });
        //Back button
        backbutton.setOnClickListener(v -> {
            Intent back = new Intent(Chartview.this, selectHabit.class);
            startActivity(back);
        });
        insights.setOnClickListener(v->{
            Intent insight = new Intent (Chartview.this, insights.class);
            insight.putExtra("habit",habit);
            startActivity(insight);
        });


    }

    private void setupChart(String habit, String option,ArrayList<chartData> raw_data ) {
        // Reset any default settings that might persist
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setPinchZoom(true);
        chart.getXAxis().setValueFormatter(null);


        chartDBhandler dbHandler = new chartDBhandler(Chartview.this);

        if(option.equals("1")) {

            List<BarEntry> values = dbHandler.convertToBarEntries(raw_data);

            BarDataSet set1 = new BarDataSet(values, habit);
            set1.setDrawValues(true);

            // Find the highest and lowest points
            BarEntry highest = null;
            BarEntry lowest = null;
            for (BarEntry entry : values) {
                if (highest == null || entry.getY() > highest.getY()) {
                    highest = entry;
                }
                if (lowest == null || entry.getY() < lowest.getY()) {
                    lowest = entry;
                }
            }

            // Define colors
            int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
            int highestColor = Color.parseColor("#4CAF50"); // Green color for highest point
            int lowestColor = Color.parseColor("#F44336"); // Red color for lowest point

            // Add custom colors for highest and lowest points
            List<Integer> colors = new ArrayList<>();
            for (BarEntry entry : values) {
                if (entry == highest) {
                    colors.add(highestColor); // Color for the highest point
                } else if (entry == lowest) {
                    colors.add(lowestColor); // Color for the lowest point
                } else {
                    colors.add(baseColor); // Regular color
                }
            }
            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(12f); // Increase data label size
            data.setValueTextColor(Color.BLACK); // Set data label color to black

            chart.setData(data);

            // Customize the chart appearance
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true); // Draw values above the bars
            chart.setPinchZoom(true);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setTextColor(Color.DKGRAY);
            xAxis.setTextSize(12f);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setLabelCount(8, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setTextSize(12f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);
            rightAxis.setTextColor(Color.DKGRAY);
            rightAxis.setTextSize(12f);

            Legend legend = chart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setFormSize(9f);
            legend.setTextSize(11f);
            legend.setXEntrySpace(4f);
            Log.d("spinner","option "+ option+"passed");
        }
        else if(option.equals("4")) {

            HashMap<String, Float> monthlyData = new HashMap<>();

            // Aggregate data by month
            for (chartData data : raw_data) {
                String month = getMonthFromDate(data.getDate()); // getDate() returns a string date
                float value = data.getData(); // getData() returns the data value

                if (monthlyData.containsKey(month)) {
                    float currentValue = monthlyData.get(month);
                    monthlyData.put(month, currentValue + value); // Summing up values for each month
                } else {
                    monthlyData.put(month, value);
                }
            }
            List<BarEntry> values = new ArrayList<>();
            int index = 0;
            for (String month : monthlyData.keySet()) {
                float totalValue = monthlyData.get(month);
                values.add(new BarEntry(index++, totalValue));
            }
            if (values.size()<60)
            {
                Toast.makeText(this, "Number of entries is small, grouping by week/input might provide better insights", Toast.LENGTH_SHORT).show();

            }

            //List<BarEntry> values = dbHandler.convertToBarEntries(raw_data);

            BarDataSet set1 = new BarDataSet(values, habit);
            set1.setDrawValues(true);

            // Find the highest and lowest points
            BarEntry highest = null;
            BarEntry lowest = null;
            for (BarEntry entry : values) {
                if (highest == null || entry.getY() > highest.getY()) {
                    highest = entry;
                }
                if (lowest == null || entry.getY() < lowest.getY()) {
                    lowest = entry;
                }
            }

            // Define colors
            int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
            int highestColor = Color.parseColor("#4CAF50"); // Green color for highest point
            int lowestColor = Color.parseColor("#F44336"); // Red color for lowest point

            // Add custom colors for highest and lowest points
            List<Integer> colors = new ArrayList<>();
            for (BarEntry entry : values) {
                if (entry == highest) {
                    colors.add(highestColor); // Color for the highest point
                } else if (entry == lowest) {
                    colors.add(lowestColor); // Color for the lowest point
                } else {
                    colors.add(baseColor); // Regular color
                }
            }
            set1.setColors(colors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(12f); // Increase data label size
            data.setValueTextColor(Color.BLACK); // Set data label color to black

            chart.setData(data);

            // Customize the chart appearance
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true); // Draw values above the bars
            chart.setPinchZoom(true);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setTextColor(Color.DKGRAY);
            xAxis.setTextSize(12f);

            ArrayList<String> months = new ArrayList<>(monthlyData.keySet());
            xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setLabelCount(8, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setTextSize(12f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);
            rightAxis.setTextColor(Color.DKGRAY);
            rightAxis.setTextSize(12f);

            Legend legend = chart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setFormSize(9f);
            legend.setTextSize(11f);
            legend.setXEntrySpace(4f);
            Log.d("spinner","option "+ option+"passed");

        }
        else if (option.equals("3")) {

            HashMap<String, Float> weeklyData = new HashMap<>();

            // Aggregate data by day of week
            for (chartData data : raw_data) {
                String dayOfWeek = getDayOfWeekFromDate(data.getDate()); // this method returns day of the week string
                float value = data.getData(); // this method returns data value

                if (weeklyData.containsKey(dayOfWeek)) {
                    float currentValue = weeklyData.get(dayOfWeek);
                    weeklyData.put(dayOfWeek, currentValue + value); // Summing up values for each day
                } else {
                    weeklyData.put(dayOfWeek, value);
                }
            }

            // Prepare BarEntry objects with day indices and corresponding values
            List<BarEntry> entries = new ArrayList<>();
            for (String dayOfWeek : weeklyData.keySet()) {
                float totalValue = weeklyData.get(dayOfWeek);
                int dayIndex = getDayIndex(dayOfWeek); // Use method to get day index
                entries.add(new BarEntry(dayIndex, totalValue));
            }

            if (entries.size()<7)
            {
                Toast.makeText(this, "Number of entries is small, grouping by input might provide better insights", Toast.LENGTH_SHORT).show();

            }
            // Create a BarDataSet with entries
            BarDataSet set1 = new BarDataSet(entries, habit);
            set1.setDrawValues(true);

            // Find the highest and lowest points
            BarEntry highest = null;
            BarEntry lowest = null;
            for (BarEntry entry : entries) {
                if (highest == null || entry.getY() > highest.getY()) {
                    highest = entry;
                }
                if (lowest == null || entry.getY() < lowest.getY()) {
                    lowest = entry;
                }
            }

            // Define colors
            int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
            int highestColor = Color.parseColor("#4CAF50"); // Green color for highest point
            int lowestColor = Color.parseColor("#F44336"); // Red color for lowest point

            // Add custom colors for highest and lowest points
            List<Integer> colors = new ArrayList<>();
            for (BarEntry entry : entries) {
                if (entry == highest) {
                    colors.add(highestColor); // Color for the highest point
                } else if (entry == lowest) {
                    colors.add(lowestColor); // Color for the lowest point
                } else {
                    colors.add(baseColor); // Regular color
                }
            }
            set1.setColors(colors);

            // Create BarData and set to chart
            BarData data = new BarData(set1);
            data.setValueTextSize(12f); // Set data label size
            data.setValueTextColor(Color.BLACK); // Set data label color

            chart.setData(data);

            // Customize chart appearance
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true); // Draw values above the bars
            chart.setPinchZoom(true);

            // Customize X Axis
            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setTextColor(Color.DKGRAY);
            xAxis.setTextSize(12f);

            // Set custom day labels
            String[] daysOfWeek = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));

            // Customize Y Axis
            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setLabelCount(8, false); // Adjust label count as needed
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setTextSize(12f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false); // Adjust label count as needed
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);
            rightAxis.setTextColor(Color.DKGRAY);
            rightAxis.setTextSize(12f);

            // Customize Legend
            Legend legend = chart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setFormSize(9f);
            legend.setTextSize(11f);
            legend.setXEntrySpace(4f);
        }
        else if (option.equals("2")){

            List<BarEntry> entries = new ArrayList<>();

            // Calculate the number of weeks based on the size of raw_data
            int rawDataSize = raw_data.size();
            double num_weeks = Math.ceil((double) rawDataSize / 7);

            // Process each week
            for (int week = 0; week < num_weeks; week++) {
                float totalValue = 0;
                int startIndex = week * 7;

                // Sum the values for the current week
                for (int i = 0; i < 7; i++) {
                    int currentIndex = startIndex + i;
                    if (currentIndex < rawDataSize) {
                        float value = raw_data.get(currentIndex).getData();
                        totalValue += value;
                    }
                }

                // Add the total value for the week to the entries list
                entries.add(new BarEntry((float) week + 1, totalValue));
            }
            int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
            int highestColor = Color.parseColor("#4CAF50"); // Green color for highest point
            int lowestColor = Color.parseColor("#F44336"); // Red color for lowest point
            if (entries.size()<7)
            {
                Toast.makeText(this, "Number of entries is small, grouping by input might provide better insights", Toast.LENGTH_SHORT).show();

            }

            // Create the BarDataSet and set the data to the chart
            BarDataSet set1 = new BarDataSet(entries, habit);
            BarEntry highest = null;
            BarEntry lowest = null;
            for (BarEntry entry : entries) {
                if (highest == null || entry.getY() > highest.getY()) {
                    highest = entry;
                }
                if (lowest == null || entry.getY() < lowest.getY()) {
                    lowest = entry;
                }
            }
            List<Integer> colors = new ArrayList<>();
            for (BarEntry entry : entries) {
                if (entry == highest) {
                    colors.add(highestColor); // Color for the highest point
                } else if (entry == lowest) {
                    colors.add(lowestColor); // Color for the lowest point
                } else {
                    colors.add(baseColor); // Regular color
                }
            }
            set1.setColors(colors);
            BarData barData = new BarData(set1);
            chart.setData(barData);
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDrawBarShadow(false);
            chart.setDrawValueAboveBar(true); // Draw values above the bars
            chart.setPinchZoom(true);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setTextColor(Color.DKGRAY);
            xAxis.setTextSize(12f);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setLabelCount(8, false);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            leftAxis.setSpaceTop(15f);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setTextColor(Color.DKGRAY);
            leftAxis.setTextSize(12f);

            YAxis rightAxis = chart.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setLabelCount(8, false);
            rightAxis.setSpaceTop(15f);
            rightAxis.setAxisMinimum(0f);
            rightAxis.setTextColor(Color.DKGRAY);
            rightAxis.setTextSize(12f);

            Legend legend = chart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setFormSize(9f);
            legend.setTextSize(11f);
            legend.setXEntrySpace(4f);
            Log.d("spinner","option "+ option+"passed");
        }

        chart.invalidate(); // Refresh the chart
    }
    private String getMonthFromDate(String date) {
        if (date != null && date.length() >= 7) {
            return date.substring(5, 7); // Extract month part
        }
        return "";
    }
    private String getDayOfWeekFromDate(String dateString) {
        // Implement logic to get day of week from date string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Unknown"; // Handle parsing exception
        }

        // Get day of the week from date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Convert day of week to a readable string (e.g., "Monday", "Tuesday", etc.)
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "Unknown"; // Handle unknown day
        }
    }
    private int getDayIndex(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase()) {
            case "sunday":
                return 0;
            case "monday":
                return 1;
            case "tuesday":
                return 2;
            case "wednesday":
                return 3;
            case "thursday":
                return 4;
            case "friday":
                return 5;
            case "saturday":
                return 6;
            default:
                return -1; // Handle unknown or invalid day of the week gracefully
        }
    }
    private void initializeOptionIdentifierMap() {
        optionIdentifierMap = new HashMap<>();
        optionIdentifierMap.put("By Input", "1");
        optionIdentifierMap.put("Sort by Week", "2");
        optionIdentifierMap.put("Sort by Day of Week", "3");
        optionIdentifierMap.put("Sort by Month", "4");
    }

}