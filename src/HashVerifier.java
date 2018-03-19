import java.util.LinkedList;

public class HashVerifier implements Runnable{

    private LinkedList<String> sentHash;
    private LinkedList<String> receivedHash;

    public HashVerifier() {
        this.sentHash = new LinkedList<>();
        this.receivedHash = new LinkedList<>();
    }

    public synchronized void addToSent(String hash) {
        sentHash.add(hash);
    }

    public synchronized void addToReceive(String hash) {
        receivedHash.add(hash);
        notifyAll();
    }

    public synchronized String getFromReceive() {
        return receivedHash.pollFirst();
    }


    public synchronized boolean isReceiveEmpty() {
        return receivedHash.isEmpty();
    }

    @Override
    public synchronized void run() {
        while(true) {
            if (isReceiveEmpty()) {
                try {
                    wait();
                } catch (InterruptedException ie) {
                    System.out.println("Interrupted Exception in Verifier: " + ie.getMessage());
                }
            } else {

                String hash = getFromReceive();

                if(sentHash.removeFirstOccurrence(hash)) {
                } else {
                    System.out.println("Hash Does Not Exist");
                }
            }
        }
    }
}
