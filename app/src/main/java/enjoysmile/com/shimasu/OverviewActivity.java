package enjoysmile.com.shimasu;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class OverviewActivity extends AppCompatActivity {

    private RecyclerView mActivityRecyclerView;
    private HistoryAdapter mActivityAdapter;
    private RecyclerView.LayoutManager mActivityLayoutManager;

    private RealmList<History> historyData;
    private RealmList<Activity> activities; // seed from database

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // open realm
        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        // recycler view
        mActivityRecyclerView = (RecyclerView) findViewById(R.id.activity_recycler_view);

        // use a linear layout manager
        mActivityLayoutManager = new LinearLayoutManager(this);
        mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

        // get activities
        RealmResults<Activity> activityResult = realm.where(Activity.class).findAllSorted("type", Sort.ASCENDING);
        activities = new RealmList<>();
        activities.addAll(activityResult.subList(0, activityResult.size()));

        // get history
        RealmResults<History> historyResult = realm.where(History.class).findAllSorted("date", Sort.DESCENDING);
        historyData = new RealmList<>();
        historyData.addAll(historyResult.subList(0, historyResult.size()));
        mActivityAdapter = new HistoryAdapter(historyData);
        mActivityRecyclerView.setAdapter(mActivityAdapter);


        // build floating action menu
        final FloatingActionMenu menuMultipleActions = (FloatingActionMenu) findViewById(R.id.multiple_actions);

        for (int i = 0; i < activities.size(); i++) {
            FloatingActionButton _fab = new FloatingActionButton(getBaseContext()); // new button
            _fab.setLabelText(activities.get(i).name);
            _fab.setButtonSize(FloatingActionButton.SIZE_MINI);
            if (activities.get(i).type == getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD)) {
                _fab.setColorNormalResId(R.color.colorAccent);
                _fab.setColorPressedResId(R.color.colorAccentDark);
                _fab.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24px);
            } else {
                _fab.setColorNormalResId(R.color.colorPrimary);
                _fab.setColorPressedResId(R.color.colorPrimaryDark);
                _fab.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24px);
            }

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

                            // update label
                            TextView amountLabel = (TextView) dialogView.findViewById(R.id.add_activity_amount_label);
                            amountLabel.setText(String.format(Locale.getDefault(), "%d", _historyToAdd.getQuantity()));
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
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });


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
                                    realm.commitTransaction();

                                    // update adapter
                                    historyData.add(0, _historyToAdd);
                                    mActivityAdapter.updateAdapter(historyData);

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
                    builder.create().show();
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
}
