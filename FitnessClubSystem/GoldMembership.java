package FitnessClubSystem;

public class GoldMembership extends Membership {

    public GoldMembership() {
        super("Gold", 200.0);
    }

    @Override
    public double calculateFee() {
        return price;
    }


    @Override
    public boolean hasPriorityBooking() {
        return true;
    }

    @Override
    public boolean hasSaunaAccess() {
        return true;
    }
}


