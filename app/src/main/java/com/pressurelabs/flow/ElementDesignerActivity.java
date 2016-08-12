package com.pressurelabs.flow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


/**
 * Flow_V2
 *
 * @author Robert Simoes, 2016-07-09
 *         Copyright (c) 2016, Robert Simoes All rights reserved.
 *
// This class is the activity for the flow element designer, where the
// User can name and time the new task element
// This class will create the objects and assign them the inputted properties,
// Then pass them back to the Sandbox man to draw and store in the Flow's ArrayList
 */

public class ElementDesignerActivity extends AppCompatActivity {

    // The Element Designer is the resource provider, FlowSandBoxActivity is the blacksmith!
    private Spinner selectTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_element_designer);

        selectTime = (Spinner)findViewById(R.id.select_time);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.select_time,android.R.layout.simple_spinner_item);

        //Specifying each layout for the dropdown items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectTime.setAdapter(adapter);

        setClickListeners();

    }

    private void setClickListeners() {
        selectTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /** Confirms completion of a Flow Element by validating input,
     *  instantiating a new FlowElement object, and passing the
     *  data back to the previous activity.
     * @param view the checkmark clicked in view
     */
    public void doneElement(View view) {
        EditText nameInput = (EditText)findViewById(R.id.nameInput);
        EditText timeInput = (EditText)findViewById(R.id.timeInput);
        String elementName = nameInput.getText().toString();
        String elementTime = timeInput.getText().toString();
        String timeUnits = selectTime.getSelectedItem().toString();

        if (timeUnits==null) {
            timeUnits = FlowElement.DEFAULT_UNITS;
        }

        if (!elementName.equals("") && !elementTime.equals("")) {

            Intent returnData = new Intent();
            FlowElement newElement = new FlowElement(elementName, Integer.parseInt(elementTime), timeUnits);
            returnData.putExtra("newElement", newElement);
            setResult(RESULT_OK, returnData);
            finish();

        } else {
            Toast.makeText(ElementDesignerActivity.this, "Mind checking that name and time again?", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * On "X" pressed, delete the element being created and return to the previous screen
     */
    public void forgetElement(View view) {
        onBackPressed();
    }

    /**
     * On Back Pressed, delete the current element being made and return to previous screen
     */
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED,null);
        super.onBackPressed();
    }
}