package nhacks16.flow.Main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Random;

import nhacks16.flow.R;

public class FinishedFlowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_flow);

        Flow finishedFlow = getIntent().getParcelableExtra("finishedFlow");
        TextView msg = (TextView)findViewById(R.id.praise_msg);
        TextView complete = (TextView) findViewById(R.id.flow_complete);


        String[] array = this.getResources().getStringArray(R.array.praise_msg);
        String randomStr = array[new Random().nextInt(array.length)];

        msg.setText(randomStr);
        complete.setText(finishedFlow.getName() + " was finished in:\n" +
                "                " + finishedFlow.getTime());

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
