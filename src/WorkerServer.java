import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public class WorkerServer implements Runnable {

    protected Socket clientSocket = null;
    protected boolean entryPoint = false;
   
    public WorkerServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        SocketAddress clientRemoteAddress = clientSocket.getRemoteSocketAddress();
        final String clientIP = clientRemoteAddress.toString();
        try {
            ObjectInputStream objectInput = new ObjectInputStream(clientSocket.getInputStream());
            try {
                Object object = objectInput.readObject();
                Object[] objectList = (Object[]) object;
                switchCase(clientIP, objectList);

            } catch (ClassNotFoundException e) {
                System.out.println("This object does not corresponds to one of the above");
                e.printStackTrace();
            }
        } catch (IOException e) {
            //report exception somewhere.
           // e.printStackTrace();
        }
    }

    private void switchCase(String clientIP, Object[] objectList) {
        String command = (String) objectList[0];
        if (command.equals("newUser")) {
        	newUser(objectList);
        }
        if (command.equals("addUsers")) {
        	addUsers(objectList);
        }
        if (command.equals("addUser")) {
        	addUser(objectList);
        }
        if (command.equals("removeUser")) {
        	removeUser(objectList);
        }
        if (command.equals("beSubCoordinator")) {
        	Node.beSubCoordinator();
        }
        if (command.equals("newCoordinator")) {
        	newCoordinator(objectList);
        }
        if (command.equals("newSubCoordinator")) {
        	newSubCoordinator(objectList);
        }
    }
    
    public void newUser(Object[] objectList){
    	System.out.println("Add new user " + (int)objectList[1]);
    	Node.addUser((int)objectList[1]);
    	Node.sendUsers((int)objectList[1]);
    	if(Node.getAllUsers().size() == 2){
    		Node.youAreTheNewSubCoordinator((int)objectList[1]);
    	}
    	else
    		Node.sendUser((int)objectList[1]);
    }
    
    public void addUser(Object[] objectList){
    	System.out.println("Add user " + (int)objectList[1]);
    	Node.addUser((int)objectList[1]);
    }
    
    public void addUsers(Object[] objectList){
    	Node.setUsers((List<Integer>)objectList[1]);
    	System.out.println("Receive List " + (List<Integer>)objectList[1]);
    	
    }
    
    public void removeUser(Object[] objectList){
    	System.out.println("Remove user " + (int)objectList[1]);
    	Node.removeUser((int)objectList[1]);
    }
    
    public void newCoordinator(Object[] objectList){
    	System.out.println("New Coordinator " + (int)objectList[1]);
    	Node.removeUser(Node.getCoordinator());
    	Node.setCoordinator((int)objectList[1]);
    }
    
    public void newSubCoordinator(Object[] objectList){
    	System.out.println("New SubCoordinator " + (int)objectList[1]);
    	Node.setSubcoordinator((int)objectList[1]);
    }
}