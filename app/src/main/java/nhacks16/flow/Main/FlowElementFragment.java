package nhacks16.flow.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import nhacks16.flow.R;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class FlowElementFragment extends Fragment {
    private static final String FLOW_ELEMENT = "FLOW_ELEMENT";
    private static final String TAG = TheStream.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flow_state, container, false);

        TextView name = (TextView) view.findViewById(R.id.taskName);
        final TextView timer = (TextView) view.findViewById(R.id.taskTimer);

        name.setText(
                (
                (FlowElement)
                        getArguments()
                        .get(FLOW_ELEMENT)
                )
                .getElementName()
        );

        new CountDownTimer(
                (
                        (FlowElement)
                                getArguments()
                                        .get(FLOW_ELEMENT)
                )
                .parseTimeToMiliSecs(), 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText(
                        String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                        - TimeUnit.HOURS.toMinutes(
                                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                                ),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                        - TimeUnit.MINUTES.toSeconds(
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                )

                        )
                );
            }

            public void onFinish() {
                timer.setText("Finished!");
            }
        }.start();

        // TODO determine how to make the process bar fall proportionally to the time decrease

        return view;

    }

    public FlowElementFragment() {

    }

    public static FlowElementFragment newInstance(FlowElement e) {
        Bundle args = new Bundle();
        args.putParcelable(FLOW_ELEMENT, e);
        FlowElementFragment fragment = new FlowElementFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
