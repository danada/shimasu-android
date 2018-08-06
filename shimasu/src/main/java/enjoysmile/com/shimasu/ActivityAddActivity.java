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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import io.realm.Realm;

public class ActivityAddActivity extends AppCompatActivity {
  private boolean edit_mode;
  private Activity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    Intent intent = getIntent();
    edit_mode = intent.getBooleanExtra("edit_mode", false);

    // get activity types
    final String[] activityTypes = getResources().getStringArray(R.array.activity_types);

    // if editing
    if (edit_mode) {
      // get the id from the intent (or just pass the entire object)
      Realm.init(getApplicationContext());
      Realm realm = Realm.getDefaultInstance();
      activity =
          realm
              .where(Activity.class)
              .equalTo("id", intent.getLongExtra("activity_id", 0))
              .findFirst();
      realm.close();

      // populate activity with the passed activity
      TextInputEditText activityName = findViewById(R.id.activity_add_name);
      TextInputEditText activityDescription = findViewById(R.id.activity_add_description);
      activityName.setText(activity.getName());
      activityDescription.setText(activity.getDescription());
    } else {
      // default activity object
      activity = new Activity();
      activity.setId(1);
      activity.setType(getResources().getInteger(R.integer.ACTIVITY_TYPE_ACTIVITY));
      activity.setPoints(1);
      activity.setRepeatable(true);
      activity.setDeleted(false);
    }

    // configure activity type selector
    configureActivityType();
    View activity_type_selector = findViewById(R.id.activity_type_selector);
    activity_type_selector.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);
            builder
                // TODO: stringify.
                .setTitle("Activity type")
                .setSingleChoiceItems(
                    activityTypes,
                    activity.getType(),
                    new DialogInterface.OnClickListener() { // default is activity
                      public void onClick(DialogInterface dialog, int item) {
                        Realm.init(getApplicationContext());
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        // set activity types
                        activity.setType(item);

                        // reconfigure activity type selector
                        configureActivityType();

                        realm.commitTransaction();
                        realm.close();
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
    activity_point_selector.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);

            View dialogView =
                View.inflate(
                    ActivityAddActivity.this, R.layout.dialog_fragment_point_selector, null);

            // set the points field
            final EditText pointValue = dialogView.findViewById(R.id.point_value);
            pointValue.setText("");
            pointValue.append(Integer.toString(activity.getPoints())); // set cursor to end

            // set the repeat toggle
            final CheckBox canRepeat = dialogView.findViewById(R.id.can_repeat_toggle);
            canRepeat.setChecked(activity.isRepeatable());

            builder
                .setView(dialogView)
                // TODO: stringify.
                .setTitle("Activity points")
                .setPositiveButton(
                    // TODO: stringify.
                    "Ok",
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int id) {
                        Realm.init(getApplicationContext());
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        // update the activity with these selections
                        activity.setPoints(Integer.parseInt(pointValue.getText().toString()));
                        activity.setRepeatable(canRepeat.isChecked());

                        // reconfigure activity points selector
                        configureActivityPoints();

                        realm.commitTransaction();
                        realm.close();
                        dialog.dismiss();
                      }
                    })
                .setNegativeButton(
                    // TODO: stringify.
                    "Cancel",
                    new DialogInterface.OnClickListener() {
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
      ActivityManager.TaskDescription taskDesc =
          new ActivityManager.TaskDescription(
              getString(R.string.app_name),
              bm,
              ContextCompat.getColor(this, R.color.colorPrimaryVeryDark));
      setTaskDescription(taskDesc);
    }
  }

  private void configureActivityType() {
    // find views
    TextView activityTypeLabel = findViewById(R.id.activity_type_label);
    TextView activityTypeSubtitle = findViewById(R.id.activity_type_subtitle);

    // get string arrays from resources
    String[] activityTypes = getResources().getStringArray(R.array.activity_types);
    String[] activityTypesDescription =
        getResources().getStringArray(R.array.activity_types_description);

    // set text
    activityTypeLabel.setText(activityTypes[activity.getType()]);
    activityTypeSubtitle.setText(activityTypesDescription[activity.getType()]);
  }

  private void configureActivityPoints() {
    // find views
    TextView pointAmountLabel = findViewById(R.id.point_amount_label);
    TextView pointRepeatLabel = findViewById(R.id.point_repeat_label);

    // get string arrays from resources
    String[] repeatableDescription = getResources().getStringArray(R.array.repeatable_description);

    // build point string
    String pointString;
    if (activity.getPoints() == 1) {
      pointString = getString(R.string.point);
    } else {
      pointString = getString(R.string.point_plural);
    }

    // set text
    pointAmountLabel.setText(
        getResources()
            .getString(
                R.string.activity_add_point_amount_label, activity.getPoints(), pointString));
    pointRepeatLabel.setText(repeatableDescription[activity.isRepeatable() ? 1 : 0]);
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
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_activity_delete) {
      AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);
      builder
          // TODO: stringify.
          .setMessage("Delete this activity?")
          .setPositiveButton(
              "Erase",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                  // Mark this activity as deleted.
                  Realm.init(getApplicationContext());
                  Realm realm = Realm.getDefaultInstance();
                  realm.beginTransaction();
                  Activity toUpdate =
                      realm.where(Activity.class).equalTo("id", activity.getId()).findFirst();
                  if (toUpdate == null) {
                    return;
                  }
                  toUpdate.setDeleted(true);
                  realm.copyToRealmOrUpdate(toUpdate);
                  realm.commitTransaction();
                  realm.close();
                  finish();
                  dialog.dismiss();
                }
              })
          .setNegativeButton(
              // TODO: stringify.
              "Cancel",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  dialog.dismiss();
                }
              });

      // show the dialog
      builder.create().show();
    }

    if (id == R.id.action_activity_save) {
      // activity name and description
      TextInputEditText activityName = findViewById(R.id.activity_add_name);
      TextInputEditText activityDescription = findViewById(R.id.activity_add_description);

      // add the activity to realm
      Realm.init(getApplicationContext());
      Realm realm = Realm.getDefaultInstance();
      realm.beginTransaction();

      activity.setName(activityName.getText().toString());
      activity.setDescription(activityDescription.getText().toString());

      if (edit_mode) {
        // if we're editing, don't create a new object
        Activity toUpdate = realm.where(Activity.class).equalTo("id", activity.getId()).findFirst();
        if (toUpdate == null) {
          return false;
        }

        toUpdate.setName(activity.getName());
        toUpdate.setDescription(activity.getDescription());
        toUpdate.setType(activity.getType());
        toUpdate.setPoints(activity.getPoints());
        toUpdate.setRepeatable(activity.isRepeatable());
        toUpdate.setDeleted(activity.isDeleted());
        realm.copyToRealmOrUpdate(toUpdate);
      } else {
        // new object, we need to get a new ID
        activity.setId(realm.where(Activity.class).count() + 1);
        realm.copyToRealm(activity);
      }

      realm.commitTransaction();
      realm.close();

      Intent returnIntent = new Intent();
      setResult(getResources().getInteger(R.integer.ADD_ACTIVITY_REQUEST), returnIntent);
      finish();
    }
    return super.onOptionsItemSelected(item);
  }
}
