package nhacks16.flow.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import nhacks16.flow.R;

public class SandBoxMain extends AppCompatActivity {
    // The SandBox serves as a hub for the flows, with several functions:
    // 1) Executing the elementDesigner activity
    // 2) Performing the drawing actions of the element on the view itself
    // 4) Executing the elementDetails activity
    // 5) **Eventually** executing the Pomodoro/flowState activity, where the flow begins and the timer counts down
    ///// and the user can specify if they've finished the task move to next activity || need more time (+why) || ask help (slack)


    private static final String TAG = SandBoxMain.class.getName();
    private Toolbar sbToolbar;
    private Flow workingFlow;
        // The Flow currently being worked on


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workingFlow = getIntent().getParcelableExtra("selectedFlow");
        setTitle(workingFlow.getName());
            // Careful! If this is NULL then the title will be NULL! (if != null) { exec? }
        setContentView(R.layout.activity_sand_box_main);

        sbToolbar = (Toolbar) findViewById(R.id.sbToolbar);
        setSupportActionBar(sbToolbar);

    }

    public void newElement(View view) {
        Intent launchFirst = new Intent(SandBoxMain.this, ElementDesigner.class);
        // Ask the flow how many children it has,
        /*
        // If there are 0 elements in the array, then set the first element id to 1
        if (currentElements.size() < 1) {
            elemId = 1;
            launchFirst.putExtra("FLOW_ELEMENT_ID", elemId);
            startActivity(launchFirst);
        }
        // If there is more than 1 element in the array than make the next ++elementId
        else {
            launchFirst.putExtra("FLOW_ELEMENT_ID", ++elemId);
            startActivity(launchFirst);
        } */
    }

}
