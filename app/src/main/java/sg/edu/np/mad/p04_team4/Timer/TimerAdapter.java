package sg.edu.np.mad.p04_team4.Timer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sg.edu.np.mad.p04_team4.R;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.TimerViewHolder> {

    private List<Time> timerList;
    private Context context;

    public TimerAdapter(List<Time> timerList, Context context) {
        this.timerList = timerList;
        this.context = context;
    }

    @NonNull
    @Override
    public TimerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_activity_list_timer, parent, false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerViewHolder holder, int position) {
        Time timer = timerList.get(position);
        holder.timerText.setText(timer.getTimer());
        holder.purposeText.setText(timer.getPurpose());
        holder.dateText.setText(timer.getDate());
    }

    @Override
    public int getItemCount() {
        return timerList.size();
    }

    public long getItemId(int position) {
        return timerList.get(position).getId();
    }

    public void deleteItem(int position) {
        timerList.remove(position);
        notifyItemRemoved(position);
        // Optionally, notify the database or any other data source here
    }

    public void removeItem(int position) {
        timerList.remove(position);
        notifyItemRemoved(position);
    }

    public void setTimers(List<Time> timers) {
        this.timerList = timers;
        notifyDataSetChanged();
    }

    public void clearAllItems() {
        timerList.clear(); // Change mTimerLogs to timerList
        notifyDataSetChanged();
    }

    public static class TimerViewHolder extends RecyclerView.ViewHolder {

        TextView timerText;
        TextView purposeText;
        TextView dateText;

        public TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            timerText = itemView.findViewById(R.id.timerText);
            purposeText = itemView.findViewById(R.id.purposeText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}