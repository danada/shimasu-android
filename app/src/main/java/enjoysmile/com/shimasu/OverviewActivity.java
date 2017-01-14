package enjoysmile.com.shimasu;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class OverviewActivity extends AppCompatActivity implements HistoryAdapter.ItemClickedListener {

    private HistoryAdapter mActivityAdapter;

    private RealmList<History> historyData;
    private RealmList<Activity> activities;

    private User mUser;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // recycler view
        final RecyclerView mActivityRecyclerView = (RecyclerView) findViewById(R.id.activity_recycler_view);

        // use a linear layout manager
        final RecyclerView.LayoutManager mActivityLayoutManager = new LinearLayoutManager(this);
        mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

        // open realm
        Realm.init(getApplicationContext());
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        realm = Realm.getInstance(config);
        realm = Realm.getDefaultInstance();

        // initiate user
        mUser = initializeUser();

        // get activities
        RealmResults<Activity> activityResult = realm.where(Activity.class).findAllSorted("type", Sort.ASCENDING);
        activities = new RealmList<>();
        activities.addAll(activityResult.subList(0, activityResult.size()));

        // get history
        RealmResults<History> historyResult = realm.where(History.class).findAllSorted("date", Sort.DESCENDING);
        historyData = new RealmList<>();
        historyData.addAll(historyResult.subList(0, historyResult.size()));
        buildHistoryHeaders();
        mActivityAdapter = new HistoryAdapter(historyData, this);
        mActivityRecyclerView.setAdapter(mActivityAdapter);

        // update the toolbar points
        TextView pointTotal = (TextView) findViewById(R.id.point_total);
        TextView pointSubtitle = (TextView) findViewById(R.id.point_subtitle);
        pointTotal.setText(String.format(Locale.getDefault(), "%d", mUser.getPoints()));
        pointSubtitle.setText(mUser.getPoints() == 1 ? getString(R.string.point) : getString(R.string.point_plural));


        // build floating action menu
        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) findViewById(R.id.multiple_actions);

        for (int i = 0; i < activities.size(); i++) {
            FloatingActionButton _fab = new FloatingActionButton(getBaseContext()); // new button
            _fab.setLabelText(activities.get(i).name);
            _fab.setButtonSize(FloatingActionButton.SIZE_MINI);

            if (activities.get(i).type == getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD)) {
                _fab.setColorNormalResId(R.color.colorAccent);
                _fab.setColorPressedResId(R.color.colorAccentDark);
            } else {
                _fab.setColorNormalResId(R.color.colorPrimary);
                _fab.setColorPressedResId(R.color.colorPrimaryDark);
            }

            // set the button drawable
            _fab.setImageDrawable(makeLetterDrawable(activities.get(i).name.substring(0, 1)));


            // hang onto this activity's index
            final int selectedIndex = i;

            // set the onclick listener
            _fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OverviewActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_fragment_add_activity, menuMultipleActions, false);

                    // TODO - get appropriate activities
                    final List<Activity> availableActivities = activities;

                    // build the activities array
                    String[] activityArray = new String[availableActivities.size()];
                    for (int i = 0; i < availableActivities.size(); i++) {
                        activityArray[i] = availableActivities.get(i).name;
                    }

                    // build new history object
                    final History _historyToAdd = new History();
                    _historyToAdd.setId(UUID.randomUUID().toString());
                    _historyToAdd.setActivity(activities.get(selectedIndex));
                    _historyToAdd.setQuantity(1);
                    _historyToAdd.setDate(System.currentTimeMillis());
                    _historyToAdd.setPoints(activities.get(selectedIndex).points);




                    builder.setView(dialogView)
                            .setTitle("Log Activity")
                            .setPositiveButton("Log", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    // set the time
                                    _historyToAdd.setDate(System.currentTimeMillis());

                                    // copy to realm
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(_historyToAdd);
                                    realm.copyToRealmOrUpdate(mUser);
                                    realm.commitTransaction();

                                    // update points label
                                    int difference = _historyToAdd.getPoints() *
                                            (_historyToAdd.getActivity().type ==
                                                    getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD) ? -1 : 1);
                                    updateUserPoints(difference);


                                    // if first item
                                    if (historyData.size() == 0) {
                                        // get current activity date string
                                        String _dateString = DateUtils.getRelativeTimeSpanString(
                                                _historyToAdd.getDate(),
                                                System.currentTimeMillis(),
                                                DateUtils.DAY_IN_MILLIS).toString();

                                        // build activity (holds date)
                                        Activity _a = new Activity(
                                                -1,
                                                _dateString,
                                                "",
                                                -1,
                                                0,
                                                false);
                                        // build history object (notifies adapter of date row)
                                        History _h = new History();
                                        _h.setId("-1");
                                        _h.setActivity(_a);
                                        _h.setQuantity(0);
                                        _h.setDate(0);
                                        _h.setPoints(0);

                                        // add date header
                                        mActivityAdapter.insertHistoryItem(_h);
                                    }

                                    // update adapter
                                    mActivityAdapter.insertHistoryItem(_historyToAdd);

                                    // close dialog
                                    dialog.dismiss();

                                    // close the floating action menu
                                    menuMultipleActions.close(true);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });

                    // show the dialog
                    final AlertDialog dialog = builder.create();

                    View.OnClickListener amountButtonListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // update history quantity
                            switch(view.getId()) {
                                case R.id.add_activity_amount_decrease:
                                    if (_historyToAdd.getQuantity() - 1 > 0) {
                                        _historyToAdd.setQuantity(_historyToAdd.getQuantity() - 1);
                                    }
                                    break;
                                case R.id.add_activity_amount_increase:
                                    _historyToAdd.setQuantity(_historyToAdd.getQuantity() + 1);
                                    break;
                            }

                            // recalculate points
                            _historyToAdd.setPoints(_historyToAdd.getQuantity() * _historyToAdd.getActivity().points);

                            // update amount label
                            TextView amountLabel = (TextView) dialogView.findViewById(R.id.add_activity_amount_label);
                            amountLabel.setText(String.format(Locale.getDefault(), "%d", _historyToAdd.getQuantity()));

                            // update points label
                            final int ACTIVITY_TYPE_REWARD = getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
                            final int ACTIVITY_TYPE_ACTIVITY = getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);
                            TextView pointTotalLabel = (TextView) dialogView.findViewById(R.id.add_activity_point_total_label);

                            if (_historyToAdd.getActivity().type == ACTIVITY_TYPE_REWARD) {
                                pointTotalLabel.setText("▼ " + String.format(Locale.getDefault(), "%d", _historyToAdd.getPoints()));
                                pointTotalLabel.setTextColor(ContextCompat.getColor(OverviewActivity.this, R.color.colorPointDown));
                            } else if (_historyToAdd.getActivity().type == ACTIVITY_TYPE_ACTIVITY) {
                                pointTotalLabel.setText("▲ " + String.format(Locale.getDefault(), "%d", _historyToAdd.getPoints()));
                                pointTotalLabel.setTextColor(ContextCompat.getColor(OverviewActivity.this, R.color.colorPointUp));
                            }

                            // disable log button if not enough points
                            if (_historyToAdd.getPoints() > mUser.getPoints() &&
                                    _historyToAdd.getActivity().type == ACTIVITY_TYPE_REWARD) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            } else {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }
                    };

                    // build amount buttons
                    Button amountDecrease = (Button) dialogView.findViewById(R.id.add_activity_amount_decrease);
                    Button amountIncrease = (Button) dialogView.findViewById(R.id.add_activity_amount_increase);
                    amountDecrease.setOnClickListener(amountButtonListener);
                    amountIncrease.setOnClickListener(amountButtonListener);

                    // build activity spinner
                    Spinner spinner = (Spinner) dialogView.findViewById(R.id.add_activity_spinner);
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(OverviewActivity.this,
                            android.R.layout.simple_spinner_item,
                            activityArray); //selected item will look like a spinner set from XML
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                    spinner.setSelection(selectedIndex); // set default selected index
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            _historyToAdd.setActivity(availableActivities.get(i));
                            _historyToAdd.setPoints(_historyToAdd.getQuantity() * _historyToAdd.getActivity().points);
                            // update points label
                            final int ACTIVITY_TYPE_REWARD = getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD);
                            final int ACTIVITY_TYPE_ACTIVITY = getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY);
                            TextView pointTotalLabel = (TextView) dialogView.findViewById(R.id.add_activity_point_total_label);

                            if (_historyToAdd.getActivity().type == ACTIVITY_TYPE_REWARD) {
                                pointTotalLabel.setText("▼ " + String.format(Locale.getDefault(), "%d", _historyToAdd.getPoints()));
                                pointTotalLabel.setTextColor(ContextCompat.getColor(OverviewActivity.this, R.color.colorPointDown));
                            } else if (_historyToAdd.getActivity().type == ACTIVITY_TYPE_ACTIVITY) {
                                pointTotalLabel.setText("▲ " + String.format(Locale.getDefault(), "%d", _historyToAdd.getPoints()));
                                pointTotalLabel.setTextColor(ContextCompat.getColor(OverviewActivity.this, R.color.colorPointUp));
                            }

                            // disable log button if not enough points
                            if (_historyToAdd.getPoints() > mUser.getPoints() &&
                                    _historyToAdd.getActivity().type == ACTIVITY_TYPE_REWARD) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            } else {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    dialog.show();
                }
            });


            // add the button
            menuMultipleActions.addMenuButton(_fab);
        }

        // hide and show the floating menu
        mActivityRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                if (dy > 0) {
                    menuMultipleActions.hideMenuButton(true);
                } else {
                    menuMultipleActions.showMenuButton(true);
                }
                super.onScrolled(view, dx, dy);
            }
        });


        // set the title bar color for white text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name),
                    bm,
                    ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));
            setTaskDescription(taskDesc);
        }
    }

    protected User initializeUser() {
        // get activities
        RealmResults<User> userResults = realm.where(User.class).findAll();
        if (userResults.size() < 1) {
            // make a new user
            realm.beginTransaction();
            User newUser = new User();
            newUser.setId(UUID.randomUUID().toString());
            newUser.setPoints(0);
            newUser.setLastUpdated(System.currentTimeMillis());
            realm.copyToRealmOrUpdate(newUser);
            realm.commitTransaction();

            return newUser;
        } else {
            // return our current user
            return userResults.first();
        }

    }

    protected void buildHistoryHeaders() {
        if (historyData.size() > 0) {
            for (int i = 0; i < historyData.size(); i++) {
                // get current activity date
                Calendar _calendar = Calendar.getInstance();
                _calendar.setTimeInMillis(historyData.get(i).getDate());
                final int _day = _calendar.get(Calendar.DAY_OF_MONTH);

                String _dateString = DateUtils.getRelativeTimeSpanString(
                        historyData.get(i).getDate(),
                        System.currentTimeMillis(),
                        DateUtils.DAY_IN_MILLIS
                ).toString();

                // first header
                if (i == 0) {
                    // build activity (holds date)
                    Activity _a = new Activity(
                            -1,
                            _dateString,
                            "",
                            -1,
                            0,
                            false);
                    // build history object (notifies adapter of date row)
                    History _h = new History();
                    _h.setId("-1");
                    _h.setActivity(_a);
                    _h.setQuantity(0);
                    _h.setDate(0);
                    _h.setPoints(0);

                    // add date header
                    historyData.add(i, _h);
                } else if (historyData.get(i - 1).getActivity().id != -1) { // if previous item is not a date
                    // get previous date number
                    _calendar.setTimeInMillis(historyData.get(i - 1).getDate());

                    // check if previous item's date is different
                    if (_calendar.get(Calendar.DAY_OF_MONTH) != _day) {
                        // set calendar back to current activity type
                        _calendar.setTimeInMillis(historyData.get(i).getDate());

                        // build activity (holds date)
                        Activity _a = new Activity(
                                -1,
                                _dateString,
                                "",
                                -1,
                                0,
                                false);
                        // build history object (notifies adapter of date row)
                        History _h = new History();
                        _h.setId("-1");
                        _h.setActivity(_a);
                        _h.setQuantity(0);
                        _h.setDate(0);
                        _h.setPoints(0);

                        // add date header
                        historyData.add(i, _h);
                    }
                }
            }
        }
    }

    protected BitmapDrawable makeLetterDrawable(String text) {
        Bitmap bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(64);


        Canvas canvas = new Canvas(bitmap);
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

        canvas.drawText(text, canvas.getWidth() / 2, yPos, paint);
        return new BitmapDrawable(getResources(), bitmap);
    }

    protected void updateUserPoints(int difference) {
        // update the user object
        realm.beginTransaction();
        mUser.setPoints(mUser.getPoints() + difference);
        realm.commitTransaction();

        // update the toolbar
        TextView pointTotal = (TextView) findViewById(R.id.point_total);
        TextView pointSubtitle = (TextView) findViewById(R.id.point_subtitle);
        pointTotal.setText(String.format(Locale.getDefault(), "%d", mUser.getPoints()));
        pointSubtitle.setText(mUser.getPoints() == 1 ? getString(R.string.point) : getString(R.string.point_plural));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // close our realm reference
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_activity_manage) {
            Intent intent = new Intent(this, ActivityManageActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHistoryItemClicked(int position) {
        Log.d("THIS ITEM WAS CLICKED", "POSITION: " + position);

        final int clickedItemPosition = position;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        int difference = historyData.get(clickedItemPosition).getPoints() *
                                (historyData.get(clickedItemPosition).getActivity().type ==
                                        getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD) ? 1 : -1);
                        updateUserPoints(difference);

                        // remove from realm;
                        realm.beginTransaction();
                        RealmResults<History> _h = realm.where(History.class).equalTo("id", historyData.get(clickedItemPosition).getId()).findAll();
                        _h.deleteFirstFromRealm();
                        realm.commitTransaction();

                        // remove this item
                        historyData.remove(clickedItemPosition);
                        mActivityAdapter.notifyItemRemoved(clickedItemPosition);

                        // if there's only one item left (date) remove it
                        if (historyData.size() == 1) {
                            historyData.remove(0);
                            mActivityAdapter.notifyItemRemoved(0);
                        } else if (historyData.get(clickedItemPosition - 1).getActivity().type == -1 && // if sandwiched by dates
                                historyData.size() > clickedItemPosition + 1 &&
                                historyData.get(clickedItemPosition).getActivity().type == -1) {
                            // remove the leading date object
                            historyData.remove(clickedItemPosition - 1);
                            mActivityAdapter.notifyItemRemoved(clickedItemPosition - 1);
                        }
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Remove log entry?").setPositiveButton("Remove", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }
}
