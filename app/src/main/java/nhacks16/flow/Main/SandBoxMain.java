package nhacks16.flow.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

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
    private TextView tv;

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

        tv = (TextView)findViewById(R.id.number_flow_elements);

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSandboxCount();
    }

    /** Launches a new Element Designer Activity waiting to receive a new
     *  FlowElement Object back as a Parcel
     *
     * @param view the FAB clicked to launch the function
     */
    public void createElement(View view) {
        Intent in = new Intent(SandBoxMain.this, ElementDesigner.class);
        startActivityForResult(in, 1);
            //Starts new activity waiting for the return data
    }

    /** Receives Parcel Object from previous Element Designer Activity
     *  and creates a reference object to point to the Parcel
     *
     * @param requestCode
     * @param resultCode
     * @param data the Parcelled object stored as intent data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // This is currently just a pointer to element created in the designer
        // Consider ValueOf in the future?
        FlowElement newElement = data.getParcelableExtra("newElement");
        Log.d(TAG, "Received Parcel Object: " + "'" + newElement.getElementName() + "'");

        saveElementToFlow(newElement);

        newElement = null;

    }

    /** Adds the newElement passed via parameter to the workingFlow's ArrayList
     *  while setting its FlowIndex and UpdatingSandBox Count
     *
     * @param newElement the FlowElement being saved
     */
    private void saveElementToFlow(FlowElement newElement) {
        Log.d(TAG, "Adding new element to: " + workingFlow.getName() + " ...");
        workingFlow.addElement(newElement);
        newElement.setFlowIndex(workingFlow.getElementCount());
        updateSandboxCount();
    }

    /**
     * Handles the onBackPressed event by sending user to TheStream
     */
    @Override
    public void onBackPressed() {
        Intent i= new Intent(SandBoxMain.this, TheStream.class);
        startActivity(i);//starting main activity
        finish();
    }

    /**
     *  Updates the Element Count for the current flow being worked on
     */
    private void updateSandboxCount(){
        int count = workingFlow.getElementCount();
        tv.setText(String.valueOf(count));
    }


}