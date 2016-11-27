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

        // specify an adapter
        final String[] activities = {"Beer", "Push Ups", "Sit Ups", "Snack"};
        List<Activity> activityDataset = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            String _name = activities[(int)(Math.random() * activities.length)];
            int _points = 1;
            Activity _activity = new Activity(_name, "", _points);
            activityDataset.add(_activity);
        }

        mActivityAdapter = new ActivityAdapter(activityDataset);
        mActivityRecyclerView.setAdapter(mActivityAdapter);

        // mess around with fab
        FloatingActionButton actionC = new FloatingActionButton(getBaseContext()); // new button
        actionC.setTitle("Beer");
        actionC.setIcon(R.drawable.ic_local_bar_white_24dp);
        actionC.setSize(FloatingActionButton.SIZE_MINI);
        actionC.setColorNormalResId(R.color.colorAccent);
        actionC.setColorPressedResId(R.color.colorAccentDark);

        // find the menu
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        // add the button
        menuMultipleActions.addButton(actionC);


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
