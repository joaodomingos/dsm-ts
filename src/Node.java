import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Node {
	private static List<Integer> users = new ArrayList<Integer>();;
	private static Integer coordinator = 0;
	private static Integer subcoordinator = 0;
	private static int serverPort = 1234;
	private static boolean imCoordinator = false;
	private static boolean imSubcoordinator = false;
	private static int myPort = 0;
	private static Thread coordinatorAliveCheck = null;;
	private static Thread nodesAliveCheck = null;;
	
	public static void connect(int port){
		System.out.println("new user");
		Object[] objectList = new Object[2];
	    objectList[0] = "newUser";
	    objectList[1] = myPort;
	    SendObjectThread send = new SendObjectThread(objectList, port);
	    new Thread(send).start();
	}
	public static void sendUsers(int port){
		System.out.println("send users");
		Object[] objectList = new Object[2];
	    objectList[0] = "addUsers";
	    objectList[1] = getAllUsers();
	    System.out.println(users.toString());
	    SendObjectThread send = new SendObjectThread(objectList, port);
	    new Thread(send).start();
	}
	
	public static void sendUser(int port){
		System.out.println("send user");
		Object[] objectList = new Object[2];
	    objectList[0] = "addUser";
	    objectList[1] = port;
	    Iterator<Integer> usersIter = users.iterator();
		while(usersIter.hasNext()){
			int user = usersIter.next();
	    	if(user != myPort && user!= port){
			    SendObjectThread send = new SendObjectThread(objectList, user);
			    new Thread(send).start();
	    	}
	    }
	}
	
	public static void sendRemoveUser(int port){
		System.out.println("send remove users");
		Object[] objectList = new Object[2];
	    objectList[0] = "removeUser";
	    objectList[1] = port;
	    Iterator<Integer> usersIter = users.iterator();
		while(usersIter.hasNext()){
			int user = usersIter.next();
	    	if(user != myPort){
			    SendObjectThread send = new SendObjectThread(objectList, user);
			    new Thread(send).start();
	    	}
	    }
	}
	
	public static void youAreTheNewSubCoordinator(int port){
		System.out.println("send you are new subcoordinator");
		setSubcoordinator(port);
		Object[] objectList = new Object[2];
	    objectList[0] = "beSubCoordinator";
	    SendObjectThread send = new SendObjectThread(objectList, port);
	    new Thread(send).start();
	}
	
	public static void newCoordinator(){
		System.out.println("send new coordinator");
		Object[] objectList = new Object[2];
	    objectList[0] = "newCoordinator";
	    objectList[1] = myPort;
	    Iterator<Integer> usersIter = users.iterator();
		while(usersIter.hasNext()){
			int user = usersIter.next();
	    	if(user != myPort){
			    SendObjectThread send = new SendObjectThread(objectList, user);
			    new Thread(send).start();
	    	}
	    }
	}
	
	public static void newSubCoordinator(int subCoordinator){
		System.out.println("send subcoordinator");
		Object[] objectList = new Object[2];
	    objectList[0] = "newSubCoordinator";
	    objectList[1] = subCoordinator;
	    Iterator<Integer> usersIter = users.iterator();
		while(usersIter.hasNext()){
			int user = usersIter.next();
	    	if(user != myPort && user != subCoordinator){
			    SendObjectThread send = new SendObjectThread(objectList, user);
			    new Thread(send).start();
	    	}
	    }
	}
	
	public static boolean checkCoordinator(){
		System.out.println("Im'here checking");
		try (Socket s = new Socket("localhost", coordinator)) {
			
	    } catch (IOException ex) {
	        return false;
	    }
		return true;
	}
	
	public static void checkNodes(){
		Iterator<Integer> usersIter = users.iterator();

		while(usersIter.hasNext()){
			int user = usersIter.next();
			System.out.println("Im'here "+user);
			if(user != myPort){
				try (Socket s = new Socket("localhost", user)) {			
			    } catch (IOException ex) {
			    	System.out.println("Im'here in error");
			    	usersIter.remove();;
			    	sendRemoveUser(user);
			    	if(user == subcoordinator){
			    		for(int newSubCoordinator : users){
				    		if(newSubCoordinator!=myPort){
				    			youAreTheNewSubCoordinator(newSubCoordinator);
				    			newSubCoordinator(newSubCoordinator);
				    			break;
				    		}
				    	}
			    	}
			    }
			}
		}
	}
	
	public static void beCoordinator(){
		System.out.println("I'm Coordinator");
		if(coordinator != myPort){
			users.remove(coordinator);
			coordinator = myPort;
		}
		newCoordinator();
		setImCoordinator(true);
		nodesAliveCheck = new Thread(new Runnable() {
	        public void run() {
	        	while(true){
	        		checkNodes();
	        		try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	        }
    	});
		for(Integer newSubCoordinator : users){
			if(newSubCoordinator!=myPort){
    			youAreTheNewSubCoordinator(newSubCoordinator);
    			newSubCoordinator(newSubCoordinator);
    			break;
    		}
		}
		
    	nodesAliveCheck.start();
	}
	
	public static void beSubCoordinator(){
		System.out.println("I'm SubCoordinator");
		setImSubcoordinator(true);
		subcoordinator = myPort;
    	coordinatorAliveCheck = new Thread(new Runnable() {
	        public void run() {
	        	boolean alive = true;
	            while(alive){
	            	if(!Node.checkCoordinator())
	            		alive = false;
	            	try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            ServiceMain service = new ServiceMain();
	    		service.startListener(Integer.toString(myPort));
	            beCoordinator();
	            newCoordinator();
	        }
    	});
    	coordinatorAliveCheck.start();
    }
	
	public static void addUser(Integer user){
		users.add(user);
	}
	
	public static void removeUser(Integer user){
		System.out.println("Estou a remever: " + user);
		users.remove(user);
	}
	
	public static void setUsers(List<Integer> users){
		Node.users = users;
		System.out.println(users.toString());
	}
	
	public static List<Integer> getAllUsers(){
		return users;
	}

	public static int getCoordinator() {
		return coordinator;
	}

	public static void setCoordinator(int coordinator) {
		Node.coordinator = coordinator;
	}

	public static int getSubcoordinator() {
		return subcoordinator;
	}

	public static void setSubcoordinator(int subcoordinator) {
		Node.subcoordinator = subcoordinator;
	}
	
	public static int getPort() {
		return myPort;
	}
	
	public static void setPort(int port) {
		Node.myPort = port;
	}
	
	public static boolean isImCoordinator() {
		return imCoordinator;
	}
	
	public static void setImCoordinator(boolean imCoordinator) {
		Node.imCoordinator = imCoordinator;
	}
	
	public static boolean isImSubcoordinator() {
		return imSubcoordinator;
	}
	
	public static void setImSubcoordinator(boolean imSubcoordinator) {
		Node.imSubcoordinator = imSubcoordinator;
	}
	
	public static void main(String[] args) {
		Server server = new Server(serverPort);
        try {
            new Thread(server).start();
        }catch(Exception e){
        }
	}
}
