import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ServerStatTask implements Runnable{

    private HashMap<SocketChannel, Integer> throughput;
    public String test;

    public ServerStatTask(HashMap<SocketChannel, Integer> throughput) {

        this.throughput = throughput;
        this.test = "I'm Printing";
    }

    public synchronized void addSocketChannel(SocketChannel socketChannel) {
        this.throughput.put(socketChannel, 0);
    }

    public synchronized void incrementChannel(SocketChannel socketChannel) {
        this.throughput.put(socketChannel, this.throughput.get(socketChannel) + 1);
    }

    @Override
    public void run() {

        while(true) {
            try {
                Thread.sleep(20000);
                String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());

                int totalMessages = 0;

                for (int i : throughput.values()) {
                    totalMessages += i;
                }

                int serverThroughput = totalMessages / 20;
                double meanPerClient = totalMessages / throughput.size();
                double temp = 0;

                for (int j : throughput.values()) {

                    temp += Math.pow(((double)j - meanPerClient), 2);

                }

                double nMinusOne = throughput.size() - 1;
                double stdDev = Math.sqrt((temp / nMinusOne));

                System.out.println("[" + timeStamp + "] Server Throughput: " + Integer.toString(serverThroughput)
                                    + " messages/s, Active Client Connections: " + Integer.toString(throughput.size())
                                    + ", Mean Per-client Throughput: " + Double.toString(meanPerClient)
                                    + " messages/s, Std. Dev. Of Per-client Throughput: " + Double.toString(stdDev)
                );

                throughput.replaceAll(((socketChannel, integer) -> 0));

            } catch (InterruptedException ie) {
                System.out.println("ServerStatTask Interrupted Exception caught: " + ie.getMessage());
            }
        }

    }
}
