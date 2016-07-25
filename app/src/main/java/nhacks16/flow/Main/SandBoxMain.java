package nhacks16.flow.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import nhacks16.flow.R;

import java.util.ArrayList;

public class SandBoxMain extends AppCompatActivity {
    // The SandBox serves as a hub for the flows, with several functions:
    // 1) Executing the elementDesigner activity
    // 2) Performing the drawing actions of the element on the view itself
    // 4) Executing the elementDetails activity
    // 5) **Eventually** executing the Pomodoro/flowState activity, where the flow begins and the timer counts down
    ///// and the user can specify if they've finished the task move to next activity || need more time (+why) || ask help (slack)


    private static final String TAG = SandBoxMain.class.getName();

    private Flow currentFlow;
        // Flow currently being worked on
    private FlowManagerUtil util;
    private GridView elementGridView;
    private ImageAdapter imgAdapater;
    private Toast currentToast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar sbToolbar;
        super.onCreate(savedInstanceState);
            // Not keeping Flow Object's in their own capsules
        currentFlow = getIntent().getParcelableExtra("selectedFlow");
        setTitle(currentFlow.getName());
        // Careful! If this is NULL then the title will be NULL! (if != null) { exec? }
        setContentView(R.layout.activity_sand_box_main);

        sbToolbar = (Toolbar) findViewById(R.id.sbToolbar);
        setSupportActionBar(sbToolbar);

        final ArrayList<Integer> elementGrid = new ArrayList<>();
        for (int i = 0; i< currentFlow.getElementCount(); i++){
            elementGrid.add(R.drawable.empty_task_large);
        }

        elementGridView = (GridView) findViewById(R.id.e_visual_grid);

        imgAdapater = new ImageAdapter(this, elementGrid);
            // Passes the number of elements in the Flow's child elements to set the
            // Adapter's initial size
        elementGridView.setAdapter(imgAdapater);
        util = new FlowManagerUtil();

    }

    @Override
    protected void onResume() {
        super.onResume();

        rebuildSandbox();
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
        FlowElement newElement;

        if(resultCode==RESULT_OK) {
            newElement = data.getParcelableExtra("newElement");
            Log.d(TAG, "Received Parcel Object: " + "'" + newElement.getElementName() + "'");

            saveElementToFlow(newElement);
        }

    }

    /** Adds the newElement passed via parameter to the currentFlow's ArrayList
     *  while setting its FlowIndex and UpdatingSandBox Count
     *
     * @param newElement the FlowElement being saved
     */
    private void saveElementToFlow(FlowElement newElement) {
        Log.d(TAG, "Adding new element to: " + currentFlow.getName() + " ...");
        newElement.setLocation(currentFlow.getElementCount());
        currentFlow.addElement(newElement);


        Log.d(TAG, "Updating Sandbox...");
        updateSandBox();

        util.overwriteFlow(currentFlow.getFlowArrayIndex(), currentFlow, this);
            // Saves the updated JSONFlowWrapper ArrayList as
            // the updated list (acts as the updated list
    }

    /**
     * Sets the onClick action when an element in the grid is clicked.
     */
    private void setClickListeners() {
        elementGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String eName = currentFlow.returnElement(position).getElementName();
                int eTime = currentFlow.returnElement(position).getTimeEstimate();
                String eUnits = currentFlow.returnElement(position).getTimeUnits();

                showToast("" + eName + "\n" + eTime + " " + eUnits);
            }
        });
    }

    private void showToast(String text)
    {
        if(currentToast != null)
        {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        currentToast.show();
    }


    /**
     *  Rebuilds the SandBox's Gridview based on the Flow's ChildElements
     *  and sets them as clickable.
     */
    private void rebuildSandbox() {
        setClickListeners();
    }

    /**
     *  Updates the SandBox's GridView after a single element has been created
     */
    private void updateSandBox() {
        imgAdapater.addOne();
        imgAdapater.notifyDataSetChanged();

        setClickListeners();
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

    public void goFlowState(View v) {
        if (currentFlow.getChildElements().isEmpty()) {
            this.showToast(
                    "Look's like there is no elements yet!"
            );
        } else {
            Intent in = new Intent(this, FlowStateActivity.class);
            in.putExtra("parent", currentFlow);
            startActivity(in);
        }
    }
}
