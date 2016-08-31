package com.pressurelabs.flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

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
public class FlowSandBoxActivity extends AppCompatActivity {
    // The SandBox serves as a hub for the flows, with several functions:
    //
    ///// and the user can specify if they've finished the task move to next activity || need more time (+why) || ask help (slack)


    private Flow currentFlow;
        // Flow currently being worked on
    private AppDataManager util;
    private GridView elementGridView;
    private ImageAdapter imgAdapater;
    private Toast currentToast = null;
    private Toolbar sbToolbar;
    private LinkedList<FlowElement> gridContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
            // Not keeping Flow Object's in their own capsules
        setContentView(R.layout.activity_sand_box);
        util = new AppDataManager(this);

        currentFlow = util.load(getIntent().getStringExtra(AppConstants.PASSING_UUID));

        sbToolbar = (Toolbar) findViewById(R.id.sb_toolbar);
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        TextView timesComplete = (TextView) findViewById(R.id.time_complete);

        try {
            timesComplete.setText(String.valueOf(currentFlow.getCompletionTokens()));
            title.setText(currentFlow.getName());
        } catch (NullPointerException e) {
            timesComplete.setText("");
            title.setText("");
        }


        setSupportActionBar(sbToolbar);

        elementGridView = (GridView) findViewById(R.id.e_visual_grid);


        gridContent = new LinkedList<>();
        gridContent.addAll(currentFlow.getChildElements());

        imgAdapater = new ImageAdapter(this, gridContent);
            // Passes the number of elements in the Flow's child elements to set the
            // Adapter's initial size
        elementGridView.setAdapter(imgAdapater);
        elementGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        elementGridView.setMultiChoiceModeListener(new MultiChoiceListener());
        elementGridView.setSelector(ContextCompat.getDrawable(FlowSandBoxActivity.this, R.drawable.gridview_selector));

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
                String eName = currentFlow.returnElement(position).getElementName();
                int eTime = currentFlow.returnElement(position).getTimeEstimate();
                String eUnits = currentFlow.returnElement(position).getTimeUnits();

                showToast("" + eName + "\n" + eTime + " " + eUnits);
            }
        });
    }
    /** Launches a new Element Designer Activity waiting to receive a new
     *  FlowElement Object back as a Parcel
     *
     * @param view the FAB clicked to launch the function
     */
    public void createElement(View view) {
        Intent in = new Intent(FlowSandBoxActivity.this, ElementDesignerActivity.class);
        startActivityForResult(in, AppConstants.DESIGNER_REQUEST_CODE);
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

        if(requestCode == AppConstants.DESIGNER_REQUEST_CODE && resultCode==RESULT_OK) {
            newElement = data.getParcelableExtra("newElement");
            addElementToFlow(newElement);
        }

        if(requestCode == AppConstants.FS_REQUEST_CODE && resultCode==RESULT_OK){
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
        currentFlow.addElement(newElement);

        imgAdapater.notifyDataSetChanged();

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

    class MultiChoiceListener implements GridView.MultiChoiceModeListener {

        //TODO Gridview not appearing as selected even when item has been selected
        //Add Delete Feature

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectCount = elementGridView.getCheckedItemCount();

            mode.setSubtitle("" + selectCount + " item(s) selected");

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            sbToolbar.setVisibility(View.INVISIBLE);
            getMenuInflater().inflate(R.menu.menu_gridview_context,menu);
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case R.id.action_delete_selected_items:
                    deleteSelection();
                    mode.finish();
                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.

                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            sbToolbar.setVisibility(View.VISIBLE);
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

            currentFlow.removeSelected(deletedChildElements);

            gridContent.clear();
            gridContent.addAll(currentFlow.getChildElements());

            imgAdapater.update(gridContent);

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
                            imgAdapater.update(gridContent);
                            util.overwrite(currentFlow.getUuid(),currentFlow);
                        }
                    });


            bar.show();
    }

}

