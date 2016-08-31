package com.pressurelabs.flow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Random;


public class FinishedFlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_flow);


        String timeComplete = getIntent().getStringExtra(AppConstants.EXTRA_FORMATTED_TIME);
        int millisInFlow = getIntent().getIntExtra(AppConstants.EXTRA_MILLIS_IN_FLOW,0);

        AppDataManager util = new AppDataManager(this);
        Flow finishedFlow = util.load(getIntent().getStringExtra(AppConstants.EXTRA_PASSING_UUID));

        // Gets the Flow Manager Util saved in TheHubActivity from Complex Preferences

        finishedFlow.addCompletionToken();

        String[] exportData = prepareCSVExport(finishedFlow, millisInFlow);

        TextView msg = (TextView)findViewById(R.id.praise_msg);
        TextView complete = (TextView) findViewById(R.id.flow_name);
        TextView time = (TextView) findViewById(R.id.complete_time);

        String[] array = this.getResources().getStringArray(R.array.praise_msg);
        String randomStr = array[new Random().nextInt(array.length)];

        try {
            msg.setText(randomStr);
            complete.setText(finishedFlow.getName() + " was finished in:");
            time.setText(timeComplete);
            util.overwrite(finishedFlow.getUuid(), finishedFlow);
        } catch (NullPointerException e) {
            Toast.makeText(this, "Woops, couldn't finish the Flow!", Toast.LENGTH_LONG).show();
            this.onBackPressed();
        }




        // Overwrites current flow in the file
    }

    private String[] prepareCSVExport(Flow finishedFlow, int millisInFlow) {
        /*
            ArrayList[0] = flowName;
            ArrayList[1] = childrenCount
            ArrayList[2] = "xH yM" format estimated total time
            ArrayList[3] = completion tokens
         */
        ArrayList<String> exportData = finishedFlow.buildStatsExportList();

//        exportData.add(AppUtils.calculateHours());
        String[] a = {"A","B"};

        return a;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void completeFlow(View v) {
        onBackPressed();
    }

}
