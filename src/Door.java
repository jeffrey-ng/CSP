import java.util.Random;

/**
 * Created by jeffreyng on 14-11-30.
 */
public class Door extends Thread{
    private final int SLEEP_LOWER = 50;
    private final int SLEEP_UPPER = 250;
    private int floor;
    private volatile boolean isOpen;
    private volatile boolean running;
    private Elevator elev;
    private Random r;

    public Door(int floor, Elevator elev){
        this.floor = floor;
        this.isOpen = false;
        this.elev = elev;
        this.running = true;
        r = new Random();
    }

    @Override
    public void run() {
        while(running) {
            if(isOpen) {
                while(isOpen && running);
                if (!running) break;

                //tell elevator its closed
                elev.setCloseDoor();
            } else {
                while(!isOpen && running);
                if (!running) break;
                //tell elevator its opened
                elev.setOpenDoor();
            }
        }
    }

    public void open() {
        isOpen = true;
        System.out.println("DOORS OPENED ON FLOOR "+ floor);
        sleep();
    }

    public void close() {
        isOpen = false;
        System.out.println("DOORS CLOSED ON FLOOR "+ floor);
        sleep();
    }

    public void finish() {
        running = false;
    }
    private void sleep(){
        try{
            Thread.sleep(Math.max(r.nextInt(SLEEP_UPPER),SLEEP_LOWER));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
