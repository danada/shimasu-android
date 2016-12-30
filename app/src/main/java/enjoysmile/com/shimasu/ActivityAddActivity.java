package enjoysmile.com.shimasu;

import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class ActivityAddActivity extends AppCompatActivity {
    private MenuItem activitySaveButton;

    private boolean edit_mode;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        edit_mode = intent.getBooleanExtra("edit_mode", false);

        // get activity types
        final String[] activityTypes = getResources().getStringArray(R.array.activity_types);


        // if editing
        if (edit_mode) {
            // get the id from the intent (or just pass the entire object)
            long activityId = intent.getLongExtra("activity_id", 0);
            // get
            Realm.init(getApplicationContext());
            Realm realm = Realm.getDefaultInstance();
            // get activities, order by type
            RealmResults<Activity> _a = realm.where(Activity.class).equalTo("id", activityId).findAll();
            activity = realm.copyFromRealm(_a.first());
            realm.close();
            // populate activity with the passed activity
            TextInputEditText activityName = (TextInputEditText) findViewById(R.id.activity_add_name);
            TextInputEditText activityDescription = (TextInputEditText) findViewById(R.id.activity_add_description);
            activityName.setText(activity.name);
            activityDescription.setText(activity.description);
        } else {
            // default activity object
            activity = new Activity(1, null, null, getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY), 1, true);
        }


        // configure activity type selector
        configureActivityType();
        View activity_type_selector = findViewById(R.id.activity_type_selector);
        activity_type_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);
                builder.setTitle("Activity type")
                        .setSingleChoiceItems(activityTypes, activity.type, new DialogInterface.OnClickListener() { // default is activity
                            public void onClick(DialogInterface dialog, int item) {
                                // set activity types
                                activity.type = item;

                                // reconfigure activity type selector
                                configureActivityType();

                                dialog.dismiss();
                            }
                        });

                // show the dialog
                builder.create().show();
            }
        });

        // configure activity point selector
        configureActivityPoints();
        View activity_point_selector = findViewById(R.id.activity_point_selector);
        activity_point_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_fragment_point_selector, null);

                // set the points field
                final EditText pointValue = (EditText) dialogView.findViewById(R.id.point_value);
                pointValue.setText("");
                pointValue.append(Integer.toString(activity.points)); // set cursor to end

                // set the repeat toggle
                final CheckBox canRepeat = (CheckBox) dialogView.findViewById(R.id.can_repeat_toggle);
                canRepeat.setChecked(activity.repeatable);

                builder.setView(dialogView)
                        .setTitle("Activity points")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // update the activity with these selections
                                activity.points = Integer.parseInt(pointValue.getText().toString());
                                activity.repeatable = canRepeat.isChecked();

                                //reconfigure activity points selector
                                configureActivityPoints();

                                dialog.dismiss();
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

        // set the title bar color for white text
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name),
                    bm,
                    ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));
            setTaskDescription(taskDesc);
        }
    }

    private void configureActivityType() {
        // find views
        TextView activityTypeLabel = (TextView) findViewById(R.id.activity_type_label);
        TextView activityTypeSubtitle = (TextView) findViewById(R.id.activity_type_subtitle);

        // get string arrays from resources
        String[] activityTypes = getResources().getStringArray(R.array.activity_types);
        String[] activityTypesDescription = getResources().getStringArray(R.array.activity_types_description);

        // set text
        activityTypeLabel.setText(activityTypes[activity.type]);
        activityTypeSubtitle.setText(activityTypesDescription[activity.type]);
    }

    private void configureActivityPoints() {
        //find views
        TextView pointAmountLabel = (TextView) findViewById(R.id.point_amount_label);
        TextView pointRepeatLabel = (TextView) findViewById(R.id.point_repeat_label);

        // get string arrays from resources
        String[] repeatableDescription = getResources().getStringArray(R.array.repeatable_description);

        // build point string
        String pointString;
        if (activity.points == 1) {
            pointString = getString(R.string.point);
        } else {
            pointString = getString(R.string.point_plural);
        }

        // set text
        pointAmountLabel.setText(activity.points + " " + pointString);
        pointRepeatLabel.setText(repeatableDescription[activity.repeatable ? 1 : 0]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_add, menu);

        // if edit mode
        if (edit_mode) {
            // show the delete button
            menu.findItem(R.id.action_activity_delete).setVisible(true);
        }

        // call validate input
        activitySaveButton = menu.findItem(R.id.action_activity_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_activity_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);
            builder.setMessage("Delete this activity?")
                    .setPositiveButton("Erase", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // delete this activity
                            Realm.init(getApplicationContext());
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            RealmResults<Activity> _a = realm.where(Activity.class).equalTo("id", activity.id).findAll();
                            _a.deleteFirstFromRealm();
                            realm.commitTransaction();
                            realm.close();
                            finish();
                            dialog.dismiss();
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

        if (id == R.id.action_activity_save) {
            // activity name and description
            TextInputEditText activityName = (TextInputEditText) findViewById(R.id.activity_add_name);
            TextInputEditText activityDescription = (TextInputEditText) findViewById(R.id.activity_add_description);
            activity.name = activityName.getText().toString();
            activity.description = activityDescription.getText().toString();

            // add the activity to realm
            Realm.init(getApplicationContext());
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();

            if (edit_mode) {
                // if we're editing, don't create a new object
                RealmResults<Activity> _a = realm.where(Activity.class).equalTo("id", activity.id).findAll();
                Activity toReplace = _a.first();
                toReplace = activity;
                realm.copyToRealmOrUpdate(toReplace);
            } else {
                // new object, we need to get a new ID
                activity.id = realm.where(Activity.class).count();
                realm.copyToRealm(activity);
            }

            realm.commitTransaction();
            realm.close();

            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}