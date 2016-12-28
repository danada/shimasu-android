package enjoysmile.com.shimasu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class ActivityAddActivity extends AppCompatActivity {

    private Spinner activityType;

    private MenuItem activitySaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*
        activityType = (Spinner) findViewById(R.id.activity_type_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_Types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityType.setAdapter(adapter);
        */

        View _selector = findViewById(R.id.activity_type_selector);
        _selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("THIS", "WAS CLICKED");
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddActivity.this);
                final String[] _types = {"Reward", "Activity"};
                builder.setTitle("Activity type")
                    .setSingleChoiceItems(_types, -1, new DialogInterface
                            .OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Toast.makeText(getApplicationContext(),
                                    "Group Name = " + _types[item], Toast.LENGTH_SHORT).show();
                            dialog.dismiss();// dismiss the alertbox after chose option

                        }
                    });

                builder.create().show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_add, menu);

        // hide delete button
        menu.findItem(R.id.action_activity_delete).setVisible(false);

        // call validate input
        activitySaveButton = menu.findItem(R.id.action_activity_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_activity_save) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
