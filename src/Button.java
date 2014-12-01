import java.util.Random;

/**
 * Created by jeffreyng on 14-11-30.
 */
public class Button extends Thread {
    private final int SLEEP_LOWER = 50;
    private final int SLEEP_UPPER = 250;
    private int floor;
    private volatile boolean pushed;
    public volatile boolean running;
    private Random r;
    private Elevator elev;

    public Button(int floor, Elevator elev) {
        this.floor = floor;
        this.pushed = false;
        this.running = true;
        this.elev = elev;
        r= new Random();
    }

    @Override
    public void run() {
        while(running) {
//            while(!pushed && running);
//            if (!running) break;
//
//            pushed = false;
        }
    }

    public void push(){
        this.pushed = true;
        sleep();
    }


    public void cancel(){
        this.pushed = false;
        sleep();
    }

    public boolean inquire(){
        return this.pushed;
    }

    private void sleep(){
        try{
            Thread.sleep(Math.max(r.nextInt(SLEEP_UPPER),SLEEP_LOWER));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void finish(){
        running = false;
    }
}
