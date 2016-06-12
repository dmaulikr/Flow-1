package nhacks16.flow.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Iterator;

import nhacks16.flow.R;

/** The Stream is a Hub Screen which provides a List of the User's current Flows along with some minor details.
 *  The class allows for the creation and saving of new Flows, destruction of current ones and launching of the
 *  Flows into a new Activity
 *
 * @author Robert
 */
public class TheStream extends AppCompatActivity {

    private static final String TAG = TheStream.class.getName();
    private Toolbar streamToolbar;
    private Flow newFlow;
        //Blank flow object declared.

    private static ArrayList<Flow> lvContent;
        // Manages the content present within the ListView

    private FlowManagerUtil manager;
        // Manages the saving of data and Flow objects to internal storage

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

        lvContent = new ArrayList<Flow>();
        manager = new FlowManagerUtil();
    }

    /** Sets up each of the individual list view items to be clicked and launch an
     *  new activity based on selected Flow Object.
     *
     */
    private void setItemOnClicks() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    /* Passes Flow but passes the memory address of the childFlowElements
                     instead of the actual object containing the
                      */
                Flow selectedFlow = (Flow) listView.getItemAtPosition(position);

                Log.d(TAG, "Selected Item is: " + selectedFlow.getName());

                Intent i = new Intent(TheStream.this, SandBoxMain.class);

                i.putExtra("selectedFlow", selectedFlow);
                    // Parcels the Flow Object to@ be passed to new activity

                startActivity(i);

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
                clearAllFlows();
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
                return super.onOptionsItemSelected(item);

        }
    }

    /** Recreates the original Stream ListView by reading internal storage data
     *  and repopulating the ListView
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        populateList();
        setItemOnClicks();
    }

    /** Populates the ListView by asking for internal storage to be read,
     *  determining whether the response data is valid, and rebuilding the
     *  ListView if possible.
     */
    private void populateList() {

        try {
            Log.d(TAG, "Retrieving FlowManager data... ");
            Gson gson = new Gson();

            String responseData = manager.loadFlowDataInternal(this);
                // Reads Internal Storage JSON file, receiving return in String Format

            if (!responseData.equals("")){

                rebuildListView(this);

                iterateLVContent();

            } else {
                /* If no data is avaliable from file, a new Array Adapter will be setup and
                   feed a blank ListView.
                 */
                Log.d(TAG, "~ No data available.");

                Log.d(TAG, "> Creating New Adapater... \n");
                helperAdapter = new FlowArrayAdapter(this, lvContent);

                Log.d(TAG, ">> Setting New Adapter... \n");
                listView.setAdapter(helperAdapter);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception throw in populating list: " + e.getMessage());
        }
    }

    /** Attempts to rebuild the ListView Content by rebuilding the ListView ArrayList
     *  from file and recreating the Array Adapter.
     *
     * @param context
     */
    private void rebuildListView(Context context) {
        try {
            Log.d(TAG, "~ Rebuilding List View....\n");

            Log.d(TAG, "> Rebuilding Flow ArrayList... \n");
            lvContent = manager.rebuildFlowArray(context);

            Log.d(TAG, ">> Recreating Adapater... \n");
            helperAdapter = new FlowArrayAdapter(this, lvContent);

            Log.d(TAG, ">>> Setting Adapter... \n");
            listView.setAdapter(helperAdapter);

            Log.d(TAG, ">>>> SUCCESS!... \n");
        } catch (Exception e) {
            Log.e(TAG, "## Failed to Rebuild ListView " + e.getMessage());
        }

    }

    /** Iterates through the ListView Content displaying
     *  attributes of all the Flow Objects currently being
     *  contained.
     */
    private void iterateLVContent() {

        Iterator<Flow> iter = lvContent.iterator();

        Log.d(TAG, "~ Retrieved Flow Data: ");
        while(iter.hasNext()) {
            Flow temp = iter.next();
            Log.d(TAG, "\n>> Name: " + temp.getName()
                    + "\n>> Index:  " + temp.getFlowArrayIndex()
                    + "\n>> Element Count:  " + temp.getElementCount()
                    + "\n>> Time:  " + temp.getTime()
                    + "\n>> Children:  " + temp.getChildElements().toString());
        }
    }

    /** Launches the process of creating a new Flow Object by
     *  developing a Custom Dialog Box and determining valid
     *  user input.
     *
     */
    public void createNewFlow() {

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
                            newFlow = new Flow(nameInputET.getText().toString(), 0.0);

                            updateLVContent(newFlow);
                            saveFlowToManager(lvContent);

                        }
                    }
                });

        customDialog.setNegativeButton("Nevermind",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

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


    /** Updates the ListViewContent with a received Flow Object, and
     *  updates the UI to display the relevant changes.
     *
      * @param flow the Flow being added to the ListView Content
     */
// Updates the UI ListView content to display the newly created Flow Object
    private void updateLVContent(Flow flow) {
        if (helperAdapter!=null) {
            Log.d(TAG, "Adding New Flow to ListView Content...");
            flow.setFlowArrayIndex(lvContent.size());
            lvContent.add(flow);

            Log.d(TAG, "Current ListViewContent Size is: " + lvContent.size());

            helperAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "Update to ListViewContent Failed: helper adapter is null" );
        }
    }

    /** Requests to save the current state of the ListViewContent
     *  to Internal Storage via the dataManager utility
     *
     * @param currentLVContent
     */
    private void saveFlowToManager(ArrayList<Flow> currentLVContent) {
        manager.saveFlowDataInternal(this, currentLVContent);
    }

    /** Deletes All Flows visible in the ListView as well as
     *  requests for all Flow data to be deleted from Internal Storage.
     *
     * @return boolean, confirmation of
     */
    private boolean clearAllFlows() {
        lvContent.removeAll(lvContent);
        manager.deleteFileData(this);
        helperAdapter.notifyDataSetChanged();
        return true;
    }

}


