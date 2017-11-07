package enjoysmile.com.shimasu;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

/*
 * Copyright (c) 2017 enjoy|smile. All Rights Reserved.
 */
class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private List<Activity> mActivities;

  // constructor
  ActivityAdapter(List<Activity> activities) {
    mActivities = activities;

    buildHeaders();
  }

  private void buildHeaders() {
    if (mActivities.size() > 0) {
      // add type divider
      for (int i = 0; i < mActivities.size(); i++) {
        Activity activityTypeHeader = new Activity();
        activityTypeHeader.setId(-1);
        activityTypeHeader.setType(mActivities.get(i).getType());

        // first header
        if (i == 0) {
          // add reward header
          mActivities.add(0, activityTypeHeader);
        } else {
          // second header (if necessary)
          if (i < mActivities.size()
              && // if there's another activity after this
              mActivities.get(i - 1).getId() != -1
              && mActivities.get(i).getId() != -1
              && // if the last or current row not header
              (mActivities.get(i).getType()
                  != mActivities
                      .get(i - 1)
                      .getType())) { // if this type doesn't equal previous type
            // add reward header
            mActivities.add(i, activityTypeHeader);
          }
        }
      }
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
      case -1:
        {
          View v =
              LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.list_subheading, parent, false);
          return new SubheadingViewHolder(v);
        }
      default:
        {
          View v =
              LayoutInflater.from(parent.getContext())
                  .inflate(R.layout.activity_view, parent, false);
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
      String[] activity_types =
          subheadingLabel.getResources().getStringArray(R.array.activity_types);
      subheadingLabel.setText(activity_types[_activity.getType()]);
    } else { // activity row
      ViewHolder holder = (ViewHolder) h;
      // activity label
      TextView activityLabel = holder.getActivityLabel();
      activityLabel.setText(_activity.getName());

      // activity subtitle
      TextView activitySubtitle = holder.getActivitySubtitle();
      activitySubtitle.setText(_activity.getDescription());

      // activity points
      TextView activityPointLabel = holder.getActivityPointLabel();

      final int ACTIVITY_TYPE_REWARD =
          activityPointLabel.getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
      final int ACTIVITY_TYPE_ACTIVITY =
          activityPointLabel.getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);

      if (_activity.getType() == ACTIVITY_TYPE_REWARD) {
        activityPointLabel.setText(
            activityPointLabel
                .getContext()
                .getString(R.string.reward_point_label, _activity.getPoints()));
        activityPointLabel.setTextColor(
            ContextCompat.getColor(activityLabel.getContext(), R.color.colorPointDown));
      } else if (_activity.getType() == ACTIVITY_TYPE_ACTIVITY) {
        activityPointLabel.setText(
            activityPointLabel
                .getContext()
                .getString(R.string.activity_point_label, _activity.getPoints()));
        activityPointLabel.setTextColor(
            ContextCompat.getColor(activityLabel.getContext(), R.color.colorPointUp));
      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (mActivities.get(position).getId() == -1) {
      return -1;
    } else {
      return 0;
    }
  }

  @Override
  public int getItemCount() {
    return mActivities.size();
  }

  void updateAdapter(List<Activity> activities) {
    this.mActivities = activities;

    buildHeaders();
    notifyDataSetChanged();
  }

  private class ViewHolder extends RecyclerView.ViewHolder {
    private final TextView activityLabel;
    private final TextView activitySubtitle;
    private final TextView activityPointLabel;

    ViewHolder(View v) {
      super(v);

      // Define click listener for the ViewHolder's View.
      v.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // start edit intent
              Intent intent = new Intent(v.getContext(), ActivityAddActivity.class);
              // set edit mode flag
              intent.putExtra("edit_mode", true);
              // set activity id
              intent.putExtra("activity_id", mActivities.get(getAdapterPosition()).getId());
              // start the activity
              ((android.app.Activity) v.getContext())
                  .startActivityForResult(
                      intent,
                      v.getContext().getResources().getInteger(R.integer.EDIT_ACTIVITY_REQUEST));
            }
          });

      activityLabel = v.findViewById(R.id.activityLabel);
      activitySubtitle = v.findViewById(R.id.activitySubtitle);
      activityPointLabel = v.findViewById(R.id.activityPointLabel);
    }

    TextView getActivityLabel() {
      return activityLabel;
    }

    TextView getActivitySubtitle() {
      return activitySubtitle;
    }

    TextView getActivityPointLabel() {
      return activityPointLabel;
    }
  }

  private class SubheadingViewHolder extends RecyclerView.ViewHolder {
    private final TextView subheadingLabel;

    SubheadingViewHolder(View v) {
      super(v);

      subheadingLabel = v.findViewById(R.id.subheadingLabel);
    }

    TextView getSubheadingLabel() {
      return subheadingLabel;
    }
  }
}
