package com.pressurelabs.flow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.kobakei.ratethisapp.RateThisApp;

import java.util.ArrayList;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 *  The Stream is a Hub Screen which provides a List of the User's current Flows along with some minor details.
 *  The class allows for the creation and saving of new Flows, destruction of current ones and launching of the
 *  Flows into a new Activity
 *
 */
public class TheStreamActivity extends AppCompatActivity{
    public static final String RESTORED_USER_FLOWS = "RESTORED_USER_FLOWS";
    public static final String RESTORED_MANAGER_UTIL = "RESTORED_MANAGER_UTIL";

    private Flow newFlow;
        //Blank flow object declared.

    private static ArrayList<Flow> lvContent;
        // Manages the content present within the ListView

    private DataManagerUtil manager;
        // Manages the saving of data and Flow objects to internal storage

    private FlowArrayAdapter helperAdapter;
    private ListView listView;

    /** UI Actions and Set up */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stream);

        //Sets up toolbar
        Toolbar streamToolbar = (Toolbar) findViewById(R.id.stream_toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.stream_toolbar_title);
        toolbarTitle.setText("The Stream");
        setSupportActionBar(streamToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.stream_feed);

        if (savedInstanceState!=null && !savedInstanceState.isEmpty())
        {
            lvContent = savedInstanceState.getParcelableArrayList(RESTORED_USER_FLOWS);
            manager = savedInstanceState.getParcelable(RESTORED_MANAGER_UTIL);
        } else {
            lvContent = new ArrayList<>();
            manager = new DataManagerUtil(this);
        }


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

                Intent i = new Intent(TheStreamActivity.this, FlowSandBoxActivity.class);

                i.putExtra("selectedFlow", selectedFlow);
                    // Parcels the Flow Object to@ be passed to new activity
                startActivity(i);
            }

        });

        listView.setLongClickable(true);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    // View is the child view provided from AdapterView parent
                showPopUpMenu(TheStreamActivity.this, view, position);
                return true;
            }
        });


    }



    public void showPopUpMenu(Context context, View v, final int position) {

        LinearLayout viewGroup = (LinearLayout) findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_window, viewGroup);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setFocusable(true);

        int dividerMargin = 30; // Top bottom
        int popupPadding = layout.getPaddingBottom();
        int popupDisplayHeight = -(v.getHeight()-dividerMargin+popupPadding);

        // Displaying the popup at the specified location, + offsets.
        popup.showAsDropDown(v,0,popupDisplayHeight, Gravity.RIGHT);
        // Getting a reference to Close button, and close the popup when clicked.
        ImageView delete = (ImageView) layout.findViewById(R.id.delete_item);

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteFlow(position);
                popup.dismiss();
            }
        });
    }



    /* Allows the menu items to appear in the toolbar */
   @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
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
            case R.id.action_delete_flows:
                deleteFlowsDialog();
                return true;

            case R.id.action_new_flow:
                createNewFlow();
                return true;

            case R.id.terms_of_use:
                // User chose the "Settings" item, show the app settings UI...
                goToEULA();
                return false;

            // TODO Implement settings again?
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void goToEULA() {
        startActivity(
                new Intent(TheStreamActivity.this, EULAActivity.class)
        );

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

        boolean flowListAvailable = !manager.getFlowList().isEmpty();
        // Reads Internal Storage JSON file, receiving return in String Format

        if (flowListAvailable) {

            new Runnable() {
                @Override
                public void run() {
                    rebuildListView(TheStreamActivity.this);
                }
            }.run();


        } else {
                /* If no data is avaliable from file, a new Array Adapter will be setup and
                   feed a blank ListView.
                 */

            helperAdapter = new FlowArrayAdapter(this, lvContent);
            // Create new adapter with ListViewContent
            listView.setAdapter(helperAdapter);
        }
    }

    /** Attempts to rebuild the ListView Content by rebuilding the ListView ArrayList
     *  from file and recreating the Array Adapter.
     *
     * @param context current activity context
     */
    private void rebuildListView(Context context) {

        lvContent = manager.getFlowList();

        helperAdapter = new FlowArrayAdapter(this, lvContent);
        // Recreate FlowArrayAdapter and set
        listView.setAdapter(helperAdapter);

    }


    /** Launches the process of creating a new Flow Object by
     *  developing a Custom Dialog Box and determining valid
     *  user input.
     *
     */
    public void createNewFlow() {

            //Create edit text field for name entry
        final EditText nameInputET = new EditText(TheStreamActivity.this);
        AlertDialog.Builder customDialog = createNewFlowDialog(nameInputET);

        customDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameInputET.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(TheStreamActivity.this, "Every Flow deserves a good name :(", Toast.LENGTH_LONG).show();

                            createNewFlow(); //Recall the dialog
                        } else {
                            newFlow = new Flow(nameInputET.getText().toString(), 0);

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

    private AlertDialog.Builder createNewFlowDialog(EditText nameInputET) {
        AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(TheStreamActivity.this);

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
            flow.setFlowManagerIndex(lvContent.size());
            lvContent.add(flow);
                // Set the Flow Manager Index and add to List View Content

            helperAdapter.notifyDataSetChanged();
        }
    }

    /** Requests to save the current state of the ListViewContent
     *  to Internal Storage via the dataManager utility
     *
     * @param currentLVContent the current content in the list view ArrayList
     */
    private void saveFlowToManager(ArrayList<Flow> currentLVContent) {
        manager.saveToFile(this, currentLVContent);
    }

    /**
     * Creates and prompts user for confirmation of deleting all
     * Flow's in the list view from file
     */
    private void deleteFlowsDialog() {
        new AlertDialog.Builder(this)
                .setTitle("ALL Flows will be deleted.")
                .setMessage("This action is PERMANENT")
                .setCancelable(false)
                .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteAllFlows();
                        Toast.makeText(TheStreamActivity.this,
                                "Those poor Flows. \nI hope you're proud of yourself",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .setNegativeButton("No Don't! It's a Trap!", null)
                .show();
    }

    /** Deletes All Flows visible in the ListView as well as
     *  requests for all Flow data to be deleted from Internal Storage.
     *
     * @return boolean, confirmation of
     */
    private void deleteAllFlows() {
        lvContent.removeAll(lvContent);
        manager.eraseFileData(this);
        helperAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void deleteFlow(int position) {
        /* Commented out to prevent app breaking (Delete single flows for next commit) */

//          manager.delete(
//                  (Flow) listView.getItemAtPosition(position),
//                  TheStreamActivity.this
//        );
//
//        helperAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(RESTORED_USER_FLOWS, lvContent);
        outState.putParcelable(RESTORED_MANAGER_UTIL, manager);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);

        RateThisApp.Config config = new RateThisApp.Config(3, 10);
            // Custom title ,message and buttons names
        config.setTitle(R.string.rate_app_title);
        config.setMessage(R.string.rate_app_message);
        config.setYesButtonText(R.string.rate);
        config.setNoButtonText(R.string.no_rate);
        config.setCancelButtonText(R.string.rate_cancel);
        RateThisApp.init(config);

        // If the criteria is satisfied, "Rate this app" dialog will be shown
        RateThisApp.showRateDialogIfNeeded(this);


    }
}


