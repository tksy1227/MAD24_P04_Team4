package sg.edu.np.mad.p04_team4.HabitTracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.R;

public class insights extends AppCompatActivity {
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insights);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // defining elements
        TextView title = findViewById(R.id.textViewToolbarTitle);
        ImageButton backButton = findViewById(R.id.backButton);
        TextView textViewAverageTitle = findViewById(R.id.textViewAverageTitle);
        TextView textViewAverageValue = findViewById(R.id.textViewAverageValue);
        TextView textViewAverageUnit = findViewById(R.id.textViewAverageUnit);
        TextView textViewMinTitle = findViewById(R.id.textViewMinTitle);
        TextView textViewMinValue = findViewById(R.id.textViewMinValue);
        TextView textViewMinUnit = findViewById(R.id.textViewMinUnit);
        TextView textViewMaxTitle = findViewById(R.id.textViewMaxTitle);
        TextView textViewMaxValue = findViewById(R.id.textViewMaxValue);
        TextView textViewMaxUnit = findViewById(R.id.textViewMaxUnit);
        TextView textViewDurationTitle = findViewById(R.id.textViewDurationTitle);
        TextView textViewDuratisonValue = findViewById(R.id.textViewDurationValue);
        TextView textViewDateStartedTitle = findViewById(R.id.textViewDateStartedTitle);
        TextView textViewDateStartedValue = findViewById(R.id.textViewDateStartedValue);
        TextView textViewLastDateEnteredTitle = findViewById(R.id.textViewLastDateEnteredTitle);
        TextView textViewLastDateEnteredValue = findViewById(R.id.textViewLastDateEnteredValue);
        barChart = findViewById(R.id.chart);
        title.setText(getString(R.string.Insights));
        textViewMinTitle.setText(getString(R.string.smallest_value));
        textViewMaxTitle.setText(getString(R.string.largest_value));
        textViewAverageTitle.setText(getString(R.string.average_amount));
        textViewDurationTitle.setText(getString(R.string.no_of_days));
        // recieve habit identifier and handle nulls
        String habit = getIntent().getStringExtra(getString(R.string.habit));
        String option = getIntent().getStringExtra(getString(R.string.option));
        if (habit == null) {
            //do error handling
            Log.e(getString(R.string.passing_tag), getString(R.string.insight_habit_not_passed));
            // go back
            Intent nohabit = new Intent(insights.this, selectHabit.class);
            startActivity(nohabit);
        }
        else
        {
            Log.d(getString(R.string.passing_tag), getString(R.string.insight_gp_habit)+habit);
        }
        // back button logic
        backButton.setOnClickListener(v->{
            Intent back1 = new Intent(insights.this, Chartview.class);
            back1.putExtra(getString(R.string.habit),habit);
            startActivity(back1);
        });
        chartDBhandler dbHandler = new chartDBhandler(insights.this);
        ArrayList<chartData> raw_data = dbHandler.getDataByHabit(habit);
        String unit = raw_data.get(0).getUnit();
        textViewAverageUnit.setText(unit);
        textViewMinUnit.setText(unit);
        textViewMaxUnit.setText(unit);
        float average = calculateAverage(raw_data);
        float min = calculateMin(raw_data);
        float max = calculateMax(raw_data);


        String date_start = dbHandler.getHabitStartDate(habit);
        String last_date =  dbHandler.getHabitLastDate(habit);
        Long duration = dbHandler.getHabitDuration(habit);
        if (duration==0){
            duration= 1L;
            textViewDuratisonValue.setText(String.format(getString(R.string.duration_day), duration));
        }
        else{
            textViewDuratisonValue.setText(String.format(getString(R.string.duration_days), duration));
        }


        textViewMinValue.setText(String.format(getString(R.string.min_tag), min));
        textViewMaxValue.setText(String.format(getString(R.string.max_tag), max));
        //textViewMax.setText( String.valueOf(max));

        // Display the average value
        textViewAverageValue.setText(String.format(getString(R.string.average_tag), average));
        textViewDateStartedValue.setText(date_start);
        textViewLastDateEnteredValue.setText(last_date);





    }
    private static float calculateAverage(ArrayList<chartData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }

        float sum = 0;
        for (chartData data : dataList) {
            sum += data.getData();
        }

        return sum / dataList.size();
    }
    private float calculateMin(ArrayList<chartData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            Log.w(getString(R.string.insights_lowercase), getString(R.string.datalist_empty));
            return 0;
        }

        float min = Float.MAX_VALUE;
        for (chartData data : dataList) {
            if (data.getData() < min) {
                min = data.getData();
            }
        }

        return min;
    }

    private float calculateMax(ArrayList<chartData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            Log.w(getString(R.string.insights_lowercase), getString(R.string.datalist_empty));
            return 0;
        }

        float max = Float.MIN_VALUE;
        for (chartData data : dataList) {
            if (data.getData() > max) {
                max = data.getData();
            }
        }

        return max;
    }
    private void histogram(ArrayList<chartData> raw_data, String habit) {
        if (barChart == null) {
            Log.e(getString(R.string.histogram), getString(R.string.barchart_view));
            return;
        }

        chartDBhandler dbHandler = new chartDBhandler(insights.this);
        List<BarEntry> values = dbHandler.convertToBarEntries(raw_data);

        // Sort entries by ascending order of y-value
        Collections.sort(values, new Comparator<BarEntry>() {
            @Override
            public int compare(BarEntry entry1, BarEntry entry2) {
                return Float.compare(entry1.getY(), entry2.getY());
            }
        });

        // Log the sorted values to verify the order
        for (BarEntry entry : values) {
            Log.d(getString(R.string.histogram), getString(R.string.sorted_entry)+"x=" + entry.getX() + ", y=" + entry.getY());
        }

        // Check if entries are not empty
        if (values.isEmpty()) {
            Log.e(getString(R.string.histogram), getString(R.string.no_data_avail));
            return;
        }

        // Determine the min and max y-values
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        for (BarEntry entry : values) {
            if (entry.getY() < minY) minY = entry.getY();
            if (entry.getY() > maxY) maxY = entry.getY();
        }

        // Calculate bin width
        int numBins = 10;
        float binWidth = (maxY - minY) / numBins;

        // Initialize bins
        int[] binCounts = new int[numBins];
        for (int i = 0; i < numBins; i++) {
            binCounts[i] = 0;
        }

        // Bin the data
        for (BarEntry entry : values) {
            int binIndex = (int) ((entry.getY() - minY) / binWidth);
            if (binIndex >= numBins) binIndex = numBins - 1; // Ensure it does not exceed array bounds
            binCounts[binIndex]++;
        }

        // Create new binned BarEntry list
        List<BarEntry> binnedEntries = new ArrayList<>();
        for (int i = 0; i < numBins; i++) {
            // X value represents the bin index, Y value represents the count in that bin
            binnedEntries.add(new BarEntry(i, binCounts[i]));
        }

        BarDataSet set1 = new BarDataSet(binnedEntries, habit);
        set1.setDrawValues(true);

        // Define colors
        int baseColor = Color.parseColor("#76C7C0"); // Base pastel color
        int highestColor = Color.parseColor("#4CAF50"); // Green color for highest point
        int lowestColor = Color.parseColor("#F44336"); // Red color for lowest point

        // Find the highest and lowest counts
        BarEntry highest = null;
        BarEntry lowest = null;
        for (BarEntry entry : binnedEntries) {
            if (highest == null || entry.getY() > highest.getY()) {
                highest = entry;
            }
            if (lowest == null || entry.getY() < lowest.getY()) {
                lowest = entry;
            }
        }

        // Add custom colors for highest and lowest points
        List<Integer> colors = new ArrayList<>();
        for (BarEntry entry : binnedEntries) {
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

        barChart.setData(data);

        // Customize the chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true); // Draw values above the bars
        barChart.setPinchZoom(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(12f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(Color.DKGRAY);
        leftAxis.setTextSize(12f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.DKGRAY);
        rightAxis.setTextSize(12f);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        // Refresh the chart
        barChart.invalidate();
    }

}