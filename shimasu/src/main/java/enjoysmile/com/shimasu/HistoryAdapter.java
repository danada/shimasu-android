package enjoysmile.com.shimasu;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;

/**
 * Created by Daniel on 11/17/2016. HistoryAdapter displays history items in the main
 * OverviewActivity
 */
class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private List<History> mHistoryData;
  private ItemClickedListener mItemClickedListener;

  // provide a constructor
  HistoryAdapter(List<History> activityDataset, ItemClickedListener itemClickedListener) {
    mHistoryData = activityDataset;
    mItemClickedListener = itemClickedListener;
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    switch (viewType) {
        // TODO: Use constant for this value.
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
                  .inflate(R.layout.history_view, parent, false);
          return new ViewHolder(v);
        }
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {
    // get activity
    History history = mHistoryData.get(position);

    if (getItemViewType(position) == -1) { // date row
      SubheadingViewHolder holder = (SubheadingViewHolder) h;
      TextView subheadingLabel = holder.getSubheadingLabel();
      subheadingLabel.setText(mHistoryData.get(position).getActivity().getName());
    } else { // history row
      ViewHolder holder = (ViewHolder) h;

      // activity name
      TextView tv = holder.getTextView();
      tv.setText(history.getActivity().getName());

      TextView activityIconText = holder.getActivityIconText();
      activityIconText.setText(
          history.getActivity().getName().substring(0, 1).toUpperCase(Locale.getDefault()));

      TextView activityPointLabel = holder.getActivityPointLabel();

      // set subtitle
      TextView textViewSubtitle = holder.getTextViewSubtitle();
      textViewSubtitle.setText(
          tv.getContext()
              .getString(
                  R.string.history_row_point_amount_label,
                  history.getActivity().getDescription(),
                  history.getQuantity()));

      final int ACTIVITY_TYPE_REWARD = tv.getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
      final int ACTIVITY_TYPE_ACTIVITY =
          tv.getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);

      if (history.getActivity().getType() == ACTIVITY_TYPE_REWARD) {
        activityPointLabel.setText(
            tv.getContext().getString(R.string.reward_point_label, history.getPoints()));
        activityPointLabel.setTextColor(
            ContextCompat.getColor(tv.getContext(), R.color.colorPointDown));
        PorterDuffColorFilter activityFilter =
            new PorterDuffColorFilter(
                ContextCompat.getColor(tv.getContext(), R.color.colorAccent),
                PorterDuff.Mode.MULTIPLY);
        holder.getActivityIcon().setColorFilter(activityFilter);
      } else if (history.getActivity().getType() == ACTIVITY_TYPE_ACTIVITY) {
        activityPointLabel.setText(
            tv.getContext().getString(R.string.activity_point_label, history.getPoints()));
        activityPointLabel.setTextColor(
            ContextCompat.getColor(tv.getContext(), R.color.colorPointUp));
        PorterDuffColorFilter rewardFilter =
            new PorterDuffColorFilter(
                ContextCompat.getColor(tv.getContext(), R.color.colorPrimary),
                PorterDuff.Mode.MULTIPLY);
        holder.getActivityIcon().setColorFilter(rewardFilter);
      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    if (mHistoryData.get(position).getId().equals("-1")) {
      return -1;
    } else {
      return 0;
    }
  }

  @Override
  public int getItemCount() {
    return mHistoryData.size();
  }

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

      textView = v.findViewById(R.id.textView);
      activityIcon = v.findViewById(R.id.activity_icon);
      activityIconText = v.findViewById(R.id.activity_icon_text);
      textViewSubtitle = v.findViewById(R.id.textViewSubtitle);
      activityPointLabel = v.findViewById(R.id.activityPointLabel);
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

    private TextView getActivityIconText() {
      return activityIconText;
    }

    private TextView getTextViewSubtitle() {
      return textViewSubtitle;
    }

    private TextView getActivityPointLabel() {
      return activityPointLabel;
    }
  }

  private class SubheadingViewHolder extends RecyclerView.ViewHolder {
    private final TextView subheadingLabel;

    private SubheadingViewHolder(View v) {
      super(v);

      subheadingLabel = v.findViewById(R.id.subheadingLabel);
    }

    private TextView getSubheadingLabel() {
      return subheadingLabel;
    }
  }
}
