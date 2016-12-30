package enjoysmile.com.shimasu;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.R.attr.onClick;

public class ActivityManageActivity extends AppCompatActivity {
    private RecyclerView mActivityRecyclerView;
    private RecyclerView.Adapter mActivityAdapter;
    private RecyclerView.LayoutManager mActivityLayoutManager;

    private List<Activity> activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get
        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();
        // get activities, order by type
        RealmResults<Activity> _a = realm.where(Activity.class).findAllSorted("type", Sort.ASCENDING);
        activities = realm.copyFromRealm(_a);
        realm.close();

        mActivityRecyclerView = (RecyclerView) findViewById(R.id.activity_recycler_view);

        // use a linear layout manager
        mActivityLayoutManager = new LinearLayoutManager(this);
        mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

        mActivityAdapter = new ActivityAdapter(activities);
        mActivityRecyclerView.setAdapter(mActivityAdapter);

        // bind add button
        final FloatingActionButton addActivityButton = (FloatingActionButton) findViewById(R.id.add_activity_button);
        addActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO - startActivityForResult to get notified of addition
                Intent intent = new Intent(getApplicationContext(), ActivityAddActivity.class);
                startActivity(intent);
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

}