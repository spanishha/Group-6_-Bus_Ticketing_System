package group4;

// INHERITANCE
// POLYMORPHISM
public class BaliwagFareCalculator extends FareCalculator {
    @Override
    public double calculatePassengerFare(double fare, String passengerType) {
        if (passengerType.equals("Regular")) {
            return fare;
        } else {
            return fare * 0.80;
        }
    }
}
