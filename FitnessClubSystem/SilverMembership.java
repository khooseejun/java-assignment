package FitnessClubSystem;

public class SilverMembership extends Membership {

    public SilverMembership() {
        super("Silver", 100.0);
    }

    @Override
    public double calculateFee() {
        return price;
    }


    @Override
    public boolean hasPriorityBooking() {
        return false;
    }


    @Override
    public boolean hasSaunaAccess() {
        return true;
    }
}
