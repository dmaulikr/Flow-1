package com.pressurelabs.flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
            // Not keeping Flow Object's in their own capsules
        setContentView(R.layout.activity_sand_box);
        util = new AppDataManager(this);

        currentFlow = util.load(getIntent().getStringExtra(AppConstants.UUID_PASSED));

        sbToolbar = (Toolbar) findViewById(R.id.sb_toolbar);
        TextView title = (TextView) findViewById(R.id.toolbar_title);

        TextView timesComplete = (TextView) findViewById(R.id.time_complete);
        timesComplete.setText(String.valueOf(currentFlow.getCompletionTokens()));
        title.setText(currentFlow.getName());

        setSupportActionBar(sbToolbar);

        elementGridView = (GridView) findViewById(R.id.e_visual_grid);


        final ArrayList<Integer> elementGrid = new ArrayList<>();
        for (int i = 0; i< currentFlow.getElementCount(); i++){
            elementGrid.add(R.drawable.flag_black_48dp);
        }

        imgAdapater = new ImageAdapter(this, elementGrid);
            // Passes the number of elements in the Flow's child elements to set the
            // Adapter's initial size
        elementGridView.setAdapter(imgAdapater);
        elementGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        elementGridView.setMultiChoiceModeListener(new MultiChoiceListener());
        elementGridView.setDrawSelectorOnTop(true);
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
        currentFlow.addElement(newElement);

        updateSandBox();
        util.overwrite(currentFlow.getUuid(),currentFlow);

        // Saves the updated JSONFlowWrapper ArrayList as
        // the updated list (acts as the updated list

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

    /**
     *  Updates the SandBox's GridView after a single element has been created
     */
    private void updateSandBox() {
        imgAdapater.addOne();
        imgAdapater.notifyDataSetChanged();

        setClickListeners();
    }


    public void goFlowState(View v) {
        if (currentFlow.getChildElements().isEmpty()) {
            this.showToast(
                    "Look's like there is no elements yet!"
            );
        } else {
            Intent in = new Intent(this, FlowStateActivity.class);
            in.putExtra(AppConstants.UUID_PASSED,currentFlow.getUuid());
            startActivity(in);
        }
    }

    class MultiChoiceListener implements GridView.MultiChoiceModeListener {

        //TODO Gridview not appearing as selected even when item has been selected
        //TODO Make Action bar be converted to ActionMode bar
        //Add Delete Feature

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectCount = elementGridView.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            sbToolbar.setVisibility(View.GONE);
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
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            sbToolbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Handles the onBackPressed event by sending user to TheHubActivity
     */
    @Override
    public void onBackPressed() {
        Intent i= new Intent(FlowSandBoxActivity.this, TheHubActivity.class);
        startActivity(i);//starting main activity
        super.onBackPressed();
    }


}
