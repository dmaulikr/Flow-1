package nhacks16.flow.UsersManagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import nhacks16.flow.Main.SandBoxMain;
import nhacks16.flow.Main.TheStream;
import nhacks16.flow.R;

public class UserLogin extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_user_login);
    }


    public void attemptLogin(View view) {
                final EditText ETUserName = (EditText) findViewById(R.id.ETUserName);
                final EditText ETPassword = (EditText) findViewById(R.id.ETPassword);
                String userName = ETUserName.getText().toString();
                String password = ETPassword.getText().toString();
                try {
                    if (userName.length() >= 0 && password.length() >= 0) {
                        DBUserAdapter dbUser = new DBUserAdapter(UserLogin.this);
                        dbUser.open();

                        if (dbUser.Login(userName, password)) {
                            Toast.makeText(UserLogin.this, "Successfully Logged In", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(UserLogin.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                        }
                        dbUser.close();
                    }

                } catch (Exception e) {
                    Toast.makeText(UserLogin.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }
            }

    public void GoToSignup(View view) {
        Intent signup = new Intent(UserLogin.this, UserSignUp.class);
        startActivity(signup);
    }


    public void GoToStream(View view) {
        Intent thestream = new Intent(UserLogin.this, TheStream.class);
        startActivity(thestream);
    }
}
