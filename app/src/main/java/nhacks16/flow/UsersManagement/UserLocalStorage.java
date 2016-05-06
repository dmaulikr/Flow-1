package nhacks16.flow.UsersManagement;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Owner on 2016-03-12.
 */
public class UserLocalStorage {
   /* If a local store is created, must give context */
    public static final String SP_NAME = "userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStorage(Context context) {
        userLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    /* Generate methods to get manipulate data from Db
     */

    public void storeUserData(User user ){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
            /* Allows edit what's contained in 'sharedpreferences' to edit local database pre */
        spEditor.putString("name", user.name);
        spEditor.putString("username", user.userName);
        spEditor.putString("password", user.password);
        /* Updates everything in the shared preference with attributes of user which is
        * passed into this method */
        spEditor.commit();
    }

    // This method will return a user and the its attribute's
    public User getLoggedInUser() {
        String name = userLocalDatabase.getString("name", "");
        String userName = userLocalDatabase.getString("userName","");
        String password = userLocalDatabase.getString("password", "");
        User storedUser = new User(); // <-- see User.Java
        return storedUser;

    }
        // Determines session (logged in vs. logged out)
    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.putBoolean("LoggedIn", loggedIn);
        spEditor.commit();
    }

    //Looks at local db to see if user logged in
    public Boolean getUserLoggedIn() {
        if (userLocalDatabase.getBoolean("loggedIn", false)) {
            return true;
        }
        else{
            return false;
        }
    }

    public void clearUserData() {
        SharedPreferences.Editor spEditor=userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
