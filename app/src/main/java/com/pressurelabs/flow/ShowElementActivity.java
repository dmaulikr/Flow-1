package com.pressurelabs.flow;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ShowElementActivity extends AppCompatActivity implements ShowElementFragment.onEditPasser{

    private String menuState;
    private Flow flow;
    private ShowElementFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_element);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_show_element);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        menuState = AppConstants.MENU_ITEMS_NATIVE;

        int clickedPosition = getIntent().getIntExtra(AppConstants.EXTRA_POSITION_SELECTED,0);
            // 0 Being default for first element in flow

        flow =
                new AppDataManager(this).load(
                        getIntent().getStringExtra(AppConstants.EXTRA_PASSING_UUID)
                );

        getSupportActionBar().setTitle(flow.getName());

        // Create the adapter that will return a fragment for each of the three

        if (findViewById(R.id.showelement_fragment_container) != null) {
            if (savedInstanceState != null) {

                return;
            }

            fragment = ShowElementFragment.newInstance(
                    flow.getChildAt(clickedPosition)
            );


            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction
                    .add(R.id.showelement_fragment_container, fragment)
                    .commit();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_element, menu);

        MenuItem editElements = menu.findItem(R.id.action_edit_element_info);
        MenuItem cancelEdits = menu.findItem(R.id.action_cancel_edits);
        editElements.setVisible(true);
        cancelEdits.setVisible(false);

        if (menuState.equals(AppConstants.MENU_ITEMS_PARTIAL)) {
            editElements.setVisible(false);
            cancelEdits.setVisible(true);
        }



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_edit_element_info:
                fragment.beginEdits();
                toggleMenuItemsTo(AppConstants.MENU_ITEMS_PARTIAL);

                // create action for editing
                // Notify fragment to swap
                break;

            case R.id.action_cancel_edits:
                fragment.finishEdits(AppConstants.STATUS_CANCELLED);
                toggleMenuItemsTo(AppConstants.MENU_ITEMS_NATIVE);
                break;

            case R.id.action_send_feedback:
                AppUtils.sendFeedback(ShowElementActivity.this);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleMenuItemsTo(String declaredState) {
        if (declaredState.equals(AppConstants.MENU_ITEMS_PARTIAL)) {
            getSupportActionBar().setTitle(R.string.show_elements_editing);
            menuState=AppConstants.MENU_ITEMS_PARTIAL;
            invalidateOptionsMenu();
        } else if (declaredState.equals(AppConstants.MENU_ITEMS_NATIVE)){
            getSupportActionBar().setTitle(flow.getName());
            menuState=AppConstants.MENU_ITEMS_NATIVE;
            invalidateOptionsMenu();
        }

    }

    @Override
    public void onEditsPassed(Bundle b, String status) {
        if (status.equals(AppConstants.STATUS_CONFIRM_CANCEL)) {
            toggleMenuItemsTo(AppConstants.MENU_ITEMS_NATIVE);
        } else if (status.equals(AppConstants.STATUS_CONFIRM_EDITS)){
            modifyElementData(b);
            new AppDataManager(this).overwrite(flow.getUuid(),flow);

        }
    }

    private void modifyElementData(Bundle b) {
        fragment.getCurrentElement().setElementName(b.getString(AppConstants.KEY_NEW_NAME));
        fragment.getCurrentElement().setTimeEstimate(Integer.parseInt(b.getString(AppConstants.KEY_NEW_TIME)));
        fragment.getCurrentElement().setTimeUnits(b.getString(AppConstants.KEY_NEW_UNITS));
        fragment.getCurrentElement().setElementNotes(b.getString(AppConstants.KEY_NEW_NOTES));
    }

    public void onFinishedEdits(View v) {
        fragment.finishEdits(AppConstants.STATUS_COMMIT_EDITS);
        menuState=AppConstants.MENU_ITEMS_NATIVE;
        invalidateOptionsMenu();
    }


    //TODO Add Editing Popup Function - Change Title to Editing
    //TODO Swap toggle button for something else
    //TODO Adds dates to tasks?
    //TODO Add Animations and transitions
}
