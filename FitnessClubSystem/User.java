package FitnessClubSystem;

/*
 * Abstract User Class
 * ---------------------------------------------------
 * Represents ANY person who can login to the system.
 * 
 * Demonstrates:
 * - Abstraction (cannot instantiate directly)
 * - Encapsulation (private attributes)
 * - Polymorphism (abstract displayMenu())
 */

public abstract class User {

    private String userId;   // M001 / T001 / A001
    private String password;


    public User(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }


    public boolean validatePassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    // Polymorphism: Each role displays different menu
    public abstract void displayMenu();
}
