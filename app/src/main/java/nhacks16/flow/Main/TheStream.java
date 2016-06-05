package nhacks16.flow.Main;

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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import nhacks16.flow.R;

public class TheStream extends AppCompatActivity {
    // The Stream is basically a feed where a user can select the specific Flow that they wish to work on, view or create


    // To Convert Back to proper list

    private static final String TAG = TheStream.class.getName();
    private Toolbar streamToolbar;
    private Flow newFlow; //Blank flow object declared.

    private static ArrayList<Flow> flowManager;

    private FlowArrayAdapter helperAdapter;
    private ListView listView;

    /** UI Actions and Set up */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stream);

        //Sets up toolbar
        streamToolbar = (Toolbar) findViewById(R.id.streamToolbar);
        setSupportActionBar(streamToolbar);

        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.streamFeed);

    }

    private void setItemOnClicks() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Flow selectedFlow = (Flow) listView.getItemAtPosition(position);

                    // Below is only for developer sake of confirmation -- Non-crucial code

                Log.d(TAG, "Selected Item is: " + selectedFlow.getName());

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
            case R.id.action_delete_flow:
                deleteFlows();
                return true;

            case R.id.action_newFlow:
                createNewFlow();
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return false;





            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return false;
                //return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateList();
        setItemOnClicks();
    }

    private void populateList() {

        try {
            Log.d(TAG, "Retrieving flowManager data...");
            SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            // Get ArrayListBack
            Gson gson = new Gson();

            // Convert JSON Object stored as STRING in Shared Prefs, back to
            // Useable ArrayList Collection
            String json = mPrefs.getString("flowManager", null);
            Type type = new TypeToken<ArrayList<Flow>>() {}.getType();
            flowManager = gson.fromJson(json, type);

            Log.d(TAG, "FLAG");
        } catch (Exception e) {
            Log.e(TAG, "Exception throw in populating list: " + e.getMessage());
        }

        if (flowManager==null) {
            flowManager = new ArrayList<Flow>();
        }

        /// Need an adapter.add() statement somewhere
        helperAdapter = new FlowArrayAdapter(this, flowManager);
        listView.setAdapter(helperAdapter);

    }

    /** Creation of new Flow Object and Dialogs */
    public void createNewFlow() {
        //Creates dialog box asking for name for the new flow

        //Create edit text field for name entry
        final EditText nameInputET = new EditText(TheStream.this);
        AlertDialog.Builder customDialog = customizeDialog(nameInputET);

        customDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameInputET.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(TheStream.this, "Every Flow deserves a good name :(", Toast.LENGTH_LONG).show();

                            createNewFlow(); //Recall the dialog
                        } else {
                            // Sets name of flow object
                            newFlow = new Flow(nameInputET.getText().toString());
                            newFlow.setTotalTime(0.0);

                            updateFlowManager(newFlow);
                            //Just a fancy 1 liner really means:
                              // instantiateFlow(String input) => returns newFlow, addToStream(newFlow)
                        }
                    }
                });

        customDialog.setNegativeButton("Nevermind",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        //Display Alert
        customDialog.show();

    }

    private AlertDialog.Builder customizeDialog(EditText nameInputET) {
        AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(TheStream.this);

        //Sets up Layout Parameters
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMarginStart(42);
        params.setMarginEnd(50);


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

        return newFlowDialog;
    }


    private void updateFlowManager(Flow flow) {
        flowManager.add(flow);
        flow.setFlowManagerIndex(flowManager.size());

        helperAdapter.notifyDataSetChanged();
        saveFlowManager();
            // Async?

    }

    private void deleteFlows() {
        flowManager.clear();
        saveFlowManager();
            // Saves blank flowManager
        helperAdapter.notifyDataSetChanged();
    }

    public void saveFlowManager() {
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(flowManager);
        prefsEditor.putString("flowManager", json);
        prefsEditor.commit();
    }


}


