package com.pressurelabs.flow;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kobakei.ratethisapp.RateThisApp;

import java.util.ArrayList;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-08-14
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 *  The Hub Screen provides a List of the User's current Flows along with some minor details.
 *  The class allows for the creation and saving of new Flows, destruction of current ones and launching of the
 *  Flows into a new Activity
 */
public class TheHubActivity extends AppCompatActivity implements RecyclerViewAdapter.onCardClickListener {

    private AppDataManager manager;
    // Manages the saving of data and Flow objects to internal storage

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<Flow> rvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_the_hub);
        TextView toolbarTitle = (TextView) findViewById(R.id.title_the_hub);
        toolbarTitle.setText("The Hub");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (savedInstanceState!=null && !savedInstanceState.isEmpty())
        {
            rvContent = savedInstanceState.getParcelableArrayList(AppConstants.RESTORED_USER_FLOWS);
            manager = savedInstanceState.getParcelable(AppConstants.RESTORED_DATA_MANAGER);
        } else {
            rvContent = new ArrayList<>();
            manager = new AppDataManager(this);
        }



    }

    /* Allows the menu items to appear in the toolbar */
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_the_hub, menu);
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
                new Intent(TheHubActivity.this, EULAActivity.class)
        );

    }

    /** Recreates the original RecView by reading internal storage data
     *  and repopulating the rvContent ArrayList
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        populateRecycleView();
        /* DATA LEAK HERE DON'T TOUCH*/
//        ComplexPreferences cPrefs = ComplexPreferences
//                .getComplexPreferences(this, AppConstants.COMPLEX_PREFS, MODE_PRIVATE);
//        cPrefs.putObject(AppConstants.APP_DATA_MANAGER,manager);
    }


    /** Populates the RecycleView by asking for internal storage to be read,
     *  determining whether the response data is valid, and rebuilding the
     *  RecycleView if possible.
     */
    private void populateRecycleView() {

        boolean savedContentAvailable = manager.hasData();
        // Reads Internal Storage JSON file, receiving return in String Format

        if (savedContentAvailable) {

            new Runnable() {
                @Override
                public void run() {
                    rebuildContent();
                }
            }.run();


        } else {
                /* If no data is avaliable from file, a new Array Adapter will be setup and
                   feed a blank RecycleView
                 */

            adapter = new RecyclerViewAdapter(TheHubActivity.this, rvContent);
            // Create new adapter with Recycle View Content
//            adapter.setCardEditingCallback(this);
            recyclerView.setAdapter(adapter);

        }
    }

    /** Attempts to rebuild the RecycleView Content by rebuilding the RecycleView ArrayList
     *  from file and recreating the Array Adapter.
     *
     */
    private void rebuildContent() {

        rvContent = manager.generateArrayList();

        adapter = new RecyclerViewAdapter(TheHubActivity.this, rvContent);
        // Recreate FlowArrayAdapter and set

//        adapter.setCardEditingCallback(this);
        recyclerView.setAdapter(adapter);

    }


    /** Launches the process of creating a new Flow Object by
     *  developing a Custom Dialog Box and determining valid
     *  user input.
     *
     */
    public void createNewFlow() {

        //Create edit text field for name entry
        final EditText nameInputET = new EditText(TheHubActivity.this);
        AlertDialog.Builder customDialog = customDialog(nameInputET);

        customDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameInputET.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(TheHubActivity.this, "Every Flow deserves a good name :(", Toast.LENGTH_LONG).show();

                            createNewFlow(); //Recall the dialog
                        } else {

                            Flow newF = new Flow(nameInputET.getText().toString(), 0);

                            updateUIContent(newF);

                            manager.save(newF.getUuid(),newF);

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

    private AlertDialog.Builder customDialog(EditText nameInputET) {
        AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(TheHubActivity.this);

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


    /** Updates the View Content with a received Flow Object, and
     *  updates the UI to display the relevant changes.
     *
     * @param flow the Flow being added to the content
     */
    private void updateUIContent(Flow flow) {
        if (adapter != null) {
            rvContent.add(flow);
            // Set the Flow Manager Index and add to List View Content

            adapter.notifyDataSetChanged();
        }
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
                        deleteAllFlowData();
                        Toast.makeText(TheHubActivity.this,
                                "Those poor Flows. \nI hope you're proud of yourself",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                })
                .setNegativeButton("No Don't! It's a Trap!", null)
                .show();
    }

    /** Deletes All Flows visible in the RecView as well as
     *  requests for all Flow data to be deleted from Internal Storage.
     *
     * @return boolean, confirmation of
     */
    private void deleteAllFlowData() {
        rvContent.removeAll(rvContent);
        manager.deleteAll();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deleteCard(Flow flowToDelete, int position) {
        /* Commented out to prevent app breaking (Delete single flows for next commit) */
        rvContent.remove(position);
        manager.delete(flowToDelete.getUuid());
        adapter.notifyItemRemoved(position);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Monitor launch times and interval from installation
        RateThisApp.onStart(this);
        RateThisApp.Config config = new RateThisApp.Config(10, 10);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(AppConstants.RESTORED_USER_FLOWS, rvContent);
        outState.putParcelable(AppConstants.RESTORED_DATA_MANAGER, manager);
        super.onSaveInstanceState(outState);
    }

}
