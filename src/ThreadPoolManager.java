import java.util.ArrayList;
import java.util.LinkedList;

public class ThreadPoolManager extends Thread{

    private ArrayList<Threads> threadPool = new ArrayList<>();
    private LinkedList<Runnable> queue = new LinkedList<>();
    private int numOfThreads;

    public ThreadPoolManager(int size) {
        this.numOfThreads = size;
        this.threadPool = new ArrayList<>(size);
        for(int i = 0; i < size; i++) {
            Threads threads = new Threads();
            threadPool.add(threads);
        }
    }

    public synchronized void startThreadPool() {
        for(int i = 0; i < numOfThreads; i++) {
            this.threadPool.get(i).start();
        }
    }

    private synchronized void addToQueue(Runnable event) {
        queue.add(event);
        notifyAll();

    }

    private synchronized Runnable getFromQueue(LinkedList queue) {
        if(queue.isEmpty()) {
            return null;
        } else {
            return (Runnable)queue.pollFirst();
        }
    }

    private synchronized boolean isEmpty(LinkedList queue) {
        if(queue.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return "ThreadPoolManager has " + numOfThreads + " threads initialized";
    }


    private class Threads extends Thread {

        @Override
        public synchronized void run() {
            System.out.println("Run Method entered");
            while(true) {

                while(!isEmpty(queue)) {
                    Runnable event = getFromQueue(queue);
                    event.run();
                }

                try {
                    wait();
                } catch (InterruptedException ie)  {
                    System.out.println("Run error has occured: " + ie.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        ThreadPoolManager threadPoolManager = new ThreadPoolManager(2);

        Runnable start = new Runnable() {
            @Override
            public void run() {
                threadPoolManager.startThreadPool();
            }
        };

        Thread startThreadPool = new Thread(start);
        startThreadPool.start();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("Dva 4 Lyfe");
            }
        };

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("Korean Drama 4 Lyfe");
            }
        };

        threadPoolManager.addToQueue(runnable);
        threadPoolManager.addToQueue(runnable1);

    }
}
