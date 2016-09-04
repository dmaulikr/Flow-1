package com.pressurelabs.flow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.ViewSwitcher;

/**
 * Flow_app
 *
 * @author Robert Simoes, 2016-09-03
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */


public class ShowElementFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public ShowElementFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ShowElementFragment newInstance(FlowElement element) {
        ShowElementFragment fragment = new ShowElementFragment();
        Bundle args = new Bundle();
        args.putParcelable(AppConstants.EXTRA_ELEMENT_PARCEL, element);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_show_element, container, false);

        TextView name = (TextView) rootView.findViewById(R.id.switcher_show_element_name);

        TextView time = (TextView) rootView.findViewById(R.id.switcher_show_element_time);

        FlowElement f = getArguments()
                .getParcelable(AppConstants.EXTRA_ELEMENT_PARCEL);

        name.setText(f.getElementName());

        switch (f.getTimeUnits()) {
            case "minutes":
                time.setText(
                        String.valueOf(
                                AppUtils.calcRemainderMins(
                                        f.getTimeEstimate()
                                )
                        )
                );
                break;
            case "hours":
                time.setText(
                        String.valueOf(
                                AppUtils.calcHours(
                                        f.getTimeEstimate()
                                )
                        )
                );
                break;
            default:
                break;
        }

        return rootView;
    }
}