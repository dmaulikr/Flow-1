package nhacks16.flow.UsersManagement;

/**
 * Created by Owner on 2016-03-12.
 */
public class User {
/* Creating new users 2 methods */
    String name, userName, password;
        //username = email
    Integer id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName(){
        return this.userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }
}


