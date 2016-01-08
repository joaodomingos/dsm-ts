import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class SendObjectThread implements Runnable {
    private Object object;
    private String host = "localhost";
    private int port;

    public SendObjectThread(Object object, int port) {
        this.object = object;
        this.port = port;
    }

    @Override
    public void run() {
        Object[] objectList = (Object[])object;
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 15000);

            try {
                ObjectOutputStream objectOutput = new ObjectOutputStream(socket.getOutputStream());
                objectOutput.writeObject(object);
                objectOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}