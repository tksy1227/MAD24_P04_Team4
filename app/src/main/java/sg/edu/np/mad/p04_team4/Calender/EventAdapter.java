package sg.edu.np.mad.p04_team4.Calender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import sg.edu.np.mad.p04_team4.R;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;
    private AppDatabase db;
    private Runnable reloadEvents;
    private Drawable deleteIcon;
    private ColorDrawable background;
    private Context context;

    public EventAdapter(List<Event> eventList, AppDatabase db, Runnable reloadEvents, RecyclerView recyclerView) {
        this.eventList = eventList;
        this.db = db;
        this.reloadEvents = reloadEvents;
        this.context = recyclerView.getContext();

        deleteIcon = ContextCompat.getDrawable(recyclerView.getContext(), android.R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                showDeleteDialog(viewHolder.getAdapterPosition(), viewHolder.itemView);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = itemView.getBottom() - iconMargin;

                if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
                }

                background.draw(c);
                deleteIcon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.title.setText(event.getTitle());
        holder.date.setText(formatDateRange(event.getStartTime(), event.getEndTime()));
        holder.startTime.setText(formatTime(event.getStartTime()));
        holder.endTime.setText(formatTime(event.getEndTime()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEventActivity.class);
            intent.putExtra("eventId", event.getId());
            intent.putExtra("selectedDate", event.getStartTime());
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void showDeleteDialog(int position, View view) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("Delete Event")
                .setMessage("Would you like to delete this event?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new DeleteEventTask(view.getContext(), position, view).execute(eventList.get(position));
                })
                .setNegativeButton("No", (dialog, which) -> {
                    notifyItemChanged(position); // Reset swipe position
                })
                .show();
    }

    private void deleteEvent(int position, View view) {
        Event event = eventList.get(position);
        db.scheduleDao().delete(event);
        eventList.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(view.getContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
    }

    private String formatDateRange(long startTimeInMillis, long endTimeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(startTimeInMillis) + " - " + sdf.format(endTimeInMillis);
    }

    private String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timeInMillis);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView date;
        public TextView startTime;
        public TextView endTime;

        public EventViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.eventTitle);
            date = itemView.findViewById(R.id.eventDate);
            startTime = itemView.findViewById(R.id.eventStartTime);
            endTime = itemView.findViewById(R.id.eventEndTime);
        }
    }

    private class DeleteEventTask extends AsyncTask<Event, Void, Void> {
        private Context context;
        private int position;
        private View view;

        DeleteEventTask(Context context, int position, View view) {
            this.context = context;
            this.position = position;
            this.view = view;
        }

        @Override
        protected Void doInBackground(Event... events) {
            db.scheduleDao().delete(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            eventList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Event deleted successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
