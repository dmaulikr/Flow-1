package nhacks16.flow.Main;

import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import nhacks16.flow.R;

public class FlowStateActivity extends AppCompatActivity {
    private TextView tv;
    private Flow parentFlow;
    private FlowElement fElement;
    private Fragment fragment;
    private FragmentManager frManager;
    private FragmentTransaction frTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_state);

        parentFlow = getIntent().getParcelableExtra("parent");

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            fragment = FlowElementFragment.newInstance(
                    parentFlow.getChildElements().get(0)
            );

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            frManager = getFragmentManager();
            frTransaction = frManager.beginTransaction();

            frTransaction.add(R.id.fragment_container, fragment)
                    .commit();

        }

        // Get element from Sandbox
        // get time of element set count down to element.getTime
        // Set name of the element textview using element.getName
        // Start timer and use Progress Bar Class Android https://developer.android.com/reference/android/widget/ProgressBar.html
        // If timer runs out, show thumbs down need more time
        // If finished early parcel and send a .next() of the element to the new fragment?


    }
}
