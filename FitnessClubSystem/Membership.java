package FitnessClubSystem;

/*
 * Abstract Membership Class
 * ------------------------
 * Demonstrates ABSTRACTION & POLYMORPHISM.
 * Different membership types override calculateFee().
 */
public abstract class Membership {

    protected String membershipName;
    protected double price;

    public Membership(String membershipName, double price) {
        this.membershipName = membershipName;
        this.price = price;
    }

    public String getMembershipName() {
        return membershipName;
    }

    public double getPrice() { 
        return price; 
    }

    public abstract double calculateFee();

    /*
     * Priority class booking privilege
     */
    public abstract boolean hasPriorityBooking();

    /*
     * Sauna access privilege
     */
    public abstract boolean hasSaunaAccess();

}

