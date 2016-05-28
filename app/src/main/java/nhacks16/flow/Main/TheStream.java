package nhacks16.flow.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.util.ArrayList;

import nhacks16.flow.R;

public class TheStream extends AppCompatActivity {
    // The Stream is basically a feed where a user can select the specific Flow that they wish to work on, view or create

    private static final String TAG = TheStream.class.getName();
    private Toolbar streamToolbar;
    private Flow newFlow; //Blank flow object declared.
    private static ArrayList<Flow> flowsInStream = new ArrayList<>();

    private FlowArrayAdapter helperAdapter;
    private ListView lv;

    /** UI Actions and Set up */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stream);

        //Sets up toolbar
        streamToolbar = (Toolbar) findViewById(R.id.streamToolbar);
        setSupportActionBar(streamToolbar);

        // Must instantiate in oncreate, but variable is declare global!
        helperAdapter = new FlowArrayAdapter(this, flowsInStream);

        // Attach the adapter to a ListView
        lv = (ListView) findViewById(R.id.streamFeed);
        lv.setAdapter(helperAdapter);

        setItemOnClicks();

    }

    private void setItemOnClicks() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Flow selectedFlow = (Flow) lv.getItemAtPosition(position);


                Log.d(TAG, "Testing Shared Preferences from onClick....");
                SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);

                Gson gson = new Gson();
                String json = mPrefs.getString(selectedFlow.getGsonId(),"");
                Flow f = gson.fromJson(json, Flow.class);

                Log.d(TAG, "Selected Item is: " + selectedFlow.getGsonId() + "\n " + json);

                Intent i = new Intent(TheStream.this, SandBoxMain.class);

                i.putExtra("selectedFlow", selectedFlow); // Parcel Object

                startActivity(i); //add new Flow

                finish();
            }
        });
    }

    /* Allows the menu items to appear in the toolbar */
   @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thestream, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /* Invokes methods based on the icon picked in the toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* When the user selects one of the app bar items, the system
        calls your activity's onOptionsItemSelected() callback method,
        and passes a MenuItem object to indicate which item was clicked */

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_newFlow:
                    flowDialog();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return false;
                //return super.onOptionsItemSelected(item);

        }
    }


    /** Creation of new Flow Object and Dialogs */
    public void flowDialog() {
        //Creates dialog box asking for name for the new flow
        AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(TheStream.this);

        //Sets up Layout Parameters
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMarginStart(42);
        params.setMarginEnd(50);

        //Create edit text field for name entry
        final EditText nameInputET = new EditText(TheStream.this);

        //Sets up length and 1 line filters
        nameInputET.setInputType(InputType.TYPE_CLASS_TEXT);
            //Only allows A-Z, a-z, 0-9, and special characters (%$!@)

        nameInputET.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(20)
        });

        //Adds the ET and params to the layout of the dialog box
        layout.addView(nameInputET, params);

        newFlowDialog.setTitle("Name your new Flow.");

        newFlowDialog.setView(layout);


        newFlowDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameInputET.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(TheStream.this, "Every Flow deserves a good name :(", Toast.LENGTH_LONG).show();

                            flowDialog(); //Recall the dialog
                        } else {
                            // Sets name of flow object
                            addToStream(instantiateFlow(nameInputET.getText().toString()));
                            //Just a fancy 1 liner really means:
                              // instantiateFlow(String input) => returns newFlow, addToStream(newFlow)
                        }
                    }
                });

        newFlowDialog.setNegativeButton("Nevermind",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        //Display Alert
        newFlowDialog.show();

    }

    private Flow instantiateFlow(String userInput) {
        //Instantiates the newFlow object.
        newFlow = new Flow(userInput);
        newFlow.setTotalTime(0.0);
        return newFlow;
    }

    private void addToStream(Flow flow) {
        flowsInStream.add(flow);
        String gsonId = "Flow " + flowsInStream.size();
        flow.setGsonId(gsonId);
        saveFlow(flow, gsonId);
        helperAdapter.notifyDataSetChanged();
    }

        private void saveFlow(Flow flow, String id) {
            SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
            try {
                SharedPreferences.Editor prefsEditor = mPrefs.edit();
                Gson gson = new Gson();
                String json = gson.toJson(flow);
                prefsEditor.putString(id, json);
                prefsEditor.commit();
                Log.d(TAG, "Saved " + flow.getGsonId() + " to Shared Preferences");


                String j = mPrefs.getString(flow.getGsonId(), "");
                Flow f = gson.fromJson(j, Flow.class);
                Log.d(TAG, "Here is the JSON Object: " + j);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


}


