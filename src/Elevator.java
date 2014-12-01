import java.util.HashSet;
import java.util.Random;

/**
 * Created by jeffreyng on 14-11-30.
 */
public class Elevator extends Thread{

    private final int SLEEP_LOWER = 50;
    private final int SLEEP_UPPER = 250;
    public enum State {IDLE, UP, DOWN};

    private Button[] elevButtons;
    private User[] users;
    private Button[] callButtons;
    private Door[] doors;

    private int cFloor;
    private int floorCount;
    private int userCount;

    private volatile HashSet<Integer> demands;

    private volatile int workingUsers;
    private volatile boolean isOpen;
    private volatile int count;
    private State state;

    private Random r;

    public Elevator(int floorCount,int userCount, int travelCount) {
        this.cFloor = 0;
        this.count=0;
        this.userCount = userCount;
        this.floorCount = floorCount;
        this.state = State.IDLE;
        this.workingUsers = userCount;
        elevButtons = new Button[this.floorCount];
        users = new User[this.userCount];
        callButtons = new Button[this.floorCount];
        doors = new Door[this.floorCount];
        r = new Random();

        demands = new HashSet<Integer>();

        for (int i = 0; i < this.floorCount;i++) {
            Button eB = new Button(i,this);
            Button cB = new Button(i,this);
            elevButtons[i]=eB;
            callButtons[i]=cB;
            Door d = new Door(i,this);
            doors[i]=d;
        }

        for (int j =0;j<this.userCount;j++){
            users[j] = new User(travelCount, this);
        }
    }

    @Override
    public void run(){
        init();
        while (workingUsers > 0) {
            System.out.println("WORKING USERS "+workingUsers);
            boolean u = upReq(this.cFloor);
            boolean d = downReq(this.cFloor);
            boolean tf = thisFloor(this.cFloor,u,d);

            if(tf) {
                doors[this.cFloor].open();
                while (!isOpen);

                callButtons[this.cFloor].cancel();
                elevButtons[this.cFloor].cancel();
                for(User cUser: users) {
                    cUser.opened();
                }

                while(count != workingUsers);
                count=0;

                doors[this.cFloor].close();

                for (User cUser: users){
                    cUser.closed();
                }
                while(isOpen);

            } else if (d) {
//                if (!(state==State.UP && u)) {
                    state=State.DOWN;
                    cFloor = cFloor-1;
                    System.out.println("ELEVATOR ARRIVES ON FLOOR " + cFloor);
//                } else {
//                    System.out.println("BROKEN D");
//                }
            } else if (u) {
//                if (!(state==State.DOWN && d)) {
                    state=State.UP;
                    cFloor = cFloor+1;
                    System.out.println("ELEVATOR ARRIVES ON FLOOR "+cFloor);

//                } else {
//                    System.out.println("BROKEN U");
//                }
            } else {
                state = State.IDLE;
            }
            sleep();
        }
        for (int i =0; i < floorCount;i++){
            callButtons[i].finish();
            elevButtons[i].finish();
            doors[i].finish();
        }
        System.out.println("Elevator : DONE");


    }

    private boolean thisFloor(int cfloor, boolean u, boolean d) {
        System.out.println("THISFLOOR at " + cfloor);

        boolean req = elevButtons[cfloor].inquire();
        boolean q = callButtons[cfloor].inquire();
//        boolean r = false;
//        if (!req) {
//            if (!(state==State.UP && u) || !(state==State.DOWN && d)) {
//                r = callButtons[cfloor].inquire();
//            }
//        }
        return (req || q);
    }

    public synchronized void request(int d) {
        demands.add(d);
        sleep();
    }

    private synchronized void fulfill(int d) {
        demands.remove(d);
        sleep();
    }



    private boolean upReq(int cfloor) {
        boolean req = false;
        boolean re = false;
        boolean rc = false;
        for (int i = cfloor+1; i < floorCount;i++) {
            re = elevButtons[i].inquire();
            rc = callButtons[i].inquire();
            if (req || re || rc) return true;
        }
        return false;
    }
    private boolean downReq(int cfloor) {
        boolean req = false;
        boolean re = false;
        boolean rc = false;
        for (int i = cfloor-1; i >= 0;i--) {
            re = elevButtons[i].inquire();
            rc = callButtons[i].inquire();
            if (req || re || rc) return true;
        }
        return false;
    }

    public void setOpenDoor(){
        this.isOpen = true;
    }
    public void setCloseDoor(){
        this.isOpen = false;
    }


    public void pushCallButton(int floor) {
        callButtons[floor].push();

        sleep();
    }
    public void pushElevButton(int floor) {
        elevButtons[floor].push();
        sleep();
    }

    public int getFloor(){
        return this.cFloor;
    }

    public synchronized void isReady(){
        count++;
    }
    private void sleep(){
        try{
            Thread.sleep(Math.max(r.nextInt(SLEEP_UPPER),SLEEP_LOWER));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        System.out.println("START INITTING");
        for (int i =0; i < floorCount;i++){
            doors[i].start();
            callButtons[i].start();
            elevButtons[i].start();

        }
        for (User cUser : users) {
            cUser.start();
        }
        System.out.println("DONE INITTING");
    }
}
