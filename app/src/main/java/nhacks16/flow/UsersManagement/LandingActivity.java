package nhacks16.flow.UsersManagement;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import nhacks16.flow.R;

public class LandingActivity extends Activity implements View.OnClickListener {

    Button butLogout;
    EditText ETUserName, ETName;
    UserLocalStorage userLocalStorage; //Landing must have access to local store

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        butLogout = (Button) findViewById(R.id.butLogout);
        ETName = (EditText) findViewById(R.id.ETName);
        ETUserName = (EditText) findViewById(R.id.ETUserName);

        butLogout.setOnClickListener(this);

        userLocalStorage = new UserLocalStorage(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (authenticate() == true) {
            displayUserDetails();
        }else{
            startActivity(new Intent(LandingActivity.this, UserLogin.class));
        }
    }

    private boolean authenticate() {
        return userLocalStorage.getUserLoggedIn();
        // Returns true if user logged in
    }

    private void displayUserDetails() {
        User user = userLocalStorage.getLoggedInUser();

        ETUserName.setText(user.userName);
        ETName.setText(user.name);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.butLogout:
                userLocalStorage.clearUserData();
                userLocalStorage.setUserLoggedIn(false);
                // If logged out clear data and destroy sessions

                startActivity(new Intent(this, UserLogin.class));
                break;
        }
    }

}
