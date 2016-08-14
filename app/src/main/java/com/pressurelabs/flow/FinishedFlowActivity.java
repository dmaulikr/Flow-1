package com.pressurelabs.flow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import java.util.Random;


public class FinishedFlowActivity extends AppCompatActivity {

    private static final String COMPLEX_PREFS = "COMPLEX_PREFS";
    private static final String USER_FLOWS = "USER_FLOWS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_flow);


        Flow finishedFlow = getIntent().getParcelableExtra("finishedFlow");
        String completionTime = getIntent().getStringExtra("completionTime");
        DataManagerUtil util =
                ComplexPreferences
                        .getComplexPreferences(
                                this,
                                COMPLEX_PREFS,
                                MODE_PRIVATE)
                        .getObject(USER_FLOWS, DataManagerUtil.class);
        // Gets the Flow Manager Util saved in TheStreamActivity from Complex Preferences

        finishedFlow.addCompletionToken();

        TextView msg = (TextView)findViewById(R.id.praise_msg);
        TextView complete = (TextView) findViewById(R.id.flow_name);
        TextView time = (TextView) findViewById(R.id.complete_time);

        String[] array = this.getResources().getStringArray(R.array.praise_msg);
        String randomStr = array[new Random().nextInt(array.length)];

        msg.setText(randomStr);
        complete.setText(finishedFlow.getName() + " was finished in:");
        time.setText(completionTime);



        util.overwriteFlow(finishedFlow.getFlowManagerIndex(),finishedFlow, this);
        // Overwrites current flow in the file
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void completeFlow(View v) {
        finish();
    }

}
