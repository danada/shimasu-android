package enjoysmile.com.shimasu;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniel on 12/14/2016.
 */

public class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Activity> mActivities;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView activityLabel;
        private final TextView activitySubtitle;
        private final TextView activityPointLabel;

        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // start edit intent
                    Intent intent = new Intent(v.getContext(), ActivityAddActivity.class);
                    // set edit mode flag
                    intent.putExtra("edit_mode", true);
                    // set activity id
                    intent.putExtra("activity_id", mActivities.get(getAdapterPosition()).id);
                    // start the activity
                    ((android.app.Activity) v.getContext()).startActivityForResult(intent,
                            v.getContext().getResources().getInteger(R.integer.EDIT_ACTIVITY_REQUEST));
                }
            });

            activityLabel = (TextView) v.findViewById(R.id.activityLabel);
            activitySubtitle = (TextView) v.findViewById(R.id.activitySubtitle);
            activityPointLabel = (TextView) v.findViewById(R.id.activityPointLabel);
        }

        public TextView getActivityLabel() { return activityLabel; }
        public TextView getActivitySubtitle() { return activitySubtitle; }
        public TextView getActivityPointLabel() { return activityPointLabel; }
    }

    public class SubheadingViewHolder extends RecyclerView.ViewHolder {
        private final TextView subheadingLabel;

        public SubheadingViewHolder(View v) {
            super(v);

            subheadingLabel = (TextView) v.findViewById(R.id.subheadingLabel);
        }

        public TextView getSubheadingLabel() { return subheadingLabel; }
    }

    // constructor
    public ActivityAdapter(List<Activity> activities) {
        mActivities = activities;

        buildHeaders();
    }

    private void buildHeaders() {
        if (mActivities.size() > 0) {
            // add type divider
            for (int i = 0; i < mActivities.size(); i++) {
                // first header
                if (i == 0) {
                    // add reward header
                    mActivities.add(0, new Activity(-1,
                            null,
                            "",
                            mActivities.get(i).type,
                            0,
                            false));
                } else {
                    // second header (if necessary)
                    if (i < mActivities.size() && // if there's another activity after this
                            mActivities.get(i - 1).id != -1 && mActivities.get(i).id != -1 && // if the last or current row not header
                            (mActivities.get(i).type != mActivities.get(i - 1).type)) { // if this type doesn't equal previous type
                        // add reward header
                        mActivities.add(i, new Activity(-1,
                                null,
                                "",
                                mActivities.get(i).type,
                                0,
                                false));
                    }
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case -1: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subheading, parent, false);
                return new SubheadingViewHolder(v);
            }
            default: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_view, parent, false);
                return new ViewHolder(v);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        //get activity
        Activity _activity = mActivities.get(position);

        if (getItemViewType(position) == -1) { // type row
            SubheadingViewHolder holder = (SubheadingViewHolder) h;
            TextView subheadingLabel = holder.getSubheadingLabel();
            // get activity type string from ID
            String[] activity_types = subheadingLabel.getResources().getStringArray(R.array.activity_types);
            subheadingLabel.setText(activity_types[_activity.type]);
        } else { // activity row
            ViewHolder holder = (ViewHolder) h;
            // activity label
            TextView activityLabel = holder.getActivityLabel();
            activityLabel.setText(_activity.name);

            // activity subtitle
            TextView activitySubtitle = holder.getActivitySubtitle();
            activitySubtitle.setText(_activity.description);

            // activity points
            TextView activityPointLabel = holder.getActivityPointLabel();

            final int ACTIVITY_TYPE_REWARD = activityPointLabel.getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
            final int ACTIVITY_TYPE_ACTIVITY = activityPointLabel.getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);

            if (_activity.type == ACTIVITY_TYPE_REWARD) {
                activityPointLabel.setText("▼ " + _activity.points);
                activityPointLabel.setTextColor(ContextCompat.getColor(activityLabel.getContext(), R.color.colorPointDown));
            } else if (_activity.type == ACTIVITY_TYPE_ACTIVITY) {
                activityPointLabel.setText("▲ " + _activity.points);
                activityPointLabel.setTextColor(ContextCompat.getColor(activityLabel.getContext(), R.color.colorPointUp));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mActivities.get(position).id == -1) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() { return mActivities.size(); }

    public void updateAdapter(List<Activity> activities) {
        this.mActivities = activities;

        buildHeaders();
        notifyDataSetChanged();
    }
}
