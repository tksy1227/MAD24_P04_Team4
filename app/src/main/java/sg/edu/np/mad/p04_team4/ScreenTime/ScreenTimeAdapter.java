package sg.edu.np.mad.p04_team4.ScreenTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class ScreenTimeAdapter extends BaseAdapter {

    private Context context;
    private List<ScreenTimeEntry> dataList;

    // Constructor to initialize the adapter with context and data list
    public ScreenTimeAdapter(Context context, List<ScreenTimeEntry> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    // Return the number of items in the data list
    @Override
    public int getCount() {
        return dataList.size();
    }

    // Return the item at the specified position
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    // Return the item ID for the specified position
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Create and return the view for each item in the data list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout for the list item if not already inflated
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_screen_time, parent, false);
        }

        // Get the current screen time entry
        ScreenTimeEntry entry = dataList.get(position);

        // Get references to the TextViews in the layout
        TextView featureNameTextView = convertView.findViewById(R.id.tvFeatureName);
        TextView durationTextView = convertView.findViewById(R.id.tvDuration);

        // Set the feature name and formatted duration in the TextViews
        featureNameTextView.setText(entry.getFeatureName());
        durationTextView.setText(formatDuration(entry.getDuration()));

        return convertView;
    }

    // Helper method to format the duration from seconds to HH:mm:ss
    private String formatDuration(long duration) {
        long seconds = duration;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
