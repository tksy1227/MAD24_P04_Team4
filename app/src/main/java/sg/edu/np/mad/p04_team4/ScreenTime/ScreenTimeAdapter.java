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

    public ScreenTimeAdapter(Context context, List<ScreenTimeEntry> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_screen_time, parent, false);
        }

        ScreenTimeEntry entry = dataList.get(position);

        TextView featureNameTextView = convertView.findViewById(R.id.tvFeatureName);
        TextView durationTextView = convertView.findViewById(R.id.tvDuration);

        featureNameTextView.setText(entry.getFeatureName());
        durationTextView.setText(formatDuration(entry.getDuration()));

        return convertView;
    }

    private String formatDuration(long duration) {
        long seconds = duration;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
