package enjoysmile.com.shimasu;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;
import java.util.List;

public class OverviewActivity extends AppCompatActivity {

    private RecyclerView mActivityRecyclerView;
    private RecyclerView.Adapter mActivityAdapter;
    private RecyclerView.LayoutManager mActivityLayoutManager;

    private List<History> historyData;


    private List<Activity> activities; // later, seed from database

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
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // recycler view
        mActivityRecyclerView = (RecyclerView) findViewById(R.id.activity_recycler_view);

        // use a linear layout manager
        mActivityLayoutManager = new LinearLayoutManager(this);
        mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

        // seed activities
        seedActivities();

        // build the history list
        historyData = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            // pick an activity
            Activity _a = activities.get((int)(Math.random() * activities.size()));

            // pick a quantity
            int _q = (int)(Math.random() * 5) + 1;

            historyData.add(new History(i + 1,
                    _a,
                    _q,
                    System.currentTimeMillis(),
                    _a.points * _q));
        }

        mActivityAdapter = new ActivityAdapter(historyData);
        mActivityRecyclerView.setAdapter(mActivityAdapter);


        // build floating action menu
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        for (int i = 0; i < activities.size(); i++) {
            FloatingActionButton _fab = new FloatingActionButton(getBaseContext()); // new button
            _fab.setTitle(activities.get(i).name);
            _fab.setIcon(R.drawable.ic_local_bar_white_24dp);
            _fab.setSize(FloatingActionButton.SIZE_MINI);
            if (activities.get(i).type == R.integer.ACTIVITY_TYPE_REWARD) {
                _fab.setColorNormalResId(R.color.colorAccent);
                _fab.setColorPressedResId(R.color.colorAccentDark);
            } else {
                _fab.setColorNormalResId(R.color.colorPrimary);
                _fab.setColorPressedResId(R.color.colorPrimaryDark);
            }

            // add the button
            menuMultipleActions.addButton(_fab);
        }


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
