package nhacks16.flow.Main;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private OnFragmentSelectedListener mCallback;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void cancelFlowState() {
        elementTimer.cancel();
    }

    // Container Activiy must implement this interface
    public interface OnFragmentSelectedListener {
            public void onOptionSelected(int position);
        }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.running_element_fragment, container, false);

        TextView name = (TextView) view.findViewById(R.id.task_name);
        timeDisplay = (TextView) view.findViewById(R.id.time_display);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

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
                TextView notFinished = (TextView) getView().findViewById(R.id.not_finished);
                notFinished.setVisibility(View.VISIBLE);
                notFinished.animate().alpha(1.0f).setDuration(300);
                timeDisplay.setText("Finished!");
            }
        };
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                elementTimer.start();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Ensures container activity implements callback interface!

        try {
            mCallback = (OnFragmentSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement the appropriate interface");
        }
    }

    public FlowElementFragment() {

    }


}
