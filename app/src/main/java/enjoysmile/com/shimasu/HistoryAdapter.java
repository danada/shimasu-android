package enjoysmile.com.shimasu;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniel on 11/17/2016.
 */

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<History> mActivityDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView activityIcon;
        private final TextView activityIconText;
        private final TextView textViewSubtitle;
        private final TextView activityPointLabel;

        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("HISTORY_ADAPTER", "Element " + getAdapterPosition() + " clicked.");
                }
            });

            textView = (TextView) v.findViewById(R.id.textView);
            activityIcon = (ImageView) v.findViewById(R.id.activity_icon);
            activityIconText = (TextView) v.findViewById(R.id.activity_icon_text);
            textViewSubtitle = (TextView) v.findViewById(R.id.textViewSubtitle);
            activityPointLabel = (TextView) v.findViewById(R.id.activityPointLabel);
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getActivityIcon() {
            return activityIcon;
        }

        public TextView getActivityIconText() { return activityIconText; }

        public TextView getTextViewSubtitle() { return textViewSubtitle; }

        public TextView getActivityPointLabel() { return activityPointLabel; }
    }

    //provide a constructor
    public HistoryAdapter(List<History> activityDataset) {
        mActivityDataset = activityDataset;
    }

    // create new views (used by layout manager)
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view, parent, false);

        // set margins, etc
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get activity
        History _history = mActivityDataset.get(position);

        // activity name
        TextView tv = holder.getTextView();
        tv.setText(_history.activity.name);

        TextView activityIconText = holder.getActivityIconText();
        activityIconText.setText(_history.activity.name.substring(0, 1).toUpperCase());

        TextView activityPointLabel = holder.getActivityPointLabel();



        // set subtitle
        TextView textViewSubtitle = holder.getTextViewSubtitle();
        textViewSubtitle.setText(_history.activity.description +
                " × " +
                _history.quantity);

        final int ACTIVITY_TYPE_REWARD = tv.getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
        final int ACTIVITY_TYPE_ACTIVITY = tv.getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);

        if (_history.activity.type == ACTIVITY_TYPE_REWARD) {
            activityPointLabel.setText("▼ " + _history.points);
            activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointDown));
            PorterDuffColorFilter activityFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            holder.getActivityIcon().setColorFilter(activityFilter);
        } else if (_history.activity.type == ACTIVITY_TYPE_ACTIVITY) {
            activityPointLabel.setText("▲ " + _history.points);
            activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointUp));
            PorterDuffColorFilter rewardFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.getActivityIcon().setColorFilter(rewardFilter);
        }
    }

    @Override
    public int getItemCount() {
        return mActivityDataset.size();
    }
}
