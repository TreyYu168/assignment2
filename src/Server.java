import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;

public class Server {

    private int portNum;
    private JobQueueManager jobQueueManager;
    private Selector selector;
    private ThreadPool threadPool;
    private HashMap<SocketChannel, Integer> clientThroughput;
    private ServerStatTask serverStatTask;
    private Thread serverStatThread;

    public Server(int portNum, int numOfThreads) throws IOException{
        this.portNum = portNum;
        this.jobQueueManager = new JobQueueManager();
        Selector selector = Selector.open();
        this.selector = selector;
        this.threadPool = new ThreadPool(jobQueueManager, numOfThreads);
        this.clientThroughput = new HashMap<>();
        this.serverStatTask = new ServerStatTask(this.clientThroughput);
        this.serverStatThread = new Thread(serverStatTask);
    }

    private void startServer() throws IOException {
        // Create a Selector
        threadPool.startThreadPool();
        serverStatThread.start();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(portNum));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(true) {

            this.selector.select();

            Iterator keys = this.selector.selectedKeys().iterator();

            while (keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();

                if (key.isAcceptable ()) {
                    this.accept(key);
                    keys.remove();
                }

                if (key.isReadable()) {
                    this.read(key);
                    keys.remove();
                }
            }
        /*other cases such as isReadable() and isWriteable() not shown*/
      }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        serverStatTask.addSocketChannel(socketChannel);

        socketChannel.register(this.selector, SelectionKey.OP_READ);

    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel)key.channel();

        key.interestOps(SelectionKey.OP_WRITE);
        ServerTask serverTask = new ServerTask(key);

        jobQueueManager.addToQueue(serverTask);
        serverStatTask.incrementChannel(socketChannel);

    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int numOfThreads = Integer.parseInt(args[1]);

        try {

            Server server = new Server(port, numOfThreads);
            //System.out.println(server.threadPool.toString());
            //server.threadPool.startThreadPool();
            server.startServer();

        } catch(IOException ioe) {
            System.out.println("Server Main: Interrupted Exception caught: " + ioe.getMessage());
        }

    }
}
