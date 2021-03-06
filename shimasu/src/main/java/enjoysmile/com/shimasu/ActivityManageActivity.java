package enjoysmile.com.shimasu;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.clans.fab.FloatingActionButton;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.List;

public class ActivityManageActivity extends AppCompatActivity {
  private ActivityAdapter mActivityAdapter;

  private List<Activity> activities;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_manage);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    Realm.init(getApplicationContext());
    Realm realm = Realm.getDefaultInstance();
    // get activities, order by type
    RealmResults<Activity> _a =
        realm
            .where(Activity.class)
            .equalTo("deleted", false)
            .sort("type", Sort.ASCENDING)
            .findAll();
    activities = realm.copyFromRealm(_a);
    realm.close();

    RecyclerView mActivityRecyclerView = findViewById(R.id.activity_recycler_view);

    // use a linear layout manager
    RecyclerView.LayoutManager mActivityLayoutManager = new LinearLayoutManager(this);
    mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

    mActivityAdapter = new ActivityAdapter(activities);
    mActivityRecyclerView.setAdapter(mActivityAdapter);

    // bind add button
    final FloatingActionButton addActivityButton = findViewById(R.id.add_activity_button);
    addActivityButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ActivityAddActivity.class);
            startActivityForResult(
                intent, getResources().getInteger(R.integer.ADD_ACTIVITY_REQUEST));
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check which request we're responding to
    if (requestCode == getResources().getInteger(R.integer.ADD_ACTIVITY_REQUEST)
        || requestCode == getResources().getInteger(R.integer.EDIT_ACTIVITY_REQUEST)) {

      Realm.init(getApplicationContext());
      Realm realm = Realm.getDefaultInstance();
      // get activities, order by type
      RealmResults<Activity> _a =
          realm
              .where(Activity.class)
              .equalTo("deleted", false)
              .sort("type", Sort.ASCENDING)
              .findAll();
      activities = realm.copyFromRealm(_a);
      realm.close();
      mActivityAdapter.updateAdapter(activities);
    }
  }
}
