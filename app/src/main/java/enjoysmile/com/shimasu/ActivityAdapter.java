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

/**
 * Created by Daniel on 11/17/2016.
 */

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    private String[] mActivityDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView activityIcon;

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
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getActivityIcon() {
            return activityIcon;
        }
    }

    //provide a constructor
    public ActivityAdapter(String[] activityDataset) {
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
        // get element from dataset at this position
        // replace contents
        TextView tv = holder.getTextView();
        tv.setText(mActivityDataset[position]);

        if (mActivityDataset[position].equals("Beer") || position % 2 == 0) {
            PorterDuffColorFilter rewardFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.getActivityIcon().setColorFilter(rewardFilter);
        } else {
            PorterDuffColorFilter activityFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
            holder.getActivityIcon().setColorFilter(activityFilter);
        }
    }

    @Override
    public int getItemCount() {
        return mActivityDataset.length;
    }
}
