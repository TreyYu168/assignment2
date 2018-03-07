import java.util.ArrayList;

public class ThreadPool {

    private ArrayList<Thread> threadPool = new ArrayList<>();

    public ThreadPool(JobQueueManager threadPoolManager, int numOfThreads) {
        for(int i = 0; i < numOfThreads; i++) {
            ThreadTask threadTask = new ThreadTask(threadPoolManager);
            Thread thread = new Thread(threadTask);
            threadPool.add(thread);
        }
    }

    public void startThreadPool() {
        for(int i = 0; i < threadPool.size(); i++) {
            this.threadPool.get(i).start();
        }
    }


}