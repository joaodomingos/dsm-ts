import discovery.ServiceAnnouncer;
import discovery.ServiceFinder;
import discovery.ServiceInfo;

public class ServiceMain {

	private static final String SERVICE_ID = "e9ababe5872f24caf1a504f1d675470c";
    int mRequestId = 1;
    boolean find = false;
    String port = "";
    String ip = "null";
	
	public void startListener(String port){
		String serviceId = "e9ababe5872f24caf1a504f1d675470c";
		String ip = "localhost";

		ServiceAnnouncer announcer = new ServiceAnnouncer();
		ServiceInfo si = 
		     new ServiceInfo(port, serviceId, ip, 3219, false);
		announcer.addService(si);
		announcer.startListening();
		System.out.println("Criar serviço");
	}
	
    public Object discover(String oldPort) {
    	System.out.println("Procurar serviço");
        initFinder();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(!find)
        	startListener(oldPort);
        Object[] info = new Object[2];
        info[0]= ip;
        info[1]= port;
        return info;
    }
    
    private void initFinder() {
        ServiceFinder finder = new ServiceFinder(); 
        finder.addListener(mListener);
        finder.startListening();
        finder.findServers(SERVICE_ID, 0);
    } 

    private ServiceFinder.Listener mListener = new ServiceFinder.Listener() {
        @Override
        public void serverFound(ServiceInfo si, int requestId,
                ServiceFinder finder) {
        	find = true;
        	port = si.getServerName();
        	ip = si.getServiceHost();
            System.out.println("Found service provider named " +
                    si.getServerName() + " at " + si.getServiceHost() + ":" +
                    + si.getServicePort());
        }

        @Override
        public void listenStateChanged(ServiceFinder finder, boolean listening) {
        }
    };
}
