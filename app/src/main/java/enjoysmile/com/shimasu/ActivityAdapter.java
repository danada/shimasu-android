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

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    private List<Activity> mActivityDataset;

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
                    Log.d("ACTIVITY_ADAPTER", "Element " + getAdapterPosition() + " clicked.");
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
    public ActivityAdapter(List<Activity> activityDataset) {
        mActivityDataset = activityDataset;
    }

    // create new views (used by layout manager)
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view, parent, false);

        // set margins, etc
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get activity
        Activity _activity = mActivityDataset.get(position);

        // activity name
        TextView tv = holder.getTextView();
        tv.setText(_activity.name);

        TextView activityIconText = holder.getActivityIconText();
        activityIconText.setText(_activity.name.substring(0, 1).toUpperCase());

        TextView activityPointLabel = holder.getActivityPointLabel();

        // set subtitle
        TextView textViewSubtitle = holder.getTextViewSubtitle();
        switch (_activity.name) {
            case "Beer":
                textViewSubtitle.setText("One pint × 2");
                activityPointLabel.setText("▼ 2");
                activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointDown));
                break;
            case "Push Ups":
                textViewSubtitle.setText("10 times × 3");
                activityPointLabel.setText("▲ 3");
                activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointUp));

                break;
            case "Sit Ups":
                textViewSubtitle.setText("10 times × 2");
                activityPointLabel.setText("▲ 2");
                activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointUp));

                break;
            default:
                textViewSubtitle.setText("During class");
                activityPointLabel.setText("▼ 1");
                activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointDown));

                break;
        }

        if (_activity.name.equals("Sit Ups") || _activity.name.equals("Push Ups")) {
            PorterDuffColorFilter rewardFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.getActivityIcon().setColorFilter(rewardFilter);
        } else {
            PorterDuffColorFilter activityFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            holder.getActivityIcon().setColorFilter(activityFilter);
        }
    }

    @Override
    public int getItemCount() {
        return mActivityDataset.size();
    }
}
