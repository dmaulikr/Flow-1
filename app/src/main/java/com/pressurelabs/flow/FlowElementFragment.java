package com.pressurelabs.flow;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 */
public class FlowElementFragment extends Fragment {
    private static final String FLOW_ELEMENT = "FLOW_ELEMENT";
    private ElementTimer elementTimer;
    private ProgressBar progressBar;
    private FlowElement element;
    private int progress;
    private TextView timeDisplay;
    private OnFragmentSelectedListener mCallback;
    private OnDataPass dataPasser;

    // Container Activiy must implement this interface
    public interface OnFragmentSelectedListener {
        void onNextSelected(View view);
        void onMoreTimeSelected(View view);
    }


    public interface OnDataPass {
        void onDataPass(Bundle b, int elementNumber);
    }

//    public interface onFragmentFinished {
//        void onFragmentFinished();
//    }


    public void passData(Bundle b, int elementNumber) {
        dataPasser.onDataPass(b, elementNumber);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onDestroy() {
        cancelTimerAndPassData();

        super.onStop();
    }

    public void cancelTimerAndPassData() {
        if (element.getLocation()<0) {
            // Do not pass Data
            elementTimer.cancel();
        } else {
            Bundle b = new Bundle();
            b.putInt(
                    String.valueOf(element.getLocation()),
                    elementTimer.getTimeFinishedInMilliSecs()
            );
            passData(b, element.getLocation());
            elementTimer.cancel();
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_running_element, container, false);

        TextView name = (TextView) view.findViewById(R.id.task_name);
        timeDisplay = (TextView) view.findViewById(R.id.time_display);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        final TextView next = (TextView) view.findViewById(R.id.next);

        element = (FlowElement)getArguments().get(FLOW_ELEMENT);
        assert element != null;
        progress = (int) element.parseTimeToSecs();
            /* onTick occurs every 1000ms (1s), therefore need progress to tick at that rate
               but because using progress--, need to convert the millis to seconds to subtract
             */
        progressBar.setMax(progress);

        name.setText(
                element.getElementName()
        );

        progress = progressBar.getMax();
        progressBar.setProgress(progress);

        elementTimer= new ElementTimer(element.parseTimeToMilliSecs(), 1000);
        startTimerOnUi();

        new Runnable() {

            @Override
            public void run() {
                new CountDownTimer(2500,1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (getActivity()!=null){
                            Animation fadeinAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.fadein_animation);
                            next.setAnimation(fadeinAnimation);
                            next.setVisibility(View.VISIBLE);
                        } else {
                            next.setVisibility(View.VISIBLE);
                        }
                    }
                }.start();
            }
        }.run();

        return view;

    }



    public static FlowElementFragment newInstance(FlowElement e) {
        Bundle args = new Bundle();
        args.putParcelable(FLOW_ELEMENT, e);
        FlowElementFragment fragment = new FlowElementFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private void startTimerOnUi() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run () {
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
            dataPasser = (OnDataPass) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement the appropriate interface");
        }
    }

    public FlowElementFragment() {

    }

    class ElementTimer extends CountDownTimer {
        public int getTimeFinishedInMilliSecs() {
            return (int) (timeStart-timeRemaining);
        }

        long timeStart;
        long timeRemaining;

        @Override
        public void onTick(long millisUntilFinished) {
            timeRemaining=millisUntilFinished;

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

        @Override
        public void onFinish() {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fadein_animation);
            timeDisplay.setText("Finished!");
            timeDisplay.setAnimation(fadeInAnimation);
            /*
            // Future Version For Now We will have the user self advance to new tasks
            TextView notFinished = (TextView) getView().findViewById(R.id.not_finished);
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_animation);
            notFinished.setVisibility(View.VISIBLE);
            notFinished.startAnimation(fadeInAnimation);
             */
        }

        public ElementTimer (long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            this.timeStart=millisInFuture;
        }

    }
}
