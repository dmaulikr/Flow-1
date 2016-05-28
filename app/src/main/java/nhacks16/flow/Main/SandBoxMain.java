package nhacks16.flow.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import nhacks16.flow.R;

public class SandBoxMain extends AppCompatActivity {
    // The SandBox serves as a hub for the flows, with several functions:
    // 1) Executing the elementDesigner activity
    // 2) Performing the drawing actions of the element on the view itself
    // 4) Executing the elementDetails activity
    // 5) **Eventually** executing the Pomodoro/flowState activity, where the flow begins and the timer counts down
    ///// and the user can specify if they've finished the task move to next activity || need more time (+why) || ask help (slack)


    private static final String TAG = SandBoxMain.class.getName();
    private Flow workingFlow;
        // Flow currently being worked on
    private FlowElement f;
        // Global FlowElement holder


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar sbToolbar;
        super.onCreate(savedInstanceState);
            // Not keeping Flow Object's in their own capsules
        workingFlow = getIntent().getParcelableExtra("selectedFlow");
        setTitle(workingFlow.getName());
        // Careful! If this is NULL then the title will be NULL! (if != null) { exec? }
        setContentView(R.layout.activity_sand_box_main);

        sbToolbar = (Toolbar) findViewById(R.id.sbToolbar);
        setSupportActionBar(sbToolbar);

    }


    public void newElement(View view) {
        // Instantiate a Blank Flow Element
        // Add it in the Flow's LinkedList
        // Get an id (position) and  pass to element designer.
            f = new FlowElement();
            workingFlow.addElement(f);
            Log.d(TAG, "The number of Flow Elements in the current Flow is:  " + workingFlow.getElementCount());

            f.setId(workingFlow.findElement(f));
            Log.d(TAG, "The newly created blank Flow Id is: " + f.getId());


        Intent in = new Intent(SandBoxMain.this, ElementDesigner.class);

        startActivityForResult(in, 1);
            //Starts new activity waiting for the return data
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // No need for request codes just yet

        FlowElement helper = data.getParcelableExtra("newElement");
        Log.d(TAG, "data obj is: " + helper.getElementName() + " " + helper.getTimeUnits());
        // SAVE A NEW FLOW ELEMENT TO GSON ??
        // Override f??
            f.setElementName(String.valueOf(helper.getElementName()));
            f.setTimeEstimate(Double.valueOf(helper.getTimeEstimate()));
            f.setTimeUnits(String.valueOf(helper.getTimeUnits()));
            //Using valueOf because don't want to simply reference the data, actually want to copy it
            Log.d(TAG, "The new flowElement has been created: "
                    + f.getElementName() + " " + f.getId() + " "
                    + f.getTimeUnits() + " " + f.getTimeEstimate());
    }

    @Override
    public void onBackPressed() {
        Intent i= new Intent(SandBoxMain.this, TheStream.class);
        startActivity(i);//starting main activity
        finish();
    }

}