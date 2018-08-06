package enjoysmile.com.shimasu;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import enjoysmile.com.shimasu.data.Activity;
import enjoysmile.com.shimasu.data.History;
import enjoysmile.com.shimasu.data.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import java.util.Locale;
import java.util.UUID;

@SuppressLint("ParcelCreator")
public class OverviewActivity extends AppCompatActivity
    implements HistoryAdapter.ItemClickedListener, FragmentActivityLog.ActivityLoggedListener {

  private HistoryAdapter mActivityAdapter;
  private RecyclerView mActivityRecyclerView;

  private RealmList<History> mHistoryData = new RealmList<>();

  private User mUser;
  private Realm realm;

  private FloatingActionMenu mFloatingActionMenu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_overview);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // recycler view
    mActivityRecyclerView = findViewById(R.id.activity_recycler_view);
    // use a linear layout manager
    final RecyclerView.LayoutManager mActivityLayoutManager = new LinearLayoutManager(this);
    mActivityRecyclerView.setLayoutManager(mActivityLayoutManager);

    Realm.init(this);
    RealmConfiguration config =
        new RealmConfiguration.Builder().schemaVersion(1).migration(new Migration()).build();
    Realm.setDefaultConfiguration(config);
    realm = Realm.getDefaultInstance();

    // initiate user
    mUser = initializeUser();
    updateUserPoints(0);

    // get activities
    RealmResults<Activity> activityResult =
        realm
            .where(Activity.class)
            .equalTo("deleted", false)
            .sort("type", Sort.ASCENDING)
            .findAll();
    RealmList<Activity> activityData = new RealmList<>();
    activityData.addAll(activityResult);

    mActivityAdapter = new HistoryAdapter(mHistoryData, this);
    mActivityRecyclerView.setAdapter(mActivityAdapter);

    // Get history and add it to the history list.
    RealmResults<History> historyResult =
        realm.where(History.class).sort("date", Sort.ASCENDING).findAll();
    for (History history : historyResult) {
      addHistory(history);
    }

    // build floating action menu
    mFloatingActionMenu = findViewById(R.id.multiple_actions);

    for (Activity activity : activityData) {
      FloatingActionButton fab = new FloatingActionButton(getBaseContext());

      // customize button's appearance
      fab.setLabelText(activity.getName());
      fab.setButtonSize(FloatingActionButton.SIZE_MINI);
      if (activity.getType() == getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD)) {
        fab.setColorNormalResId(R.color.colorAccent);
        fab.setColorPressedResId(R.color.colorAccentDark);
      } else {
        fab.setColorNormalResId(R.color.colorPrimary);
        fab.setColorPressedResId(R.color.colorPrimaryDark);
      }
      // set the button drawable
      fab.setImageDrawable(makeLetterDrawable(activity.getName().substring(0, 1)));

      // hang onto this activity's index
      final int selectedIndex = activityData.indexOf(activity);

      // set the onclick listener
      fab.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // create new dialog instance with activity index
              DialogFragment newFragment =
                  FragmentActivityLog.newInstance(selectedIndex, OverviewActivity.this);
              newFragment.show(getSupportFragmentManager(), "dialog");
            }
          });

      // add the button
      mFloatingActionMenu.addMenuButton(fab);
    }

    // hide and show the floating menu
    mActivityRecyclerView.addOnScrollListener(
        new RecyclerView.OnScrollListener() {
          @Override
          public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
          }

          @Override
          public void onScrolled(@NonNull RecyclerView view, int dx, int dy) {
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
      ActivityManager.TaskDescription taskDesc =
          new ActivityManager.TaskDescription(
              getString(R.string.app_name),
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
      realm.copyToRealmOrUpdate(newUser);
      realm.commitTransaction();

      return newUser;
    } else {
      // return our current user
      return userResults.first();
    }
  }

  protected void removeHistory(History history) {
    int historyIndex = mHistoryData.indexOf(history);

    // Remove points.
    updateUserPoints(
        history.getPoints()
            * (history.getActivity().getType()
                    == getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD)
                ? 1
                : -1));

    // Remove from realm.
    realm.beginTransaction();
    RealmResults<History> historyToDelete =
        realm.where(History.class).equalTo("id", history.getId()).findAll();
    historyToDelete.deleteAllFromRealm();
    realm.commitTransaction();

    // Remove from list.
    mHistoryData.remove(historyIndex);
    mActivityAdapter.notifyItemRemoved(historyIndex);

    // If there's only one row left, remove it.
    if (mHistoryData.size() == 1) {
      mHistoryData.remove(0);
      mActivityAdapter.notifyItemRemoved(0);

      return;
    }

    // If the removed item was between two date rows, remove the row preceding the item.
    if (mHistoryData.size() > 0 && historyIndex < mHistoryData.size()) {
      History previousItem = mHistoryData.get(historyIndex - 1);
      History currentItem = mHistoryData.get(historyIndex);
      if (previousItem == null || currentItem == null) {
        return;
      }

      if (previousItem.getId().equals("-1") && currentItem.getId().equals("-1")) {
        mHistoryData.remove(previousItem);
        mActivityAdapter.notifyItemRemoved(historyIndex - 1);
      }
    }
  }

  protected void addHistory(History history) {
    // Get the history's date string.
    String dateString =
        DateUtils.getRelativeTimeSpanString(
                history.getDate(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS)
            .toString();

    // Insert a date row if it doesn't exist yet.
    if (mHistoryData.size() == 0) {
      // Create an empty activity with this date.
      Activity activity = new Activity();
      activity.setName(dateString);

      // Create history with an ID of -1 so it is rendered as a subheading.
      History dateRow = new History();
      dateRow.setId("-1");
      dateRow.setDate(history.getDate());
      dateRow.setActivity(activity);

      // Add the header to the data set.
      mHistoryData.add(0, dateRow);
      mActivityAdapter.notifyItemInserted(0);

      // Add the new activity.
      mHistoryData.add(history);
      mActivityAdapter.notifyItemInserted(1);

      return;
    }

    History previousItem = mHistoryData.get(0);
    if (previousItem == null) {
      return;
    }

    String previousDateString =
        DateUtils.getRelativeTimeSpanString(
                previousItem.getDate(), System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS)
            .toString();

    // If the 0th row is a date row and different from the activity's date.
    if (previousItem.getId().equals("-1") && !previousDateString.equals(dateString)) {
      // Create an empty activity with this date.
      Activity activity = new Activity();
      activity.setName(dateString);

      // Create history with an ID of -1 so it is rendered as a subheading.
      History dateRow = new History();
      dateRow.setId("-1");
      dateRow.setDate(history.getDate());
      dateRow.setActivity(activity);

      // Add the header to the data set.
      mHistoryData.add(0, dateRow);
      mActivityAdapter.notifyItemInserted(0);
    }

    // Add the new activity.
    mHistoryData.add(1, history);
    mActivityAdapter.notifyItemInserted(1);
  }

  protected BitmapDrawable makeLetterDrawable(String text) {
    int bitmapSize = 20;
    Resources r = getResources();
    int px =
        (int)
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, bitmapSize, r.getDisplayMetrics());

    Bitmap bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888);
    Paint paint = new Paint();
    paint.setFlags(Paint.ANTI_ALIAS_FLAG);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(Color.WHITE);
    paint.setTextAlign(Paint.Align.CENTER);
    paint.setTextSize(px);

    Canvas canvas = new Canvas(bitmap);
    int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

    canvas.drawText(text, canvas.getWidth() / 2, yPos, paint);
    return new BitmapDrawable(getResources(), bitmap);
  }

  protected void updateUserPoints(int difference) {
    // update the user object
    realm.beginTransaction();
    mUser.setPoints(mUser.getPoints() + difference);
    realm.commitTransaction();

    // update the toolbar
    TextView pointTotal = findViewById(R.id.point_total);
    TextView pointSubtitle = findViewById(R.id.point_subtitle);
    pointTotal.setText(String.format(Locale.getDefault(), "%d", mUser.getPoints()));
    pointSubtitle.setText(
        mUser.getPoints() == 1 ? getString(R.string.point) : getString(R.string.point_plural));
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

    DialogInterface.OnClickListener dialogClickListener =
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            switch (which) {
              case DialogInterface.BUTTON_POSITIVE:
                removeHistory(mHistoryData.get(clickedItemPosition));
                break;
            }
          }
        };

    // build the dialog for removing an activity
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder
        .setMessage(getString(R.string.overview_activity_delete_activity_message))
        .setPositiveButton(
            getString(R.string.overview_activity_delete_activity_remove_button),
            dialogClickListener)
        .setNegativeButton(
            getString(R.string.overview_activity_delete_activity_cancel_button),
            dialogClickListener)
        .show();
  }

  @Override
  public void onActivityLogged(History historyToAdd) {
    // update points label
    int difference =
        historyToAdd.getPoints()
            * ((historyToAdd.getActivity().getType()
                    == getResources().getInteger(R.integer.ACTIVITY_TYPE_REWARD)
                ? -1
                : 1));

    updateUserPoints(difference);
    addHistory(historyToAdd);

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
  public void writeToParcel(Parcel parcel, int i) {}
}
