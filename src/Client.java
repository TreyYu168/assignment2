import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;

public class Client {

    private String hostname;
    private int portNum;
    private int messageRate;
    private Selector selector;
    private ByteBuffer buffer;
    private HashVerifier hashVerifier;
    private ClientStatTask clientStatTask;
    private Thread clientStatThread;

    public Client(String hostname, int portNum, int messageRate) throws IOException {
        this.hostname = hostname;
        this.portNum = portNum;
        this.messageRate = messageRate;
        Selector selector = Selector.open();
        this.selector = selector;
        this.buffer = ByteBuffer.allocate(20);
        this.hashVerifier = new HashVerifier();
        this.clientStatTask = new ClientStatTask();
        this.clientStatThread = new Thread(clientStatTask);
    }

    private void startClient() throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress(hostname, portNum));
        Thread hashThread = new Thread(this.hashVerifier);
        clientStatThread.start();
        hashThread.start();


        while(true) {
            this.selector.select();

            Iterator keys = this.selector.selectedKeys().iterator();
            while(keys.hasNext()) {
                SelectionKey key = (SelectionKey) keys.next();

                if(key.isConnectable()) {
                    this.connect(key);
                    keys.remove();
                }

                if(key.isWritable()) {
                    this.write(key);
                    keys.remove();
                }

                if(key.isReadable()) {
                    this.read(key);
                    keys.remove();
                }
            }
        }
    }

    private void connect(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel)key.channel();

        try {
            socketChannel.finishConnect();
            socketChannel.register(this.selector, SelectionKey.OP_WRITE);

        }catch (Exception e) {
            System.out.println("Except caught when connecting/interestKey" + e.getMessage());
        }

    }

    private void write(SelectionKey key) {

        ClientTask clientTask = new ClientTask(key, this.messageRate, this.hashVerifier, this.clientStatTask);

        Thread write = new Thread(clientTask);

        write.start();


        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {

        SocketChannel socketChannel = (SocketChannel) key.channel();

        this.buffer.clear();
        socketChannel.read(this.buffer);
        clientStatTask.incrementRead();

        hashVerifier.addToReceive(Arrays.toString(this.buffer.array()));

    }

    public static void main(String[] args) throws IOException {
        String hostname = args[0];
        int portNum = Integer.parseInt(args[1]);
        int messageRate = Integer.parseInt(args[2]);

        Client client = new Client(hostname, portNum, messageRate);

        client.startClient();

    }
}
