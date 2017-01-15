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
import android.os.Parcel;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class OverviewActivity extends AppCompatActivity implements HistoryAdapter.ItemClickedListener, FragmentActivityLog.ActivityLoggedListener {

    private HistoryAdapter mActivityAdapter;
    private RecyclerView mActivityRecyclerView;

    private RealmList<History> mHistoryData;

    private User mUser;
    private Realm realm;

    private FloatingActionMenu mFloatingActionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // recycler view
        mActivityRecyclerView = (RecyclerView) findViewById(R.id.activity_recycler_view);
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
        updateUserPoints(0);

        // get activities
        RealmResults<Activity> activityResult = realm.where(Activity.class).findAllSorted("type", Sort.ASCENDING);
        RealmList<Activity> activityData = new RealmList<>();
        activityData.addAll(activityResult.subList(0, activityResult.size()));

        // get history
        final RealmResults<History> historyResult = realm.where(History.class).findAllSorted("date", Sort.DESCENDING);
        mHistoryData = new RealmList<>();
        mHistoryData.addAll(historyResult.subList(0, historyResult.size()));
        mActivityAdapter = new HistoryAdapter(mHistoryData, this);
        buildHistoryHeaders();
        mActivityRecyclerView.setAdapter(mActivityAdapter);

        // build floating action menu
        mFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.multiple_actions);

        for (int i = 0; i < activityData.size(); i++) {
            FloatingActionButton _fab = new FloatingActionButton(getBaseContext());

            // customize button's appearance
            _fab.setLabelText(activityData.get(i).name);
            _fab.setButtonSize(FloatingActionButton.SIZE_MINI);
            if (activityData.get(i).type == getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD)) {
                _fab.setColorNormalResId(R.color.colorAccent);
                _fab.setColorPressedResId(R.color.colorAccentDark);
            } else {
                _fab.setColorNormalResId(R.color.colorPrimary);
                _fab.setColorPressedResId(R.color.colorPrimaryDark);
            }
            // set the button drawable
            _fab.setImageDrawable(makeLetterDrawable(activityData.get(i).name.substring(0, 1)));


            // hang onto this activity's index
            final int selectedIndex = i;

            // set the onclick listener
            _fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // create new dialog instance with activity index
                    DialogFragment newFragment = FragmentActivityLog.newInstance(selectedIndex, OverviewActivity.this);
                    newFragment.show(getSupportFragmentManager(), "dialog");
                }
            });


            // add the button
            mFloatingActionMenu.addMenuButton(_fab);
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
                    mFloatingActionMenu.hideMenuButton(true);
                } else {
                    mFloatingActionMenu.showMenuButton(true);
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
        // get mActivityData
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
        if (mHistoryData.size() > 0) {
            for (int i = 0; i < mHistoryData.size(); i++) {
                // get current activity date
                Calendar _calendar = Calendar.getInstance();
                _calendar.setTimeInMillis(mHistoryData.get(i).getDate());
                final int _day = _calendar.get(Calendar.DAY_OF_MONTH);

                if (i == 0) { // first header
                    insertDateRow(i, mHistoryData.get(i).getDate());
                } else if (mHistoryData.get(i - 1).getActivity().id != -1) { // if previous item is not a date
                    // get previous date number
                    _calendar.setTimeInMillis(mHistoryData.get(i - 1).getDate());
                    // check if previous item's date is different
                    if (_calendar.get(Calendar.DAY_OF_MONTH) != _day) {
                        insertDateRow(i, mHistoryData.get(i).getDate());
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

    protected void insertDateRow(int at, long date) {
        // get current activity date string
        String _dateString = DateUtils.getRelativeTimeSpanString(
                date,
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
        mHistoryData.add(at, _h);
        // notify adapter
        mActivityAdapter.notifyItemInserted(at);
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
        final int clickedItemPosition = position;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        int difference = mHistoryData.get(clickedItemPosition).getPoints() *
                                (mHistoryData.get(clickedItemPosition).getActivity().type ==
                                        getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD) ? 1 : -1);
                        updateUserPoints(difference);

                        // remove from realm;
                        realm.beginTransaction();
                        RealmResults<History> _h = realm.where(History.class).equalTo("id", mHistoryData.get(clickedItemPosition).getId()).findAll();
                        _h.deleteFirstFromRealm();
                        realm.commitTransaction();

                        // remove this item
                        mHistoryData.remove(clickedItemPosition);
                        mActivityAdapter.notifyItemRemoved(clickedItemPosition);

                        // if there's only one item left (date) remove it
                        if (mHistoryData.size() == 1) {
                            mHistoryData.remove(0);
                            mActivityAdapter.notifyItemRemoved(0);
                        } else if (mHistoryData.get(clickedItemPosition - 1).getActivity().type == -1 && // if sandwiched by dates
                                mHistoryData.size() > clickedItemPosition + 1 &&
                                mHistoryData.get(clickedItemPosition).getActivity().type == -1) {
                            // remove the leading date object
                            mHistoryData.remove(clickedItemPosition - 1);
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

    @Override
    public void onActivityLogged(History historyToAdd) {
        // update points label
        int difference = historyToAdd.getPoints() *
                (historyToAdd.getActivity().type ==
                        getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD) ? -1 : 1);
        updateUserPoints(difference);




        String _dateString = DateUtils.getRelativeTimeSpanString(
                historyToAdd.getDate(),
                System.currentTimeMillis(),
                DateUtils.DAY_IN_MILLIS).toString();

        // if first item
        if (mHistoryData.size() == 0) {
            insertDateRow(0, historyToAdd.getDate());
        } else if (mHistoryData.size() > 0 &&
                !mHistoryData.get(0).getActivity().name.equals(_dateString)) {
            // insert a date row
            insertDateRow(0, historyToAdd.getDate());
        }

        mHistoryData.add(1, historyToAdd);
        mActivityAdapter.notifyItemInserted(1);

        // scroll up
        mActivityRecyclerView.scrollToPosition(0);

        // close menu
        mFloatingActionMenu.close(true);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
