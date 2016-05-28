package nhacks16.flow.Main;

import android.content.Intent;
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

    // The Element Designer is the resource provider, SandBoxMain is the blacksmith!
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
        String timeUnits = selectTime.getSelectedItem().toString();

        if (timeUnits==null) {
            timeUnits = FlowElement.DEFAULT_UNITS;
        }

        try {
            if (!elementName.equals("") && !elementTime.equals("")) {

                Intent returnData = new Intent();
                FlowElement newElement = new FlowElement(elementName, Double.parseDouble(elementTime), timeUnits);
                returnData.putExtra("newElement", newElement);
                setResult(1, returnData);
                finish();

            } else {
                int rnd = (int)Math.floor(Math.random()*3);
                switch(rnd) {
                    case 0:
                        Toast.makeText(ElementDesigner.this, "Oops, the task needs a name and time!", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(ElementDesigner.this, "Mind checking that name and time again?", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(ElementDesigner.this, "Hmm, that name and time can't be blank!", Toast.LENGTH_LONG).show();
                        break;
                }

            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            //Log.e = ERROR
        }
    }


}