package nhacks16.flow.Main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

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


    public void createElement(View view) {
        // Instantiate a Blank Flow Element
        // Add it in the Flow's LinkedList
        // Get an id (position) and  pass to element designer.
        Intent in = new Intent(SandBoxMain.this, ElementDesigner.class);
        startActivityForResult(in, 1);
            //Starts new activity waiting for the return data
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // No need for request codes just yet

        // This is currently just a pointer to element created in the designer
        // Consider ValueOf in the future?
        FlowElement newElement = data.getParcelableExtra("newElement");
        Log.d(TAG, "Received Parcel Object: " + "'" + newElement.getElementName() + "'");


        Log.d(TAG, "Adding new element to: " + workingFlow.getName() + " ...");
        workingFlow.addElement(newElement);
        newElement.setFlowIndex(workingFlow.getElementCount());

        Log.d(TAG, "Setting Element GSON Key ...");
            /* If the current flow name was "Website Creation",
               The key would be Webs-Elem-1
             */
        String key = workingFlow.getName().substring(0,4) + "-Elem-" + workingFlow.getElementCount();
        newElement.setGsonKey(key);

        saveElement(newElement, newElement.getGsonKey());

        /* Maybe consider saving the ArrayList to GSON, rather than individual elements?
        *  This might avoid, this lengthy task as well as possible memory problems
        *  But most importantly, unique GSONKeys (would we even need GSONKey for array?
        *  Considering that the Flow is already GSON saved... */
        Log.d(TAG, "Testing Shared Preferences....");
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString(newElement.getGsonKey(),"");
        FlowElement fE = gson.fromJson(json, FlowElement.class);

        Log.d(TAG, "SharedPrefs Test Result: \n " + newElement.getGsonKey() + "\n " + json);


        Log.d(TAG, "Displaying current FlowElements in " + workingFlow.getName() + "...\n");
        for (int i = 0; i<workingFlow.getElementCount(); i++) {
            FlowElement elm = workingFlow.findElement(i);
            Log.d(TAG, " " + elm.getElementName() + " \n "
                    + mPrefs.getString(elm.getGsonKey(),""));

        }


    }

    private void saveElement(FlowElement element, String GSONKey) {
        /* Need to create ASYNC Task for this */
        SharedPreferences  mPrefs = getPreferences(MODE_PRIVATE);
        try {
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(element);
            prefsEditor.putString(GSONKey, json);
            prefsEditor.commit();
            Log.d(TAG, "Saved " + element.getGsonKey() + " to Shared Preferences");


            String j = mPrefs.getString(element.getGsonKey(), "");
            FlowElement fE = gson.fromJson(j, FlowElement.class);
            Log.d(TAG, "Here is the JSON Object: " + j);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Intent i= new Intent(SandBoxMain.this, TheStream.class);
        startActivity(i);//starting main activity
        finish();
    }

}