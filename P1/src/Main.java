//Name: Yifei Du
public class Main {
	
	private static int numVisitors = 2;
	private static int theaterCapacity = 8;
	private static int party_ticket = 3;
	private static int totalMovie = 4;
	
	public static void main(String[] args)  {	
		long system_start_time = System.currentTimeMillis();
		
		/*To create a Monitor, all Clock, Speaker, Visitors Thread should use this monitor to go in C.S*/
		Monitor mo = new Monitor(theaterCapacity, party_ticket, totalMovie, system_start_time);
		
		//print a line tell me that the simulation begin
		System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "]" + "-------------------The Theater is open-------------------");
		
		//To create a Clock thread
		new Clock("Theatre Clock", system_start_time, mo, totalMovie);
		
		//To create a Speaker thread
		new Speaker("speaker", system_start_time, mo, totalMovie);
		
		//To create certain Visitors threads
		/*for(int i = 1; i <= numVisitors; i++) {
			new Visitors("Visitor", i, system_start_time, mo);
		}*/
		
	}
}
