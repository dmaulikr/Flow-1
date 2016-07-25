package nhacks16.flow.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import nhacks16.flow.R;

public class FlowStateActivity extends AppCompatActivity
        implements FlowElementFragment.OnFragmentSelectedListener {

    private static final String TAG = FlowStateActivity.class.getName();
    private Flow parentFlow;
    private int element=0;
    private FlowElementFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_state);

        parentFlow = getIntent().getParcelableExtra("parent");

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.flowstate_fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            fragment = FlowElementFragment.newInstance(
                   parentFlow.getChildElements().get(element)
            );

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.add(R.id.flowstate_fragment_container, fragment)
                    .commit();

        }

        // Get element from Sandbox
        // get time of element set count down to element.getTime
        // Set name of the element textview using element.getName
        // Start timer and use Progress Bar Class Android https://developer.android.com/reference/android/widget/ProgressBar.html
        // If timer runs out, show thumbs down need more time
        // If finished early parcel and send a .next() of the element to the new fragment?


    }

    @Override
    public void onBackPressed() {
        final FlowElementFragment fragment =
                (FlowElementFragment)
                        getFragmentManager().findFragmentById(R.id.flowstate_fragment_container);

        new AlertDialog.Builder(this)
                .setMessage("Your current Flow will be cancelled.")
                .setCancelable(false)
                .setPositiveButton("Understood", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        fragment.cancelTimer();
                        showCustomQuitingToast(FlowStateActivity.this);
                        FlowStateActivity.this.finish();
                    }
                })
                .setNegativeButton("No Don't!", null)
                .show();
    }

    private void showCustomQuitingToast(Context context) {
        String[] array = context.getResources().getStringArray(R.array.quit_quotes);
        String randomStr = array[new Random().nextInt(array.length)];

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.quit_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.quote);
        text.setText(randomStr);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onNextSelected(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            fragment = FlowElementFragment.newInstance(
                    parentFlow
                            .getChildElements().get(
                            ++element
                    )
            );

            transaction.replace(R.id.flowstate_fragment_container, fragment)
                    .commit();
            } catch (IndexOutOfBoundsException e) {
                if (fragment!=null) {
                    transaction.remove(fragment);
                    transaction.commit();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    fragment = null;


                    Intent i = new Intent(this, FinishedFlowActivity.class);
                    i.putExtra("finishedFlow", parentFlow);
                    startActivity(i);
                }

            }



    }

    @Override
    public void onMoreTimeSelected(View v) {
        final EditText inMinutes = new EditText(this);
        final AlertDialog.Builder customDialog = customDialog(inMinutes);

        customDialog.setPositiveButton("Lets Roll",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (inMinutes.getText().toString().equals("")) {
                            // Need to optimize this so that the dialog does NOT disappear and just display toast
                            Toast.makeText(FlowStateActivity.this, "Zero minutes... you're messing with me!", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(FlowStateActivity.this, "Time to update", Toast.LENGTH_LONG).show();
                            //TODO Update the time based on input
                        }
                    }
                });

        customDialog.setNegativeButton("Nevermind",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        customDialog.show();
    }




    private AlertDialog.Builder customDialog(EditText inTime) {
        AlertDialog.Builder newFlowDialog = new AlertDialog.Builder(FlowStateActivity.this);

        //Sets up Layout Parameters
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMarginStart(42);
        params.setMarginEnd(50);


        //Sets up length and 1 line filters
        inTime.setInputType(InputType.TYPE_CLASS_NUMBER);

        inTime.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(3)
        });

        //Adds the ET and params to the layout of the dialog box
        layout.addView(inTime, params);

        newFlowDialog.setTitle("How many more minutes?");

        newFlowDialog.setView(layout);

        return newFlowDialog;
    }
}
