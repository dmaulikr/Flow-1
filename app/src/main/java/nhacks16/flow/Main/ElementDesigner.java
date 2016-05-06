package nhacks16.flow.Main;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import nhacks16.flow.R;

public class ElementDesigner extends AppCompatActivity {
    // This class is the activity for the flow element designer, where the
    // User can name and time the new task element
    // This class will create the objects and assign them the inputted properties,
    // Then pass them back to the Sandbox man to draw and store in **Either the ArrayList or potentially a database **
    Spinner selectTime;
    ArrayAdapter<CharSequence> adapter;
    private static final String TAG = ElementDesigner.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_designer);

        selectTime = (Spinner)findViewById(R.id.selectTime);
        adapter = ArrayAdapter.createFromResource(this,R.array.selectTime,android.R.layout.simple_spinner_item);

        //Specifying each layout for the dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectTime.setAdapter(adapter);

        selectTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // If user selects item from spinner, this method is invoked
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void saveElement(View view) {
        EditText nameInput = (EditText)findViewById(R.id.nameInput);
        EditText timeInput = (EditText)findViewById(R.id.timeInput);
        String elementName = nameInput.getText().toString();
        String elementTime = timeInput.getText().toString();
        Bundle extras = getIntent().getExtras();
        try {
            if (elementName != null && elementTime != null) {
                if (extras != null) {
                    // returns the element of the id# of element to be created
                    String id = extras.getString("FLOW_ELEMENT_ID");
                    new createNewElementAsync().execute(id,elementName,elementTime);
                }

               // Intent returnToSandbox = new Intent(ElementDesigner.this, SandBoxMain.class);
                
                /* Make the addition of the the flow element to the currentElements as well as
                make the SandBox draw the element.
                Also is this efficient or will it crash constantly? (ie. is doing the async element creation a bad idea??
                 */

            } else {
                Toast.makeText(ElementDesigner.this, "Oops, you've got to name and time your task!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            //Log.e = ERROR
        }
    }

    //Runs Async task to create a new FlowElement Object using user's inputs
    private class createNewElementAsync extends AsyncTask<String, Integer, FlowElement> {


        @Override
        protected FlowElement doInBackground(String... params) {
        // params[0] = id, params[1]=elementName, params[2] = elementTime

            /*int elementId;
            elementId = Integer.parseInt(params[0]);
            Integer f = 22;
            Integer.parseInt(f);
            //parses and passes generated id from SandboxMain activity and saveElement .execute(id)

            String elementName = params[1];
            Double elementTime = Double.parseDouble(params[2]);

            // How to pass this object to the array in SandBox Main??
            FlowElement newElement = new FlowElement(elementId, elementName, elementTime);

            return newElement; */
            Log.d(TAG, "PARAMS 0 = " + params[0]);
            Log.d(TAG, "PARAMS 1 = " +  params[1]);
            Log.d(TAG, "PARAMS 2 = " +  params[2]);

            return null;
        }
    }

}