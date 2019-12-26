
public class MainClient {
	
	private static int numVisitors = 17;
	private static int totalMovie = 4;
    private static String serverHost = "127.0.0.1";
    private static int serverPort = 55533;
    
	public static void main(String[] args)  {		    
		
		//To create a Clock thread
		new ClockThread(serverHost, serverPort, "Clock", totalMovie);
		
		//To create a Speaker thread
		new SpeakerThread(serverHost, serverPort, "Speaker", totalMovie);
		
		//To create certain Visitors threads
		for(int i = 1; i <= numVisitors; i++) {
			new VisitorsThread(serverHost, serverPort, "Visitor", i);
		}
	}
}
