package sg.edu.np.mad.p04_team4.HabitTracker;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sg.edu.np.mad.p04_team4.R;
import sg.edu.np.mad.p04_team4.DailyLoginReward.ThemeUtils;

public class lineChartview extends AppCompatActivity {
    //private LineChart chart;
    private Spinner spinnerTimeGroup;
    private HashMap<String, String> optionIdentifierMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_line_chartview);

        View rootView = findViewById(R.id.chart); // Ensure this matches the root layout id
        ThemeUtils.applyTheme(this, rootView);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chart), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //LineChart chart = findViewById(R.id.chart1);
        TextView title = findViewById(R.id.textViewToolbarTitle);
        ImageButton backbutton = findViewById(R.id.backButton);
        Button add_data = findViewById(R.id.button2);
        Button bar_chart = findViewById(R.id.button1);
        Button insights = findViewById(R.id.button3);
        spinnerTimeGroup = findViewById(R.id.spinnerTimeGroup);
        bar_chart.setText(getString(R.string.bar_chart));
        insights.setText(getString(R.string.Insights));
        final String[] identifier = new String[1];

        //Back button
        backbutton.setOnClickListener(v -> {
            Intent back = new Intent(lineChartview.this, selectHabit.class);
            startActivity(back);
        });
        String habit = getIntent().getStringExtra("habit");
        String option = getIntent().getStringExtra("option");
        Log.d("erm",getString(R.string.habit));
        if (habit == null) {
            //do error handling
            Log.e("passing", "Habit not passed");
            // go back
            Intent back = new Intent(lineChartview.this, selectHabit.class);
            startActivity(back);
        }
        if (option == null) {
            //do error handling
            Log.e("passing", "option not passed, setting as 0");
            option = "1";
        }
        else
        {
            Log.d("passing", "good pass, option "+option);
            Log.d("passing", "good pass, habit "+habit);
        }
        Log.d("passing", "option: "+option);

        //setupLineChart(habit, option);
        initializeOptionIdentifierMap();

        // Set up the Spinner with options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.time_group_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeGroup.setAdapter(adapter);
        spinnerTimeGroup.setSelection(Integer.parseInt(option)-1);
        Log.d("selecting_option","option at selection "+option);



        spinnerTimeGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedOption = (String) parent.getItemAtPosition(position);
                identifier[0] = optionIdentifierMap.get(selectedOption);
                Log.d("selecting_option", getString(R.string.option_space) + selectedOption + getString(R.string.selected_with_identifier) + identifier[0]);
                //Handle the selected identifier
                setupLineChart(habit, identifier[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
        title.setText(habit + getString(R.string.chart_title));

        //going to add data activity
        add_data.setOnClickListener(v -> {
            Intent intentToCFromD = new Intent(lineChartview.this, Datainput.class);
            intentToCFromD.putExtra(getString(R.string.origin), "B"); // Pass origin identifier
            intentToCFromD.putExtra(getString(R.string.habit), habit);
            startActivity(intentToCFromD);
        });
        bar_chart.setOnClickListener(v->
        {
            Intent barchart = new Intent(lineChartview.this, Chartview.class);
            barchart.putExtra(getString(R.string.habit),habit);
            barchart.putExtra(getString(R.string.option),identifier[0]);
            startActivity(barchart);
        });
        insights.setOnClickListener(v->{
            Intent insight = new Intent (lineChartview.this, insights.class);
            insight.putExtra(getString(R.string.habit),habit);
            startActivity(insight);
        });
    }




    private void setupLineChart(String habit,String option) {
        LineChart chart = findViewById(R.id.chart1);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(true);
        chart.getXAxis().setValueFormatter(null);
        chartDBhandler dbHandler = new chartDBhandler(lineChartview.this);
        if(option .equals("1")) {
            ArrayList<chartData> raw_data = dbHandler.getDataByHabit(habit);
            List<Entry> values = dbHandler.convertToLineEntries(raw_data); // Assuming convertToLineEntries() converts to Entry type

            LineDataSet set1 = new LineDataSet(values, "Data Set 1");
            set1.setDrawValues(true);

            // Define colors
            int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
            int lineColor = Color.parseColor("#4CAF50"); // Green color for the line

            // Set colors
            set1.setColor(lineColor);
            set1.setCircleColor(lineColor);
            set1.setLineWidth(2f); // Line width
            set1.setCircleRadius(4f); // Circle radius
            set1.setValueTextSize(12f); // Value text size
            set1.setValueTextColor(Color.BLACK); // Value text color

            LineData data = new LineData(set1);
            data.setValueTextColor(Color.BLACK); // Set data label color to black

            chart.setData(data);

            // Customize the chart appearance
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
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
            rightAxis.setEnabled(false); // Disable right axis

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
            else if (option.equals("3")) {
                ArrayList<chartData> raw_data = dbHandler.getDataByHabit(habit);
                List<Entry> values = new ArrayList<>();
                final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

                HashMap<String, Float> weeklyData = new HashMap<>();
                for (String day: daysOfWeek){
                    float initial = 0;
                    weeklyData.put(day, initial);
                    Log.d("eeee", day);
                }

                // Aggregate data by day of the week
                for (chartData data : raw_data) {
                    String dayOfWeek = getDayOfWeekFromDate(data.getDate()); // This method returns the day of the week string
                    float value = data.getData(); // This method returns data value
                    float currentValue = weeklyData.get(dayOfWeek);
                    weeklyData.put(dayOfWeek, currentValue + value); // Summing up values for each day
                }

                // Convert aggregated data to entries
                for (String dayOfWeek : daysOfWeek) { // Ensure the order is as per daysOfWeek array
                    float totalValue = weeklyData.get(dayOfWeek);
                    int dayIndex = getDayIndex(dayOfWeek); // Use method to get day index
                    values.add(new Entry(dayIndex, totalValue));
                    Log.d("dedede", String.valueOf(dayIndex));
                }

                // Sort entries by x-axis index
                Collections.sort(values, new Comparator<Entry>() {
                    @Override
                    public int compare(Entry e1, Entry e2) {
                        return Float.compare(e1.getX(), e2.getX());
                    }
                });

                LineDataSet set1 = new LineDataSet(values, getString(R.string.weekly_data));
                    set1.setDrawValues(true);

                    // Define colors
                    int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
                    int lineColor = Color.parseColor("#4CAF50"); // Green color for the line

                    // Set colors
                    set1.setColor(lineColor);
                    set1.setCircleColor(lineColor);
                    set1.setLineWidth(2f); // Line width
                    set1.setCircleRadius(4f); // Circle radius
                    set1.setValueTextSize(12f); // Value text size
                    set1.setValueTextColor(Color.BLACK); // Value text color

                    LineData data = new LineData(set1);
                    data.setValueTextColor(Color.BLACK); // Set data label color to black

                    chart.setData(data);

                    // Customize the chart appearance
                    chart.getDescription().setEnabled(false);
                    chart.setDrawGridBackground(false);
                    chart.setPinchZoom(true);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(true);
                    xAxis.setTextColor(Color.DKGRAY);
                    xAxis.setTextSize(12f);

                    // Set X-axis labels to days of the week

                    xAxis.setValueFormatter(new IndexAxisValueFormatter(daysOfWeek));

                    YAxis leftAxis = chart.getAxisLeft();
                    leftAxis.setLabelCount(8, false);
                    leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                    leftAxis.setSpaceTop(15f);
                    leftAxis.setAxisMinimum(0f);
                    leftAxis.setTextColor(Color.DKGRAY);
                    leftAxis.setTextSize(12f);

                    YAxis rightAxis = chart.getAxisRight();
                    rightAxis.setEnabled(false); // Disable right axis

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
            else if (option.equals("4")){
                ArrayList<chartData> raw_data = dbHandler.getDataByHabit(habit);
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

                List<Entry> values = new ArrayList<>();
                int index = 0;
                for (String month : monthlyData.keySet()) {
                    float totalValue = monthlyData.get(month);
                    values.add(new Entry(index++, totalValue));
                }

                // Create LineDataSet
                LineDataSet dataSet = new LineDataSet(values, habit);
                dataSet.setDrawValues(true);

                // Find the highest and lowest points
                Entry highest = null;
                Entry lowest = null;
                for (Entry entry : values) {
                    if (highest == null || entry.getY() > highest.getY()) {
                        highest = entry;
                    }
                    if (lowest == null || entry.getY() < lowest.getY()) {
                        lowest = entry;
                    }
                }

                // Define colors
                int baseColor = Color.parseColor("#76C7C0"); // Base pastel color

                int lineColor = Color.parseColor("#4CAF50");// Red color for lowest point

                // Add custom colors for highest and lowest points


                // Set line color and width
                dataSet.setColor(lineColor); // Line color
                dataSet.setCircleColor(lineColor);
                dataSet.setLineWidth(2f); // Line width

                // Create LineData object
                LineData lineData = new LineData(dataSet);
                lineData.setValueTextSize(12f); // Increase data label size
                lineData.setValueTextColor(Color.BLACK); // Set data label color to black

                // Set data to the chart
                chart.setData(lineData);

                // Customize the chart appearance
                chart.getDescription().setEnabled(false);
                chart.setDrawGridBackground(false);
                chart.setDrawBorders(true);
                chart.setDrawMarkers(true); // Enable markers

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
                legend.setForm(Legend.LegendForm.LINE); // Changed to LINE for LineChart
                legend.setFormSize(9f);
                legend.setTextSize(11f);
                legend.setXEntrySpace(4f);

                Log.d("spinner", getString(R.string.option_space2) + option + getString(R.string.passed_space));

            }
            else if (option.equals("2")) {
            ArrayList<chartData> raw_data = dbHandler.getDataByHabit(habit);

            List<Entry> entries = new ArrayList<>();

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
                entries.add(new Entry((float) week + 1, totalValue));
            }

            int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
            int lineColor = Color.parseColor("#4CAF50"); // Green color for the line

            // Create the LineDataSet and set the data to the chart
            LineDataSet set1 = new LineDataSet(entries, habit);
            set1.setDrawValues(true);

            // Set colors
            set1.setColor(lineColor);
            set1.setCircleColor(lineColor);
            set1.setLineWidth(2f); // Line width
            set1.setCircleRadius(4f); // Circle radius
            set1.setValueTextSize(12f); // Value text size
            set1.setValueTextColor(Color.BLACK); // Value text color

            LineData data = new LineData(set1);
            data.setValueTextColor(Color.BLACK); // Set data label color to black

            chart.setData(data);

            // Customize the chart appearance
            chart.getDescription().setEnabled(false);
            chart.setDrawGridBackground(false);
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
            rightAxis.setEnabled(false); // Disable right axis

            Legend legend = chart.getLegend();
            legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
            legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
            legend.setDrawInside(false);
            legend.setForm(Legend.LegendForm.SQUARE);
            legend.setFormSize(9f);
            legend.setTextSize(11f);
            legend.setXEntrySpace(4f);

            chart.invalidate(); // Refresh the chart

            Log.d("spinner", getString(R.string.option_space2) + option + getString(R.string.passed_space));
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