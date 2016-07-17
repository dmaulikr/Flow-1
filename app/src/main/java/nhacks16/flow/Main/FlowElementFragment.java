package nhacks16.flow.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private CountDownTimer elementTimer;
    private ProgressBar progressBar;
    private FlowElement element;
    private int progress;
    private TextView timeDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_flow_state, container, false);

        TextView name = (TextView) view.findViewById(R.id.taskName);
        timeDisplay = (TextView) view.findViewById(R.id.timeDisplay);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        element = (FlowElement)getArguments().get(FLOW_ELEMENT);
        progress = (int) element.parseTimeToSecs();
            /* onTick occurs every 1000ms (1s), therefore need progress to tick at that rate
               but because using progress--, need to convert the milis to seconds to subtract
             */
        progressBar.setMax(progress);

        name.setText(
                element.getElementName()
        );

        progress = progressBar.getMax();
        progressBar.setProgress(progress);

        startTimer();

        // TODO determine how to make the process bar fall proportionally to the time decrease

        return view;

    }



    public static FlowElementFragment newInstance(FlowElement e) {
        Bundle args = new Bundle();
        args.putParcelable(FLOW_ELEMENT, e);
        FlowElementFragment fragment = new FlowElementFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void startTimer() {
        elementTimer = new CountDownTimer(
                element.parseTimeToMiliSecs(),
                1000) {

            public void onTick(long millisUntilFinished) {
                progressBar.setProgress(progress--);

                timeDisplay.setText(
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
                // Stuff
                timeDisplay.setText("Finished!");
            }
        };
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                elementTimer.start();
            }
        });
        // TODO STOP runOnUiThread ON BACK PRESSED
    }

    public FlowElementFragment() {

    }
}
