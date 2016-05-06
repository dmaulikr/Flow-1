package nhacks16.flow.UsersManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import nhacks16.flow.R;

public class UserSignUp extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        final EditText ETName = (EditText) findViewById(R.id.ETName);
        final EditText ETUserName = (EditText) findViewById(R.id.ETUserName);
        final EditText ETPassword = (EditText) findViewById(R.id.ETPassword);
        final EditText ETConfirmPassword = (EditText) findViewById(R.id.ETConfirmPassword);
        Button butSignUp = (Button) findViewById(R.id.butSignUp);


        butSignUp.setOnClickListener(new View.OnClickListener() {

            //Override necessary for this to work
            @Override
            public void onClick(View v) {
                final String name = ETName.getText().toString();
                final String userName = ETUserName.getText().toString();
                final String password = ETPassword.getText().toString();
                final String confirm = ETConfirmPassword.getText().toString();

                if (password.length() < 6) {
                    Toast.makeText(UserSignUp.this, "Password must be more than 6 characters.", Toast.LENGTH_LONG).show();
                }

                else if (name.equals(" ")) {
                    Toast.makeText(UserSignUp.this, "You must enter a name!", Toast.LENGTH_LONG).show();
                }

                else if (!password.equals(confirm)) {
                    Toast.makeText(UserSignUp.this, "Your passwords must match.", Toast.LENGTH_LONG).show();
                }
                else if (userName.equals(" ")) {
                    Toast.makeText(UserSignUp.this, "You need an e-mail address!", Toast.LENGTH_LONG).show();
                }

                else {
                    // Implement Async instead
                    new Thread (new Runnable() {
                        public void run() {
                            DBUserAdapter newUser = new DBUserAdapter(UserSignUp.this);
                            newUser.open();
                            newUser.AddUser(userName, password);
                        }
                    }).start();

                    Intent i = new Intent(UserSignUp.this, LandingActivity.class);
                    Toast.makeText(UserSignUp.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                    //Add Matt's feature too!!!!
                }

            }
        });
    }
}