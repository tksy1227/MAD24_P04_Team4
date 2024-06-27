package sg.edu.np.mad.p04_team4;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class TimerViewHolder extends RecyclerView.ViewHolder {
    TextView dateText;
    TextView timerText;
    TextView purposeText;
    public TimerViewHolder(View itemView) {
        super(itemView);
        dateText = itemView.findViewById(R.id.dateText);
        timerText = itemView.findViewById(R.id.timerText);
        purposeText = itemView.findViewById(R.id.purposeText);
    }
}



