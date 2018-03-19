import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientStatTask implements Runnable{

    private int writeCount;
    private int readCount;

    public ClientStatTask() {
        this.writeCount = 0;
        this.readCount = 0;
    }

    public void incrementRead() {
        this.readCount++;
    }

    public void incrementWrite() {
        this.writeCount++;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(20000);
                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

                System.out.println("[" + timeStamp + "] Total Sent Count: " + Integer.toString(writeCount) +
                                    ", Total Received Count: " + Integer.toString(readCount)
                );

                writeCount = 0;
                readCount = 0;

            } catch (InterruptedException ie) {
                System.out.println("ClientStatTask Interrupted Exception caught: " + ie.getMessage());
            }
        }

    }
}
