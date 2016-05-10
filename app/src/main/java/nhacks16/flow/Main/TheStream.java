package nhacks16.flow.Main;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import nhacks16.flow.R;

public class TheStream extends AppCompatActivity {
    // The Stream is basically a feed where a user can select the specific Flow that they wish to work on, view or create

    private static final String TAG = TheStream.class.getName();
    private Toolbar streamToolbar;
    private Flow newFlow; //Blank flow object declared.
    private static ArrayList<Flow> flowsInStream = new ArrayList<>();

    private FlowArrayAdapter helperAdapter;
    private ListView lv;

    /** UI Actions and Set up */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_stream);

        //Sets up toolbar
        streamToolbar = (Toolbar) findViewById(R.id.streamToolbar);
        setSupportActionBar(streamToolbar);

        // Must instantiate in oncreate, but variable is declare global!
        helperAdapter = new FlowArrayAdapter(this, flowsInStream);

        // Attach the adapter to a ListView
        lv = (ListView) findViewById(R.id.streamFeed);
        lv.setAdapter(helperAdapter);

        setItemOnClicks();

    }

    private void setItemOnClicks() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = lv.getItemAtPosition(position);
                Flow selectedFlow = (Flow)o;

                Intent i = new Intent(TheStream.this, SandBoxMain.class);

                i.putExtra("selectedFlow", selectedFlow);

                startActivity(i);

            }
        });
    }


    /* Allows the menu items to appear in the toolbar */
   @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thestream, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /* Invokes methods based on the icon picked in the toolbar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* When the user selects one of the app bar items, the system
        calls your activity's onOptionsItemSelected() callback method,
        and passes a MenuItem object to indicate which item was clicked */

        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_newFlow:
                    flowDialog();


                // Find out a way to send the flow object OR create the object in the Sandbox by recieving the name

                //Intent goToSandBox = new Intent(TheStream.this, SandBoxMain.class);
                // goToSandBox.putExtra("NEW_FLOW_OBJECT", newFlow);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return false;
                //return super.onOptionsItemSelected(item);

        }
    }


    /** Creation of new Flow Object and Dialogs */
    public void flowDialog() {
        //Creates dialog box asking for name for the new flow
        AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(TheStream.this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMarginStart(45);
        params.setMarginEnd(50);

        //Create edit text field for name entry
        final EditText nameInputET = new EditText(TheStream.this);
        //Sets maximum length of the EditText
        nameInputET.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        nameInputET.setMaxLines(1);
        nameInputET.setLines(1);
        //Adds the ET and params to the layout of the dialog box
        layout.addView(nameInputET, params);

        newFlowDialog.setTitle("Name your new Flow.");

        newFlowDialog.setView(layout);


        newFlowDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (nameInputET.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(TheStream.this, "Every Flow deserves a good name :(", Toast.LENGTH_LONG).show();

                            flowDialog(); //Recall the dialog
                        } else {
                            // Sets name of flow object
                            addToStream(instantiateFlow(nameInputET.getText().toString()));
                            //Just a fancy 1 liner really means:
                              // instantiateFlow(String input) => returns newFlow, addToStream(newFlow)
                        }
                    }
                });

        newFlowDialog.setNegativeButton("Nevermind",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        //Display Alert
        newFlowDialog.show();

    }

    private Flow instantiateFlow(String userInput) {
        //Instantiates the newFlow object.
        newFlow = new Flow(userInput);
        newFlow.setTotalTime(0.0);
        return newFlow;
    }

    private void addToStream(Flow flow) {
        flowsInStream.add(flow);
        helperAdapter.notifyDataSetChanged();
        //... Add update adapter .add()
        //... Intent to go to that Flow object
    }


}


