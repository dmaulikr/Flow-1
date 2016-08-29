package com.pressurelabs.flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.askerov.dynamicgrid.DynamicGridView;

import java.util.LinkedList;


/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 * 1) Executing the elementDesigner activity
 * 2) Performing the drawing actions of the element on the view itself
 * 4) Executing the elementDetails activity
 * 5) Beginning the FlowStateActivity, where the flow begins and the timer counts down
 */
public class SandBoxActivity extends AppCompatActivity implements MultiFunctionGridView.GridInteractionListener {
    // The SandBox serves as a hub for the flows, with several functions:
    //
    ///// and the user can specify if they've finished the task move to next activity || need more time (+why) || ask help (slack)

    private final String TAG = "debug";

    private Flow currentFlow;
        // Flow currently being worked on
    private AppDataManager util;
    private DynamicGridView elementGridView;
    private SandBoxGridAdapter gridAdapter;
    private Toast currentToast = null;
    private Toolbar sbToolbar;
    private LinkedList<FlowElement> gridContent;
    private String menuState;
    private int reorderToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
            // Not keeping Flow Object's in their own capsules
        setContentView(R.layout.activity_sand_box);
        util = new AppDataManager(this);

        currentFlow = util.load(getIntent().getStringExtra(AppConstants.PASSING_UUID));

        sbToolbar = (Toolbar) findViewById(R.id.toolbar_sb);

        setSupportActionBar(sbToolbar);
        getSupportActionBar().setTitle(currentFlow.getName());

        elementGridView = (MultiFunctionGridView) findViewById(R.id.drag_drop_gridview);


        gridContent = new LinkedList<>();
        gridContent.addAll(currentFlow.getChildElements());

        gridAdapter = new SandBoxGridAdapter(this, gridContent,3);

            // Passes the number of elements in the Flow's child elements to set the
            // Adapter's initial size

        // Passes the number of elements in the Flow's child elements to set the
        // Adapter's initial size
        elementGridView.setAdapter(gridAdapter);


        menuState = AppConstants.MENU_NATIVE;
        reorderToggle =0;

        ((MultiFunctionGridView) elementGridView).setGridFunctionState(AppConstants.GS_MCL_CHECKABLE);
    }

    /* Allows the menu items to appear in the toolbar */
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_sandbox, menu);
        MenuItem reorder = menu.findItem(R.id.action_reorder_elements);
        MenuItem statistics = menu.findItem(R.id.action_flow_statistics);
        if (menuState.equals(AppConstants.MENU_HIDE)) {
            statistics.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    /* Invokes methods based on the icon picked in the toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* When the user selects one of the app bar items, the system
        calls your activity's onOptionsItemSelected() callback method,
        and passes a MenuItem object to indicate which item was clicked

        Requires menu items to use app:showAsAction="xyz"*/

        switch (item.getItemId()) {

            case R.id.action_reorder_elements:
                if (reorderToggle ==0) {
                    menuState=AppConstants.MENU_HIDE;
                    getSupportActionBar().setTitle(R.string.sb_sort_title);
                    invalidateOptionsMenu();
                    ((MultiFunctionGridView) elementGridView).setGridFunctionState(AppConstants.GS_DRAG_DROP);
                    reorderToggle = 1;
                    return true;
                } else {
                    menuState=AppConstants.MENU_NATIVE;
                    getSupportActionBar().setTitle(currentFlow.getName());
                    invalidateOptionsMenu();
                    ((MultiFunctionGridView) elementGridView).setGridFunctionState(AppConstants.GS_MCL_CHECKABLE);
                    reorderToggle = 0;
                    return true;
                }


            case R.id.action_flow_statistics:
                Toast.makeText(SandBoxActivity.this, "Statistics not available yet.",Toast.LENGTH_LONG).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }




    @Override
    protected void onResume() {
        super.onResume();
        setClickListeners();
    }

    /**
     * Sets the onClick action when an element in the grid is clicked.
     */
    private void setClickListeners() {
        elementGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String eName = currentFlow.getChildAt(position).getElementName();
                int eTime = currentFlow.getChildAt(position).getTimeEstimate();
                String eUnits = currentFlow.getChildAt(position).getTimeUnits();

                showToast(eName + "\n" + eTime + " " + eUnits);
            }
        });
    }
    /** Launches a new Element Designer Activity waiting to receive a new
     *  FlowElement Object back as a Parcel
     *
     * @param view the FAB clicked to launch the function
     */
    public void createElement(View view) {
        Intent in = new Intent(SandBoxActivity.this, ElementDesignerActivity.class);
        startActivityForResult(in, 1);
        //Starts new activity waiting for the return data
    }

    /** Receives Parcel Object from previous Element Designer Activity
     *  and creates a reference object to point to the Parcel
     *
     * @param requestCode the code being requested
     * @param resultCode the code being recieved
     * @param data the Parcelled object stored as intent data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        FlowElement newElement;

        if(requestCode==1 && resultCode==RESULT_OK) {
            newElement = data.getParcelableExtra("newElement");
            addElementToFlow(newElement);
        }

        if(requestCode==2 && resultCode==RESULT_OK){
          //TODO Updates UI to # of completed flows?
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /** Adds the newElement passed via parameter to the currentFlow's ArrayList
     *  while setting its FlowIndex and UpdatingSandBox Count
     *
     * @param newElement the FlowElement being saved
     */
    private void addElementToFlow(FlowElement newElement) {
        newElement.setLocation(currentFlow.getElementCount());

        gridContent.add(newElement);
        currentFlow.add(newElement);

        gridAdapter.notifyDataSetUpdated(gridContent);

        setClickListeners();

        util.overwrite(currentFlow.getUuid(),currentFlow);

    }

    private void showToast(String text)
    {
        if(currentToast != null)
        {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        currentToast.show();
    }



    public void goFlowState(View v) {
        if (currentFlow.getChildElements().isEmpty()) {
            this.showToast(
                    "Look's like there is no elements yet!"
            );
        } else {
            Intent in = new Intent(this, FlowStateActivity.class);
            in.putExtra(AppConstants.PASSING_UUID,currentFlow.getUuid());
            startActivity(in);
        }
    }



    /**
     * Deletes selected elements by getting the checked elements in the gridview,
     * iterating through each to add the element paired to the specific selection key to temp List.
     *
     * Removes the elements present in the temp List from the Flow's childElements List, updates the adapter
     * and saves the new flow.
     *
     * Provides additional option to undo via SnackBar
     */
    private void deleteSelection() {
        SparseBooleanArray selection = elementGridView.getCheckedItemPositions();

        final LinkedList<FlowElement> reference = new LinkedList<>(gridContent);
        final LinkedList<FlowElement> deletedChildElements = new LinkedList<>();

        try {
            for (int i=0; i<selection.size();i++) {
                deletedChildElements.add(gridContent.get(selection.keyAt(i)));
                /* If the element was checked it will be in the selection SBA,
                   so we add it to the collection of elements to remove but getting the element in the grid content
                   at the index = to the key found in the selection SBA
                 */
            }

            currentFlow.removeSelectedCollection(deletedChildElements);

            gridContent.clear();
            gridContent.addAll(currentFlow.getChildElements());
            gridAdapter.notifyDataSetUpdated(gridContent);

            util.overwrite(currentFlow.getUuid(), currentFlow);

        } catch (Exception e) {
            Toast.makeText(this,R.string.sandbox_delete_exception,Toast.LENGTH_LONG);
        }


            Snackbar bar = Snackbar.make(elementGridView, R.string.snackbar_sandbox_msg, Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentFlow.setChildElements(reference);
                            currentFlow.recalculateTotalTime();
                            gridContent.clear();
                            gridContent.addAll(currentFlow.getChildElements());
                            gridAdapter.notifyDataSetUpdated(gridContent);
                            util.overwrite(currentFlow.getUuid(),currentFlow);
                        }
                    });


            bar.show();
    }

    @Override
    public void onBackPressed() {
        if (elementGridView.isEditMode()) {
            elementGridView.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void reorderElements(int originalLocation, int desiredPosition) {

        Log.d(TAG, "Before Reorder \n" + currentFlow.getChildElements().toString());
        FlowElement e = currentFlow.getChildElements().remove(originalLocation);

        Log.d(TAG, "After E Remove \n" + currentFlow.getChildElements().toString());

        currentFlow.getChildElements().add(desiredPosition, e);
        Log.d(TAG, "After Readd \n" + currentFlow.getChildElements().toString());

        currentFlow.reassignChildLocations();
        //TODO Refactor into one method ("Insert Element At") in currentFlow (also remove getChild Elements)

        gridContent.clear();
        gridContent.addAll(currentFlow.getChildElements());
        Log.d(TAG, "Grid Content After update \n" + gridContent.toString());
        gridAdapter.notifyDataSetUpdated(gridContent);
        util.overwrite(currentFlow.getUuid(),currentFlow);
    }

    @Override
    public boolean createActionMenu(ActionMode mode, Menu menu) {
        getSupportActionBar().hide();
        getMenuInflater().inflate(R.menu.menu_gridview_context,menu);
        mode.setTitle("Select Items");
        mode.setSubtitle("One item selected");
        return true;
    }

    @Override
    public boolean actionItemClicked(ActionMode mode, MenuItem item) {
        deleteSelection();
        mode.finish();
        return true;
    }

    @Override
    public void destroyActionMenu(ActionMode mode) {
        getSupportActionBar().show();
    }

    @Override
    public void updateActionMenuCheckState(ActionMode mode) {
        int selectCount = elementGridView.getCheckedItemCount();

        mode.setSubtitle("" + selectCount + " item(s) selected");
    }


}

