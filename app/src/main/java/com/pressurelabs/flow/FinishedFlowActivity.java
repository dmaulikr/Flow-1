package com.pressurelabs.flow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import java.util.Random;


public class FinishedFlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_flow);


        String completionTime = getIntent().getStringExtra("completionTime");
        AppDataManager util = new AppDataManager(this);
        Flow finishedFlow = util.load(getIntent().getStringExtra(AppConstants.UUID_SENT));

        // Gets the Flow Manager Util saved in TheHubActivity from Complex Preferences

        finishedFlow.addCompletionToken();

        TextView msg = (TextView)findViewById(R.id.praise_msg);
        TextView complete = (TextView) findViewById(R.id.flow_name);
        TextView time = (TextView) findViewById(R.id.complete_time);

        String[] array = this.getResources().getStringArray(R.array.praise_msg);
        String randomStr = array[new Random().nextInt(array.length)];

        msg.setText(randomStr);
        complete.setText(finishedFlow.getName() + " was finished in:");
        time.setText(completionTime);

        util.overwrite(finishedFlow.getUuid(), finishedFlow);

        // Overwrites current flow in the file
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void completeFlow(View v) {
        onBackPressed();
    }

}
