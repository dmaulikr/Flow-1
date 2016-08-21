package com.pressurelabs.flow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

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
    private String menuState;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<Flow> rvContent;
    private PopupWindow longClickPopup, editingPopup;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_the_hub);
        TextView toolbarTitle = (TextView) findViewById(R.id.title_the_hub);
        toolbarTitle.setText("The Hub");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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

        menuState = AppConstants.MENU_NATIVE;

    }

    /* Allows the menu items to appear in the toolbar */
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_the_hub, menu);

        MenuItem newF = menu.findItem(R.id.action_new_flow);
        MenuItem deleteAllF = menu.findItem(R.id.action_delete_flows);
        MenuItem terms = menu.findItem(R.id.terms_of_use);
        if (menuState==AppConstants.MENU_HIDE) {
            newF.setVisible(false);
            deleteAllF.setVisible(false);
            terms.setVisible(false);
        }
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
        AlertDialog.Builder customDialog = generateCustomDialog(nameInputET);

        customDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameInputET.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(TheHubActivity.this, "Every Flow deserves a good name :(", Toast.LENGTH_LONG).show();

                            createNewFlow(); //Recall the dialog
                        } else {

                            Flow newF = new Flow(nameInputET.getText().toString(), 0);

                            if (adapter != null) {
                                rvContent.add(newF);
                                // Set the Flow Manager Index and add to List View Content

                                adapter.notifyDataSetChanged();
                            }

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



    private AlertDialog.Builder generateCustomDialog(EditText nameInputET) {
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
    public void onCardClick(Flow clickedFlow) {
        Intent i = new Intent(TheHubActivity.this, FlowSandBoxActivity.class);

        i.putExtra(AppConstants.UUID_PASSED, clickedFlow.getUuid());
        startActivity(i);
    }

    @Override
    public void onCardLongClick(Flow longClickedFlow, int cardPosition, View cardViewClicked) {
        showLongClickPopUpMenu(longClickedFlow,cardPosition, cardViewClicked);
    }

    private PopupWindow showLongClickPopUpMenu(final Flow longClickedFlow, final int cardPosition, final View cardViewClicked) {

        LayoutInflater layoutInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_window_longclick, null);

        LinearLayout viewGroup = (LinearLayout)  layout.findViewById(R.id.popup_longclick);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(layout, RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);

        int dividerMargin = viewGroup.getDividerPadding(); // Top bottom
        int popupPadding = layout.getPaddingBottom();
        int popupDisplayHeight = -(cardViewClicked.getHeight()-dividerMargin-popupPadding);


        // Prevents border

        popup.setBackgroundDrawable(new ColorDrawable());
        popup.setFocusable(true);

        // Getting a reference to Close button, and close the popup when clicked.
        ImageView delete = (ImageView) layout.findViewById(R.id.popup_delete_item);

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Deletes current Flow from file and UI */
                rvContent.remove(cardPosition);
                manager.delete(longClickedFlow.getUuid());
                adapter.notifyItemRemoved(cardPosition);
                popup.dismiss();
            }
        });

        ImageView edit = (ImageView) layout.findViewById(R.id.popup_edit_item);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
                renameFlow(cardPosition, cardViewClicked);

                // TODO If going down this route change options menu to check ? How to recieve Input, change UI and update file.
            }
        });

        // Displaying the popup at the specified location, + offsets.
        popup.showAsDropDown(cardViewClicked, cardViewClicked.getMeasuredWidth(),popupDisplayHeight, Gravity.TOP);

        longClickPopup = popup;
        return popup;
    }

    private void renameFlow(final int cardPosition, final View cardViewClicked) {
        menuState = AppConstants.MENU_HIDE;
        invalidateOptionsMenu();
        final ViewSwitcher switcher = (ViewSwitcher) cardViewClicked.findViewById(R.id.rename_switcher);
        final EditText rename = (EditText) switcher.findViewById(R.id.item_flow_rename);

        rename.setInputType(InputType.TYPE_CLASS_TEXT);
        //Only allows A-Z, a-z, 0-9, and special characters (%$!@)

        rename.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(20)
        });


        rename.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (rename.hasFocus()) {
                    showEditPopupWindow(rename, cardViewClicked, switcher, cardPosition);
                } else {
                    imm.hideSoftInputFromWindow(rename.getWindowToken(), 0);
                }

            }
        });

        switcher.showNext();

        rename.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        /* Forces keyboard */


    }

    private void showEditPopupWindow(final EditText newName, View cardViewClicked, final ViewSwitcher switcher, final int cardPosition) {
        LayoutInflater layoutInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_window_editing, null);

        LinearLayout viewGroup = (LinearLayout)  layout.findViewById(R.id.popup_editing);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(layout, RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);

        int dividerMargin = viewGroup.getDividerPadding(); // Top bottom
        int popupPadding = layout.getPaddingBottom();
        int popupDisplayHeight = -(cardViewClicked.getHeight()-dividerMargin-popupPadding);


        // Prevents border from appearing outside popupwindow
        popup.setBackgroundDrawable(new ColorDrawable());
        popup.setFocusable(false);

        // Getting a reference to Close button, and close the popup when clicked.
        ImageView confirmEdit = (ImageView) layout.findViewById(R.id.popup_confirm_item_changes);

        confirmEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Flow toChange = rvContent.get(cardPosition);
                if (newName.getText().toString().equals("")) {
                    // Need to optimize this so that the dialog does NOT disappear and just display toast
                    Toast.makeText(TheHubActivity.this, "This Flow needs a name!", Toast.LENGTH_LONG).show();
                } else {
                    toChange.setName(newName.getText().toString());
                    manager.overwrite(toChange.getUuid(), toChange);
                    adapter.notifyDataSetChanged();
                    switcher.showNext();
                    menuState=AppConstants.MENU_NATIVE;
                    invalidateOptionsMenu();
                    popup.dismiss();
                    newName.clearFocus();
                }

            }
        });

        ImageView cancelEdit = (ImageView) layout.findViewById(R.id.popup_cancel_item_changes);

        cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switcher.showNext();
                menuState=AppConstants.MENU_NATIVE;
                invalidateOptionsMenu();
                popup.dismiss();
            }
        });

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        // Displaying the popup at the specified location, + offsets.
        popup.showAsDropDown(cardViewClicked, cardViewClicked.getMeasuredWidth(),popupDisplayHeight, Gravity.TOP);
        editingPopup = popup;
    }


    @Override
    protected void onPause() {
        dismissPopups();
        super.onPause();
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

    private void dismissPopups() {
        if (longClickPopup!=null && longClickPopup.isShowing()) {
            longClickPopup.dismiss();
        }

        if (editingPopup!=null && editingPopup.isShowing()) {
            editingPopup.dismiss();
        }
    }
}
