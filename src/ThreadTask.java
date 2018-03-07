public class ThreadTask implements Runnable {

    private JobQueueManager jobQueueManager;

    public ThreadTask(JobQueueManager jobQueueManager) {
        this.jobQueueManager = jobQueueManager;
    }

    @Override
    public void run() {
        while(true) {

            synchronized (jobQueueManager) {
                while (jobQueueManager.isEmpty()) {
                    try {
                        jobQueueManager.wait();
                    } catch (InterruptedException ie) {
                        System.out.println("Run error has occurred: " + ie.getMessage());
                    }
                }

                Runnable event = jobQueueManager.getFromQueue();
                event.run();
            }

        }
    }

}
