/**
 * Created by jeffreyng on 14-11-30.
 */
public class ElevatorSimulation {

    private static int passengerCount;
    private static int travelCount;

    private static Elevator elev;

    public static void main(String[] args) {
        passengerCount = Integer.parseInt(args[0]);
        travelCount = Integer.parseInt(args[1]);

        elev = new Elevator(3,passengerCount,travelCount);
        elev.start();

    }
}
