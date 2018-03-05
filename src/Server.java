import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class Server {

    private int portNum;
    private int numOfThreads;
    private ThreadPoolManager threadPool;
    private Selector selector;

    public Server(int portNum, int numOfThreads) throws IOException{
        this.portNum = portNum;
        this.numOfThreads = numOfThreads;
        this.threadPool = new ThreadPoolManager(this.numOfThreads);
        Selector selector = Selector.open();
        this.selector = selector;

    }
    private void startServer() throws IOException {
        // Create a Selector

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(portNum));
        SelectionKey socketChannel = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // wait for events
            //this.selector.select();
            // wake up to work on selected keys
            //Iterator keys = this.selector.selectedKeys().iterator();
            //while (keys.hasNext()) {
                //more housekeeping
//                if (key.isAcceptable ()) {
//                    this.accept(key);
//            }
        }
/*other cases such as isReadable() and isWriteable() not shown*/
//            } }}
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int numOfThreads = Integer.parseInt(args[1]);

        try {

            Server server = new Server(port, numOfThreads);
            System.out.println(server.threadPool.toString());
            server.threadPool.startThreadPool();

        } catch(IOException ioe) {
            System.out.println("Interrupted Exception caught: " + ioe.getMessage());
        }

    }
}
