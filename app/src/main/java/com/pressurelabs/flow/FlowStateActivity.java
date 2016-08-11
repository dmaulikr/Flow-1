package com.pressurelabs.flow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
 * FlowStateActivity is the container for the FlowElementFragments when in FlowState
 */

public class FlowStateActivity extends AppCompatActivity
        implements FlowElementFragment.OnFragmentSelectedListener, FlowElementFragment.OnDataPass {

    private Flow parentFlow;
    private int currentElement =0;
    private Integer[] millisInFlow;
        // Holds each currentElement's completetion time matching to it's Flow Location
    private FlowElementFragment fragment;
    private int endFlag;
    private static final int FINISHED = 1;
    private static final int NOT_FINISHED = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_state);

        parentFlow = getIntent().getParcelableExtra("parent");
        millisInFlow = new Integer[parentFlow.getChildElements().size()];
        endFlag=NOT_FINISHED;
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.flowstate_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {

                return;
            }

            fragment = FlowElementFragment.newInstance(
                    parentFlow.getChildElements().get(currentElement)
            );

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction
                    .add(R.id.flowstate_fragment_container, fragment)
                    .commit();

        }


    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Your current Flow will be cancelled.")
                .setCancelable(false)
                .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showCustomQuitingToast(FlowStateActivity.this);
                        FlowStateActivity.this.finish();
                    }
                })
                .setNegativeButton("No Don't!", null)
                .show();
    }

    private void showCustomQuitingToast(Context context) {
        String[] array = context.getResources().getStringArray(R.array.quit_quotes);
        String randomStr = array[new Random().nextInt(array.length)];

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.quit_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.quote);
        text.setText(randomStr);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    @Override
    public void onNextSelected(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        try {
            fragment = FlowElementFragment.newInstance(
                    parentFlow
                            .getChildElements().get(
                            ++currentElement
                    )
            );

            transaction.setCustomAnimations(
                    R.animator.card_flip_right_in,
                    R.animator.card_flip_right_out,
                    R.animator.card_flip_left_in,
                    R.animator.card_flip_left_out)
                    .replace(R.id.flowstate_fragment_container, fragment)
                    .commit();

            } catch (IndexOutOfBoundsException e) {
                /* Index Out of Bounds Exception Thrown When Flow Ends */

                if (fragment!=null) {
                    endFlag=FINISHED;
                    transaction.remove(fragment);
                    transaction.commit();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                }

            }
    }



    @Override
    public void onDataPass(Bundle recievedData, int elementNumber) {
        /* onDataPass is called after onNextSelected is called,
           therefore currentElement is updated to the next element location
           while passData is sending bundle related to previous element
           therefore currentElement-1 (basically a count)
         */
        if (currentElement!=0) {
            millisInFlow[currentElement-1]= recievedData.getInt(
                    String.valueOf(elementNumber));
        }

        if (endFlag==FINISHED) {
           goToFinishScreen();
        }
    }

    private void goToFinishScreen() {
        finish();
        Intent i = new Intent(this, FinishedFlowActivity.class);
        i.putExtra("finishedFlow", parentFlow);
        i.putExtra("completionTime", this.calculateTimeInFlow());
        startActivity(i);
    }

    private String calculateTimeInFlow() {
        int time=0;

        for (int i = 0; i< millisInFlow.length; i++) {
            time = time + millisInFlow[i];
        }

        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time)
                        - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(time)
                ),
                TimeUnit.MILLISECONDS.toSeconds(time)
                        - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(time)
                )

        );
    }

    @Override
    public void onMoreTimeSelected(View v) {
        final EditText inMinutes = new EditText(this);
        final AlertDialog.Builder customDialog = customDialog(inMinutes);

        customDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (inMinutes.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(FlowStateActivity.this, "Zero minutes... you're messing with me!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(FlowStateActivity.this, "Time to update", Toast.LENGTH_LONG).show();
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

    private AlertDialog.Builder customDialog(EditText inTime) {
        final EditText in = inTime;
        final AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(FlowStateActivity.this);
        new Runnable() {
            @Override
            public void run() {
                //Sets up Layout Parameters
                LinearLayout layout = new LinearLayout(FlowStateActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMarginStart(42);
                params.setMarginEnd(50);


                //Sets up length and 1 line filters
                in.setInputType(InputType.TYPE_CLASS_NUMBER);

                in.setFilters(new InputFilter[] {
                        new InputFilter.LengthFilter(3)
                });

                //Adds the ET and params to the layout of the dialog box
                layout.addView(in, params);

                newFlowDialog.setTitle("How many more minutes?");

                newFlowDialog.setView(layout);
            }
        }.run();

        return newFlowDialog;
    }


}
