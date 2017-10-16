package com.example.gghangura.adminconsole;

/**
 * Created by gghangura on 2017-03-23.
 */

public class RestUser {
    private String userName;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private String directoryId;

    public String UserName() {
        return userName;
    }
    public String DisplayName() {
        return displayName;
    }
    public String FirstName() {
        return firstName;
    }
    public String LastName() {
        return lastName;
    }
    public String Email() {
        return email;
    }
    public String DirectoryId() {
        return directoryId;
    }

    RestUser(Object user, Object dis, Object first,Object last, Object ema, Object directory) {
        this.userName = (String) user;
        this.displayName = (String) dis;
        this.firstName = (String) first;
        this.lastName = (String) last;
        this.email = (String) ema;
        this.directoryId = (String) directory;
    }

}
