package sg.edu.np.mad.p04_team4.Timer;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import sg.edu.np.mad.p04_team4.R;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private TimerAdapter mAdapter;
    private View backgroundView;

    public SwipeToDeleteCallback(TimerAdapter adapter) {
        super(0, ItemTouchHelper.LEFT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
        // Show confirmation dialog
        new AlertDialog.Builder(viewHolder.itemView.getContext())
                .setTitle("Delete Timer Log")
                .setMessage("Are you sure you want to delete this timer log?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Delete the item
                    int position = viewHolder.getAdapterPosition();
                    mAdapter.deleteItem(position);
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    // Revert the swiped item
                    mAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;

        if (dX < 0) { // Swiping to the left
            if (backgroundView == null) {
                backgroundView = LayoutInflater.from(recyclerView.getContext())
                        .inflate(R.layout.item_swipe_background, recyclerView, false);
            }

            // Measure and layout the background view
            backgroundView.measure(
                    View.MeasureSpec.makeMeasureSpec(itemView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(itemView.getHeight(), View.MeasureSpec.EXACTLY)
            );
            backgroundView.layout(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());

            // Draw the background view
            c.save();
            c.translate(itemView.getRight() + (int) dX, itemView.getTop());
            backgroundView.draw(c);
            c.restore();
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}

