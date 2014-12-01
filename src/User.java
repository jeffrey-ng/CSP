import java.util.Random;

/**
 * Created by jeffreyng on 14-11-30.
 */
public class User extends Thread{

    private volatile boolean inElev;
    private volatile boolean openDoor;

    private final int SLEEP_LOWER = 50;
    private final int SLEEP_UPPER = 250;

    private int cFloor;
    private int dFloor;
    private Elevator elev;
    private int elevFloor;

    private int travelCount;
    private int travelLimit;
    private int[] floors = new int[]{0,1,2};
    private Random r;
    public User(int travelLimit, Elevator e) {
        this.r = new Random();
        this.travelLimit = travelLimit;
        this.travelCount = 0;
        this.cFloor = r.nextInt(3);
        this.inElev = false;
        this.elev = e;
        this.elevFloor=e.getFloor();
        getNewRandomFloor();
    }


    @Override
    public void run(){
        while (travelCount < travelLimit) {
            if (!inElev) {
                elev.pushCallButton(cFloor);
                System.out.println("USER" + this.getId() + " CALLS BUTTON ON FLOOR "+cFloor);
                do{
                    while(!openDoor);

                    if(elevFloor==cFloor) enter();
                    elev.isReady();

                    while(openDoor);
                }while(!inElev);
            } else {
                elev.pushElevButton(dFloor);
                System.out.println("USER" + this.getId() + " PUSHS BUTTON FOR FLOOR "+dFloor);
                do{
                    while(!openDoor);
                    if (elevFloor==dFloor) exit();
                    elev.isReady();

                    while(openDoor);
                } while (inElev);
            }
            sleep();
        }
    }

    private void getNewRandomFloor(){
        do {
            dFloor = r.nextInt(3);
        }while(dFloor==cFloor);
    }
    private void enter() {
        inElev = true;
        System.out.println("USER "+this.getId()+" ENTERS ELEVATOR ON FLOOR "+cFloor);
        sleep();
    }
    private void exit() {
        inElev = false;
        cFloor=elevFloor;
        travelCount++;
        getNewRandomFloor();
        System.out.println("USER "+this.getId()+" EXITS ELEVATOR ON FLOOR "+cFloor);
        sleep();
    }

    public void opened() {
        elevFloor = elev.getFloor();
        openDoor = true;
    }
    public void closed(){
        openDoor = false;
    }

    private void sleep(){
        try{
            Thread.sleep(Math.max(r.nextInt(SLEEP_UPPER),SLEEP_LOWER));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}