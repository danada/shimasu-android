package enjoysmile.com.shimasu;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Daniel on 11/17/2016.
 * HistoryAdapter displays history items in the main
 * OverviewActivity
 */

class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<History> mHistoryData;
    private ItemClickedListener mItemClickedListener;

    interface ItemClickedListener {
        void onHistoryItemClicked(int position);
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textView;
        private final ImageView activityIcon;
        private final TextView activityIconText;
        private final TextView textViewSubtitle;
        private final TextView activityPointLabel;

        private ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(this);

            textView = (TextView) v.findViewById(R.id.textView);
            activityIcon = (ImageView) v.findViewById(R.id.activity_icon);
            activityIconText = (TextView) v.findViewById(R.id.activity_icon_text);
            textViewSubtitle = (TextView) v.findViewById(R.id.textViewSubtitle);
            activityPointLabel = (TextView) v.findViewById(R.id.activityPointLabel);
        }

        @Override
        public void onClick(View v) {
            mItemClickedListener.onHistoryItemClicked(getAdapterPosition());
        }

        private TextView getTextView() {
            return textView;
        }

        private ImageView getActivityIcon() {
            return activityIcon;
        }

        private TextView getActivityIconText() { return activityIconText; }

        private TextView getTextViewSubtitle() { return textViewSubtitle; }

        private TextView getActivityPointLabel() { return activityPointLabel; }
    }

    private class SubheadingViewHolder extends RecyclerView.ViewHolder {
        private final TextView subheadingLabel;

        private SubheadingViewHolder(View v) {
            super(v);

            subheadingLabel = (TextView) v.findViewById(R.id.subheadingLabel);
        }

        private TextView getSubheadingLabel() { return subheadingLabel; }
    }

    //provide a constructor
    HistoryAdapter(List<History> activityDataset, ItemClickedListener itemClickedListener) {
        mHistoryData = activityDataset;
        mItemClickedListener = itemClickedListener;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case -1: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_subheading, parent, false);
                return new SubheadingViewHolder(v);
            }
            default: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view, parent, false);
                return new ViewHolder(v);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
        // get activity
        History _history = mHistoryData.get(position);

        if (getItemViewType(position) == -1) { // date row
            SubheadingViewHolder holder = (SubheadingViewHolder) h;
            TextView subheadingLabel = holder.getSubheadingLabel();
            subheadingLabel.setText(mHistoryData.get(position).getActivity().name);
        } else { // history row
            ViewHolder holder = (ViewHolder) h;

            // activity name
            TextView tv = holder.getTextView();
            tv.setText(_history.getActivity().name);

            TextView activityIconText = holder.getActivityIconText();
            activityIconText.setText(_history.getActivity().name.substring(0, 1).toUpperCase());

            TextView activityPointLabel = holder.getActivityPointLabel();

            // set subtitle
            TextView textViewSubtitle = holder.getTextViewSubtitle();
            textViewSubtitle.setText(_history.getActivity().description +
                    " × " +
                    _history.getQuantity());

            final int ACTIVITY_TYPE_REWARD = tv.getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
            final int ACTIVITY_TYPE_ACTIVITY = tv.getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);

            if (_history.getActivity().type == ACTIVITY_TYPE_REWARD) {
                activityPointLabel.setText(tv.getContext().getString(R.string.activity_point_label, _history.getPoints()));
                activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointDown));
                PorterDuffColorFilter activityFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                holder.getActivityIcon().setColorFilter(activityFilter);
            } else if (_history.getActivity().type == ACTIVITY_TYPE_ACTIVITY) {
                activityPointLabel.setText(tv.getContext().getString(R.string.reward_point_label, _history.getPoints()));
                activityPointLabel.setTextColor(ContextCompat.getColor(tv.getContext(), R.color.colorPointUp));
                PorterDuffColorFilter rewardFilter = new PorterDuffColorFilter(ContextCompat.getColor(tv.getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                holder.getActivityIcon().setColorFilter(rewardFilter);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHistoryData.get(position).getActivity().id == -1) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return mHistoryData.size();
    }
}
