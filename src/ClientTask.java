import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class ClientTask implements Runnable{

    private SelectionKey selectionKey;
    private SocketChannel socketChannel;
    private int sleepRate;
    private HashVerifier hashVerifier;
    private ClientStatTask clientStatTask;

    public ClientTask(SelectionKey key, int messageRate, HashVerifier hashVerifier, ClientStatTask clientStatTask) {
        this.selectionKey = key;
        this.socketChannel = (SocketChannel)key.channel();
        this.sleepRate = 1000 / messageRate;
        this.hashVerifier = hashVerifier;
        this.clientStatTask = clientStatTask;
    }

    private byte[] createRandom() {
        byte[] b = new byte[8192];
        new Random().nextBytes(b);

        return b;
    }

    private void write()  {
        try {
            byte[] random = createRandom();
            ByteBuffer byteBuffer = ByteBuffer.wrap(random);

            try {
                byte[] hash = SHA1FromBytes(random);
                hashVerifier.addToSent(Arrays.toString(hash));

            } catch (NoSuchAlgorithmException noe) {
                System.out.println("Client Task NoSuchAlgorithmException caught: " + noe.getMessage());
            }
            while (byteBuffer.hasRemaining()) {
                socketChannel.write(byteBuffer);
            }

        } catch (IOException ioe) {
            System.out.println("Client Task IOE error caught: " + ioe.getMessage());
        }
    }

    public byte[] SHA1FromBytes(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        byte[] hash = digest.digest(data);
        return hash;
    }

    @Override
    public void run() {
        while(true) {

            this.write();
            clientStatTask.incrementWrite();
            try {
                Thread.sleep(sleepRate);
            } catch(InterruptedException ie) {
                System.out.println("Thread Sleep Interrupted Exception caught: " + ie.getMessage());
            }
        }
    }

}
