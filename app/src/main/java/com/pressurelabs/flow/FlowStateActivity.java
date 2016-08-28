package com.pressurelabs.flow;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
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
 * FlowStateActivity is the container for the FlowElementFragments when in FlowState.
 *
 * Flowstate is essentially the task completion activity for the Flow. A single Fragment containing
 * the time to complete and a progress bar are shown, while a single option "NEXT" is provided in case
 * the user completes the task early and wishes to moveon.
 */

public class FlowStateActivity extends AppCompatActivity
        implements FlowElementFragment.OnFragmentSelectedListener, FlowElementFragment.OnDataPass {

    private Flow parentFlow;
    private int currentElement;
    private Integer[] millisInFlow;
        // Holds each currentElement's completetion time matching to it's Flow Location
    private FlowElementFragment fragment;
    private int flowStateFlag;
    private String activityStateFlag;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_state);

        parentFlow = new AppDataManager(this).load(getIntent().getStringExtra(AppConstants.PASSING_UUID));

        millisInFlow = new Integer[parentFlow.getChildElements().size()];
        flowStateFlag =AppConstants.NOT_FINISHED;
        activityStateFlag = AppConstants.FS_UI_ACTIVE;
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.flowstate_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {

                return;
            }

            currentElement=0; //Location of current element

            fragment = FlowElementFragment.newInstance(
                    parentFlow.getChildElements().get(currentElement)
            );


            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction
                    .add(R.id.flowstate_fragment_container, fragment)
                    .commit();

        }

        mNotifyMgr =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onBackPressed() {


        new AlertDialog.Builder(this)
                .setTitle("Your current Flow will be cancelled.")
                .setCancelable(false)
                .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fragment.notifyBackPressed();
                        flowStateFlag=AppConstants.EARLY_EXIT;
                        FlowStateActivity.super.onBackPressed();

                        showCustomQuitingToast(FlowStateActivity.this);
                    }
                })
                .setNegativeButton("No Don't!", null)
                .show();


    }


    /**
     * Displays custom toast with random phrase from resource file.
     * @param context
     */
    private void showCustomQuitingToast(Context context) {
        String[] array = context.getResources().getStringArray(R.array.quit_quotes);
        String randomStr = array[new Random().nextInt(array.length)];

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.quit_toast,
                (ViewGroup) findViewById(R.id.toast_layout_container));

        TextView text = (TextView) layout.findViewById(R.id.quote);
        text.setText(randomStr);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }


    /**
     * Retrieves the time taken to complete the task while creating a new fragment for the next task.
     *
     * If there is no final task an exception is thrown and the Flow is completed
     * @param v
     */
    @Override
    public void onNextSelected(View v) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        try {
            fragment.cancelTimerAndPassData();

            fragment = FlowElementFragment.newInstance(
                    parentFlow
                            .getChildElements().get(
                            ++currentElement
                    )
            );

            transaction.setCustomAnimations(
                    android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.flowstate_fragment_container, fragment)
                    .commit();

            } catch (IndexOutOfBoundsException e) {
                /* Index Out of Bounds Exception Thrown When Flow Ends */

                if (fragment!=null) {
                    flowStateFlag =AppConstants.FINISHED;
                    transaction.remove(fragment);
                    transaction.commit();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                }

            }
        if (flowStateFlag ==AppConstants.FINISHED){
            goToFinishScreen();
        }
    }


    /**
     * Parses recieved data and adds to the Integer[] tracking the amount of time taken for each
     * task to complete
     *
     * @param recievedData , a bundle containing the desired completion time data
     * @param elementNumber , the location of the element in the flow and also the string used as a key
     */
    @Override
    public void onDataPass(Bundle recievedData, int elementNumber) {
        /* onDataPass is called after onNextSelected is called,
           therefore currentElement is updated to the next element location
           while passData is sending bundle related to previous element
           therefore currentElement-1 (basically a count)
         */

        millisInFlow[currentElement] = recievedData.getInt(
                String.valueOf(elementNumber));
    }

    private void goToFinishScreen() {
        Intent i = new Intent(this, FinishedFlowActivity.class);
        i.putExtra(AppConstants.PASSING_UUID, parentFlow.getUuid());
        i.putExtra("completionTime", this.calculateTimeInFlow());
        finish();
        startActivity(i);
    }

    /**
     * Iterates through the Integer[] and returns a formatted String time output of the time
     * taken to complete all tasks in the flow
     *
     * @return
     */
    private String calculateTimeInFlow() {
        int time = 0;

        for (int i = 0; i <=millisInFlow.length-1; i++) {
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

            in.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(3)
            });

            //Adds the ET and params to the layout of the dialog box
            layout.addView(in, params);

            newFlowDialog.setTitle("How many more minutes?");

            newFlowDialog.setView(layout);

        return newFlowDialog;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (flowStateFlag !=AppConstants.FINISHED && flowStateFlag != AppConstants.EARLY_EXIT) {
            onPauseNotifier();
        }



    }

    /**
     * Sets up and sends out a notification to the user keeping track of current time in Flow
     * also notifies current fragment.
     *
     */
    private void onPauseNotifier() {
        mBuilder = buildNotification();
        activityStateFlag = AppConstants.FS_NOTIFICATION_ACTIVE;
        fragment.notificationsActive(mBuilder);
        // Builds the notification and issues it.
        int mNotificationId = AppConstants.FLOW_STATE_NOTIFICATION_ID;
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }

    /**
     * Generates a notification with the pending intent to send the user to the Flow State at the current task
     * being completed
     *
     * @return
     */
    public NotificationCompat.Builder buildNotification() {
        Intent notificationIntent = new Intent(getApplicationContext(), FlowStateActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(AppConstants.PASSING_UUID,parentFlow.getUuid());
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.flow_state_notify)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setContentIntent(intent)
                        .setContentTitle(getString(R.string.fs_notification_title))
                        .setContentText("In Flow State")
                        .setAutoCancel(true)
                        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Gets an instance of the NotificationManager service

        return builder;
    }

    @Override
    protected void onResume() {
        activityStateFlag=AppConstants.FS_UI_ACTIVE;
        fragment.uiActive();

        super.onResume();

    }

}
