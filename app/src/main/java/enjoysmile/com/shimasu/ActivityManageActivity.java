package enjoysmile.com.shimasu;

import android.app.ActivityManager;
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

public class ActivityManageActivity extends AppCompatActivity {
    private RecyclerView mActivityRecyclerView;
    private RecyclerView.Adapter mActivityAdapter;
    private RecyclerView.LayoutManager mActivityLayoutManager;


    private List<Activity> activities;


    protected void seedActivities() {
        activities = new ArrayList<>();

        // beer
        activities.add(new Activity(activities.size() + 1,
                "Beer",
                "One pint",
                R.integer.ACTIVITY_TYPE_REWARD,
                10));
        // snack
        activities.add(new Activity(activities.size() + 1,
                "Snack",
                "Snack during class",
                R.integer.ACTIVITY_TYPE_REWARD,
                10));

        // push ups
        activities.add(new Activity(activities.size() + 1,
                "Push Ups",
                "10 times",
                R.integer.ACTIVITY_TYPE_ACTIVITY,
                1));
        // sit ups
        activities.add(new Activity(activities.size() + 1,
                "Sit Ups",
                "10 times",
                R.integer.ACTIVITY_TYPE_ACTIVITY,
                1));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // seed activities
        seedActivities();

        mActivityRecyclerView = (RecyclerView) findViewById(R.id.activity_recycler_view);

        // use a linear layout manager
        mActivityLayoutManager = new LinearLayoutManager(this);
        mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

        mActivityAdapter = new ActivityAdapter(activities);
        mActivityRecyclerView.setAdapter(mActivityAdapter);


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
