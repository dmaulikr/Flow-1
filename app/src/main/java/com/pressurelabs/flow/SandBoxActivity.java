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
import android.widget.GridView;
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
public class SandBoxActivity extends AppCompatActivity {
    // The SandBox serves as a hub for the flows, with several functions:
    //
    ///// and the user can specify if they've finished the task move to next activity || need more time (+why) || ask help (slack)

    private final String TAG = this.getClass().toString();

    private Flow currentFlow;
        // Flow currently being worked on
    private AppDataManager util;
    private DynamicGridView elementGridView;
    private SandBoxGridAdapter imgAdapater;
    private Toast currentToast = null;
    private Toolbar sbToolbar;
    private LinkedList<FlowElement> gridContent;
    private String menuState;
    private int sortToggle;

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

        elementGridView = (DynamicGridView) findViewById(R.id.drag_drop_gridview);


        gridContent = new LinkedList<>();
        gridContent.addAll(currentFlow.getChildElements());

        imgAdapater = new SandBoxGridAdapter(this, gridContent,3);
            // Passes the number of elements in the Flow's child elements to set the
            // Adapter's initial size
        elementGridView.setAdapter(imgAdapater);


        menuState = AppConstants.MENU_NATIVE;
        sortToggle=0;
//        setGridFunctionalState(AppConstants.GS_DRAG_DROP);
//
////        setGridFunctionalState(AppConstants.GS_MCL_CHECKABLE);
    }

    /**
     * The grid has multiple functional states:
     * --> Drag and Drop Mode
     * --> Selection Deletion Mode
     *
     * Sets the current functional state of the grid based on gridState input
     * @param status current stats of the grid
     */
    private void setGridFunctionalState(String status) {

        if (status.equals(AppConstants.GS_DRAG_DROP)) {
            elementGridView.setOnDropListener(new DynamicGridView.OnDropListener()
        {
            @Override
            public void onActionDrop()
            {
                elementGridView.stopEditMode();
            }
        });

            elementGridView.setOnDragListener(new DynamicGridView.OnDragListener() {
                @Override
                public void onDragStarted(int position) {
                    Log.d("TAG", "drag started at position " + position);
                    //TODO What to do here ?
                }

                @Override
                public void onDragPositionsChanged(int oldPosition, int newPosition) {
                    Log.d("TAG", String.format("drag item position changed from %d to %d", oldPosition, newPosition));
                }
            });
            elementGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    elementGridView.startEditMode(position);
                    return true;
                }
            });

        } else {
//            elementGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
//            elementGridView.setMultiChoiceModeListener(new MultiChoiceListener());
//            elementGridView.setSelector(ContextCompat.getDrawable(SandBoxActivity.this, R.drawable.gridview_selector));
        }

    }

    /* Allows the menu items to appear in the toolbar */
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_sandbox, menu);
        Log.d(TAG, "TEST!");
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
        and passes a MenuItem object to indicate which item was clicked */

        Log.d(TAG, "Execute");
        switch (item.getItemId()) {

            case R.id.action_reorder_elements:
                Log.d(TAG, "Inside action");
                if (sortToggle==0) {
                    menuState=AppConstants.MENU_HIDE;
                    Log.d(TAG, "\n\n Menu is hidden \nsortToggle was 0 \nSetting Title \nInvalidating OptionsMenu \n Changing Grid State");
                    getSupportActionBar().setTitle("Sorting");
                    invalidateOptionsMenu();
                    setGridFunctionalState(AppConstants.GS_DRAG_DROP);
                    sortToggle = 1;
                    return true;
                } else {
                    Log.d(TAG, "\n\n Menu is Native \nsortToggle was 1 \nReseting Setting Title \nInvalidating OptionsMenu \n Changing Grid State to Checkable");
                    menuState=AppConstants.MENU_NATIVE;
                    getSupportActionBar().setTitle(currentFlow.getName());
                    invalidateOptionsMenu();
                    setGridFunctionalState(AppConstants.GS_MCL_CHECKABLE);
                    sortToggle = 0;
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


        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            int selectCount = elementGridView.getCheckedItemCount();

            mode.setSubtitle("" + selectCount + " item(s) selected");

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getSupportActionBar().hide();
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
            getSupportActionBar().show();
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

    @Override
    public void onBackPressed() {
        if (elementGridView.isEditMode()) {
            elementGridView.stopEditMode();
        } else {
            super.onBackPressed();
        }
    }

}

