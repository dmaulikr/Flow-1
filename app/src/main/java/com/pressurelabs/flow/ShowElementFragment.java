package com.pressurelabs.flow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-09-03
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */


public class ShowElementFragment extends Fragment {

    private onEditPasser mEditCallback;
    private Context mContext;
    private EditText rename;
    private EditText retime;
    private TextView name;
    private TextView time;

    public FlowElement getCurrentElement() {
        return currentElement;
    }

    private FlowElement currentElement;
    /**
     * The fragment argument representing the section number for this
     * fragment.*/

    public ShowElementFragment() {
    }// Blank Constructor

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ShowElementFragment newInstance(FlowElement inElement) {
        ShowElementFragment fragment = new ShowElementFragment();
        Bundle args = new Bundle();
        args.putParcelable(AppConstants.EXTRA_ELEMENT_PARCEL, inElement);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_element, container, false);

        ToggleButton units = (ToggleButton) rootView.findViewById(R.id.fragment_se_units_toggle);

        name = (TextView) rootView.findViewById(R.id.switcher_se_name_TV);

        time = (TextView) rootView.findViewById(R.id.switcher_se_time_TV);

        TextView notes = (TextView) rootView.findViewById(R.id.switcher_se_notes_TV);

        currentElement = getArguments()
                .getParcelable(AppConstants.EXTRA_ELEMENT_PARCEL);


        toggleButtonState(units, false);

        name.setText(currentElement.getElementName());

        notes.setText("Notes: " + currentElement.getElementNotes());

        switch (currentElement.getTimeUnits()) {
            case "minutes":
                time.setText(
                        String.valueOf(
                                AppUtils.calcRemainderMins(
                                        currentElement.getTimeEstimate()
                                )
                        )
                );
                break;
            case "hours":
                time.setText(
                        String.valueOf(
                                AppUtils.calcHours(
                                        currentElement.getTimeEstimate()
                                )
                        )
                );
                break;
            default:
                break;
        }

        return rootView;
    }

    private void toggleButtonState(ToggleButton units, boolean disabled) {
        units.setClickable(disabled);

        if (currentElement.getTimeUnits().equals(AppConstants.UNIT_MINUTES)) {
            units.setChecked(false);
        } else {
            units.setChecked(true);
        }

    }

    private void passEdits(Bundle b, String status){
        mEditCallback.onEditsPassed(b, status);
    }

    public void finishEdits(String status) {
        //TODO refactor to viewflipper
        ViewSwitcher switcherName = (ViewSwitcher) getView().findViewById(R.id.fragment_se_switcher_name);
        ViewSwitcher switcherTime = (ViewSwitcher) getView().findViewById(R.id.fragment_se_switcher_time);
        ViewSwitcher switcherNotes = (ViewSwitcher) getView().findViewById(R.id.fragment_se_switcher_notes);
        ToggleButton units = (ToggleButton) getView().findViewById(R.id.fragment_se_units_toggle);
        toggleButtonState(units, false);

        Bundle b = new Bundle();
        if (status.equals(AppConstants.STATUS_CANCELLED)) {

            switcherName.showNext();
            switcherTime.showNext();
            switcherNotes.showNext();



            Button btn = (Button) getView().findViewById(R.id.fragment_se_finished_but);
            btn.setVisibility(View.INVISIBLE);

            passEdits(b, AppConstants.STATUS_CONFIRM_CANCEL);
            OGGlassesAnimation(0);

        } else if (status.equals(AppConstants.STATUS_COMMIT_EDITS)) {

            if (rename.getText().toString().equals("") ||
                    retime.getText().toString().equals("") ||
                    retime.getText().toString().equals("0")) {
                Toast.makeText(mContext, R.string.designer_text_validation_msg, Toast.LENGTH_LONG).show();

            } else {
                TextView notes = (TextView) getView().findViewById(R.id.switcher_se_notes_TV);
                EditText renotes = (EditText) getView().findViewById(R.id.switcher_se_notes_ET);


                name.setText(
                        rename.getText().toString()
                );

                retime.setText(
                        retime.getText().toString()
                );

                notes.setText(
                        renotes.getText().toString()
                );

                switcherName.showNext();
                switcherTime.showNext();
                switcherNotes.showNext();

            /* Pass data to parent activity */
                String timeUnits = returnUnitValue(units);
                b.putString(AppConstants.KEY_NEW_NAME, rename.getText().toString());
                b.putString(AppConstants.KEY_NEW_TIME, retime.getText().toString());
                b.putString(AppConstants.KEY_NEW_UNITS, timeUnits);
                b.putString(AppConstants.KEY_NEW_NOTES, renotes.getText().toString());

                passEdits(b, AppConstants.STATUS_CONFIRM_EDITS);

                Button finished = (Button) getView().findViewById(R.id.fragment_se_finished_but);
                finished.setVisibility(View.GONE);


                OGGlassesAnimation(1);
            }


        }

    }

    private void OGGlassesAnimation(int onOffStatus) {
        ImageView ogMLG = (ImageView) getView().findViewById(R.id.fragment_se_og_glasses);
        Animation slideIn = AnimationUtils.loadAnimation(getActivity(),android.R.anim.slide_in_left);
        Animation fadeout = AnimationUtils.loadAnimation(getActivity(),android.R.anim.fade_out);
        switch (onOffStatus) {
            case 1:
                ogMLG.startAnimation(slideIn);
                ogMLG.setVisibility(View.VISIBLE);
                break;
            case 0:
                ogMLG.startAnimation(fadeout);
                ogMLG.setVisibility(View.VISIBLE);
                break;
        }
    }

    private String returnUnitValue(ToggleButton units) {
        if (units.isChecked()) {
            return AppConstants.UNIT_HOURS;

        } else {
            return AppConstants.UNIT_MINUTES;
        }
    }

    public interface onEditPasser {
        void onEditsPassed(Bundle b,String status);
    }


    public void beginEdits() {
        ViewSwitcher switcherName = (ViewSwitcher) getView().findViewById(R.id.fragment_se_switcher_name);
        ViewSwitcher switcherTime = (ViewSwitcher) getView().findViewById(R.id.fragment_se_switcher_time);
        ViewSwitcher switcherNotes = (ViewSwitcher) getView().findViewById(R.id.fragment_se_switcher_notes);

        rename = (EditText) switcherName.findViewById(R.id.switcher_se_name_ET);
        retime = (EditText) switcherTime.findViewById(R.id.switcher_se_time_ET);
        EditText renotes = (EditText) switcherNotes.findViewById(R.id.switcher_se_notes_ET);

        rename = AppUtils.setNameInputFilters(rename);

        ToggleButton units = (ToggleButton) getView().findViewById(R.id.fragment_se_units_toggle);

        toggleButtonState(units, true);

        renotes.setText(currentElement.getElementNotes());

        rename.setText(currentElement.getElementName());

        switch (currentElement.getTimeUnits()) {
            case "minutes":
                retime.setText(
                        String.valueOf(
                                AppUtils.calcRemainderMins(
                                        currentElement.getTimeEstimate()
                                )
                        )
                );
                break;
            //TODO CALC HOURS NOT WORKING HERE
            case "hours":
                retime.setText(
                        String.valueOf(
                                AppUtils.calcHours(
                                        currentElement.getTimeEstimate()
                                )
                        )
                );
                break;
            default:
                break;
        }

        switcherName.showNext();
        switcherTime.showNext();
        switcherNotes.showNext();

        Button finished = (Button) getView().findViewById(R.id.fragment_se_finished_but);
        finished.setVisibility(View.VISIBLE);


    }

    /**
     * Ensures that all interface callbacks are assigned context
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Ensures container activity implements callback interface!
        try {
            mEditCallback = (onEditPasser) context;
            this.mContext = context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement the appropriate interface");
        }
    }
}