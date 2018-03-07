import java.util.LinkedList;

public class JobQueueManager {

    private LinkedList<ServerTask> queue;

    public JobQueueManager() {
        this.queue = new LinkedList<>();
    }

    public synchronized void addToQueue(Runnable event) {
        queue.add((ServerTask)event);
        notifyAll();
    }

    public synchronized Runnable getFromQueue() {
        return queue.pollFirst();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

}
