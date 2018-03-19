import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ServerTask implements Runnable{

    private SelectionKey selectionKey;
    private SocketChannel socketChannel;
    private ByteBuffer byteBuffer;
    private int test = 0;

    public ServerTask(SelectionKey key) {
        this.selectionKey = key;
        this.socketChannel = (SocketChannel)key.channel();

    }


    private void read() throws IOException {

        byteBuffer = ByteBuffer.allocate(8192);
        while(byteBuffer.hasRemaining()) {
            socketChannel.read(byteBuffer);
        }

    }

    public byte[] SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        byte[] hash = digest.digest(data);
        return hash;
    }

    @Override
    public void run() {
            try {
                this.read();
                byte[] byteArray = this.SHA1FromBytes(byteBuffer.array());
                socketChannel.write(ByteBuffer.wrap(byteArray));

                this.byteBuffer.clear();

                selectionKey.interestOps(SelectionKey.OP_READ);

            } catch (Exception e) {
                System.out.println("ServerTask ioe/NoSuchAlgorithm error: " + e.getMessage());
            }
    }
}
