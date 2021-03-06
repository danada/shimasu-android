package enjoysmile.com.shimasu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/*
 * Copyright (c) 2017 enjoy|smile. All Rights Reserved.
 */
public class FragmentActivityLog extends DialogFragment
    implements AdapterView.OnItemSelectedListener, View.OnClickListener {
  private AlertDialog mDialog;
  private View mDialogView;
  private RealmList<Activity> mActivityData;
  private History mHistoryToAdd;
  private User mUser;
  private Realm realm;
  private ActivityLoggedListener mActivityLoggedListener;

  public static FragmentActivityLog newInstance(
      int activityIndex, ActivityLoggedListener activityLoggedListener) {
    // TODO - pass activity type to determine which types to show
    FragmentActivityLog logActivityFragment = new FragmentActivityLog();
    Bundle args = new Bundle();
    args.putInt("activityIndex", activityIndex);
    args.putParcelable("loggedListener", activityLoggedListener);
    logActivityFragment.setArguments(args);
    return logActivityFragment;
  }

  @Override
  @NonNull
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    realm = Realm.getDefaultInstance();

    // bind listener
    mActivityLoggedListener = getArguments().getParcelable("loggedListener");

    // initialize user
    mUser = initializeUser();

    // get activities
    RealmResults<Activity> activityResult =
        realm
            .where(Activity.class)
            .equalTo("deleted", false)
            .sort("type", Sort.ASCENDING)
            .findAll();
    mActivityData = new RealmList<>();
    mActivityData.addAll(activityResult);

    // build the mActivityData array
    List<String> activityNames = new ArrayList<>();
    for (Activity activity : mActivityData) {
      activityNames.add(activity.getName());
    }

    // build history object
    mHistoryToAdd = new History();
    mHistoryToAdd.setId(UUID.randomUUID().toString());
    mHistoryToAdd.setActivity(mActivityData.get(getArguments().getInt("activityIndex")));
    mHistoryToAdd.setQuantity(1);
    mHistoryToAdd.setDate(System.currentTimeMillis());
    mHistoryToAdd.setPoints(mActivityData.get(getArguments().getInt("activityIndex")).getPoints());

    // build the view
    mDialogView =
        View.inflate(
            getActivity().getApplicationContext(), R.layout.dialog_fragment_add_activity, null);

    // build the dialog
    mDialog =
        new AlertDialog.Builder(getActivity())
            .setView(mDialogView)
            .setTitle(getString(R.string.dialog_fragment_add_activity_title))
            .setPositiveButton(
                getString(R.string.dialog_fragment_add_activity_log_button),
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int whichButton) {
                    // set the time
                    mHistoryToAdd.setDate(System.currentTimeMillis());

                    // copy to realm
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(mHistoryToAdd);
                    realm.copyToRealmOrUpdate(mUser);
                    realm.commitTransaction();

                    mActivityLoggedListener.onActivityLogged(mHistoryToAdd);

                    realm.close();
                    dialog.dismiss();
                  }
                })
            .setNegativeButton(
                getString(R.string.dialog_fragment_add_activity_cancel_button),
                new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int whichButton) {
                    realm.close();
                    dialog.dismiss();
                  }
                })
            .create();

    // build amount buttons
    Button amountDecrease = mDialogView.findViewById(R.id.add_activity_amount_decrease);
    Button amountIncrease = mDialogView.findViewById(R.id.add_activity_amount_increase);
    amountDecrease.setOnClickListener(this);
    amountIncrease.setOnClickListener(this);

    // set the quantity
    TextView amountTextView = mDialogView.findViewById(R.id.add_activity_amount_label);
    amountTextView.setText(String.format(Locale.getDefault(), "%d", mHistoryToAdd.getQuantity()));

    // build activity spinner
    Spinner spinner = mDialogView.findViewById(R.id.add_activity_spinner);
    ArrayAdapter<String> spinnerArrayAdapter =
        new ArrayAdapter<>(
            getActivity(),
            android.R.layout.simple_spinner_item,
            activityNames.toArray(
                new String[0])); // selected item will look like a spinner set from XML
    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(spinnerArrayAdapter);
    spinner.setSelection(getArguments().getInt("activityIndex")); // set default selected index
    spinner.setOnItemSelectedListener(this);

    return mDialog;
  }

  @Override
  public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
    mHistoryToAdd.setActivity(mActivityData.get(i));
    mHistoryToAdd.setPoints(mHistoryToAdd.getQuantity() * mHistoryToAdd.getActivity().getPoints());
    // update points label
    final int ACTIVITY_TYPE_REWARD = getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
    final int ACTIVITY_TYPE_ACTIVITY = getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);
    TextView pointTotalLabel = mDialogView.findViewById(R.id.add_activity_point_total_label);

    if (mHistoryToAdd.getActivity().getType() == ACTIVITY_TYPE_REWARD) {
      pointTotalLabel.setText(getString(R.string.reward_point_label, mHistoryToAdd.getPoints()));
      pointTotalLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPointDown));
    } else if (mHistoryToAdd.getActivity().getType() == ACTIVITY_TYPE_ACTIVITY) {
      pointTotalLabel.setText(getString(R.string.activity_point_label, mHistoryToAdd.getPoints()));
      pointTotalLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPointUp));
    }

    // disable log button if not enough points
    if (mHistoryToAdd.getPoints() > mUser.getPoints()
        && mHistoryToAdd.getActivity().getType() == ACTIVITY_TYPE_REWARD) {
      mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    } else {
      mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> adapterView) {}

  @Override
  public void onClick(View view) {
    // update history quantity
    switch (view.getId()) {
      case R.id.add_activity_amount_decrease:
        if (mHistoryToAdd.getQuantity() - 1 > 0) {
          mHistoryToAdd.setQuantity(mHistoryToAdd.getQuantity() - 1);
        }
        break;
      case R.id.add_activity_amount_increase:
        mHistoryToAdd.setQuantity(mHistoryToAdd.getQuantity() + 1);
        break;
    }

    // recalculate points
    mHistoryToAdd.setPoints(mHistoryToAdd.getQuantity() * mHistoryToAdd.getActivity().getPoints());

    // update amount label
    TextView amountLabel = mDialogView.findViewById(R.id.add_activity_amount_label);
    amountLabel.setText(String.format(Locale.getDefault(), "%d", mHistoryToAdd.getQuantity()));

    // update points label
    final int ACTIVITY_TYPE_REWARD = getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
    final int ACTIVITY_TYPE_ACTIVITY = getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);
    TextView pointTotalLabel = mDialogView.findViewById(R.id.add_activity_point_total_label);

    if (mHistoryToAdd.getActivity().getType() == ACTIVITY_TYPE_REWARD) {
      pointTotalLabel.setText(getString(R.string.reward_point_label, mHistoryToAdd.getPoints()));
      pointTotalLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPointDown));
    } else if (mHistoryToAdd.getActivity().getType() == ACTIVITY_TYPE_ACTIVITY) {
      pointTotalLabel.setText(getString(R.string.activity_point_label, mHistoryToAdd.getPoints()));
      pointTotalLabel.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPointUp));
    }

    // disable log button if not enough points
    if (mHistoryToAdd.getPoints() > mUser.getPoints()
        && mHistoryToAdd.getActivity().getType() == ACTIVITY_TYPE_REWARD) {
      mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    } else {
      mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
    }
  }

  protected User initializeUser() {
    // get mActivityData
    RealmResults<User> userResults = realm.where(User.class).findAll();
    if (userResults.size() < 1) {
      // make a new user
      realm.beginTransaction();
      User newUser = new User();
      newUser.setId(UUID.randomUUID().toString());
      newUser.setPoints(0);
      realm.copyToRealmOrUpdate(newUser);
      realm.commitTransaction();

      return newUser;
    } else {
      // return our current user
      return userResults.first();
    }
  }

  interface ActivityLoggedListener extends Parcelable {
    void onActivityLogged(History historyToAdd);
  }
}
