/**
 * This User class  has the username field in this example.
 */
public class User {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String password;

    public User(String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

}
