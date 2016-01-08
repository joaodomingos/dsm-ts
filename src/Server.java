import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
public class Server implements Runnable{

    protected int          serverPort   = 0;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    
    public Server(int serverPort){
    	this.serverPort = serverPort;
    }
    
    @Override
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        ////Service start or discover//////////
        ServiceMain service = new ServiceMain();
		Object[] info = (Object[]) service.discover(Integer.toString(serverPort));
		if((String)info[0] != "null"){
			String ip = (String) info[0];
			String portString = ((String) info[1]);
			int port = Integer.parseInt(portString);
			System.out.println(ip+":"+port);
			Random rand = new Random();
			int  n = rand.nextInt(20) + 1;
			serverPort += n;
			Node.setPort(serverPort);
			Node.setCoordinator(port);
			Node.connect(port);
		}
		else{
			System.out.println("entrypoint");
			Node.setPort(serverPort);
			Node.setCoordinator(serverPort);
			Node.addUser(serverPort);
			System.out.println(Node.getAllUsers().toString());
			Node.beCoordinator();
		}
		////////////////////////////////////////
		
        try {
            openServerSocket();
            while (!isStopped()) {
                Socket clientSocket = null;
                try {
                    clientSocket = this.serverSocket.accept();
                } catch (IOException e) {
                    if (isStopped()) {
                        System.out.println("Server Stopped.");
                        return;
                    }
                    throw new RuntimeException(
                            "Error accepting client connection", e);
                }
                new Thread(
                        new WorkerServer(clientSocket)
                ).start();
            }
        }catch (Exception e){
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 1234", e);
        }
    }

}