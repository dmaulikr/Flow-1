package nhacks16.flow.Main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Random;

import nhacks16.flow.R;

public class FlowStateActivity extends AppCompatActivity
        implements FlowElementFragment.OnFragmentSelectedListener {
    private TextView tv;
    private Flow parentFlow;
    private FlowElement fElement;
    private FragmentManager frManager;
    private FragmentTransaction frTransaction;

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
            FlowElementFragment fragment = FlowElementFragment.newInstance(
                    parentFlow.getChildElements().get(0)
            );

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            frManager = getFragmentManager();
            frTransaction = frManager.beginTransaction();

            frTransaction.add(R.id.flowstate_fragment_container, fragment)
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
        FlowElementFragment fragment =
                (FlowElementFragment)
                        frManager.findFragmentById(R.id.flowstate_fragment_container);

        fragment.cancelFlowState();
        showCustomQuitingToast(this);
        finish();
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
    public void onOptionSelected(int position) {
        // User selected a thumbs up, down or start option

    }

    public void next(View view) {
        //TODO implement a next fragment feature which communicates with activity and callback to render new fragment
    }

}
