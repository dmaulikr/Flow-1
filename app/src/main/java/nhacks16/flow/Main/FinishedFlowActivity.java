package nhacks16.flow.Main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

import nhacks16.flow.R;

public class FinishedFlowActivity extends AppCompatActivity {

    public static final String COMPLEX_PREFS = "COMPLEX_PREFS";
    public static final String USER_FLOWS = "USER_FLOWS";

    private static final String TAG = TheStream.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_flow);


        Flow finishedFlow = getIntent().getParcelableExtra("finishedFlow");
        String completionTime = getIntent().getStringExtra("completionTime");

        finishedFlow.addCompletionToken();
        Log.d(TAG, "Current flow tokens " + finishedFlow.getCompletionTokens());
        TextView msg = (TextView)findViewById(R.id.praise_msg);
        TextView complete = (TextView) findViewById(R.id.flow_name);
        TextView time = (TextView) findViewById(R.id.complete_time);

        String[] array = this.getResources().getStringArray(R.array.praise_msg);
        String randomStr = array[new Random().nextInt(array.length)];

        msg.setText(randomStr);
        complete.setText(finishedFlow.getName() + " was finished in:");
        time.setText(completionTime);

        FlowManagerUtil util =
                ComplexPreferences
                .getComplexPreferences(
                        this,
                        COMPLEX_PREFS,
                        this.MODE_PRIVATE)
                .getObject(USER_FLOWS, FlowManagerUtil.class);
        // Gets the Flow Manager Util saved in TheStream from Complex Preferences

        util.overwriteFlow(finishedFlow.getFlowManagerIndex(), finishedFlow, this);
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
