package FitnessClubSystem;

public class BasicMembership extends Membership {

    public BasicMembership() {
        super("Basic", 50.0);
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
        return false;
    }
}