import java.time.LocalTime;

public class Clock extends Thread{

    @Override
    public void run() {
        for(;;){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            LocalTime time = LocalTime.now();
            System.out.printf("%02d:%02d:%02d\n",
                    time.getHour(),
                    time.getMinute(),
                    time.getSecond());
        }
    }

    public static void main(String[] args) {
        new Clock().start();
    }
}