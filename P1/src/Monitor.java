//Name: Yifei Du
import java.util.Vector;
public class Monitor {

	private int theaterCapacity = 8;	// the capacity of this theater
	private int party_ticket = 3;		// max members in each group 
	private int totalMovie = 4;			//Throughout the day, how many movies will show
	private long system_start_time = 0;	//the program start time
	private boolean theaterIsOpen = true; // Only clock thread can change this value. if this value is false, it means that the last movie is done in this day, all threads should terminate. 
	private Object theaterIsOpenObject = new Object(); // change theaterIsOpen will be on this theaterIsOpenObject Object
	
	public Object speakerWaiting = new Object();  //speaker waits for next movie
	public Object speakerDistributeTickets = new Object(); 	//speaker waits to Distribute Tickets
	public Object visitorsInTheatre = new Object();	//visitors already get a seat and ready for watch the movie
	public Object door = new Object();				//all visitor go in or go out theater should use one door.
	private Vector<Object> visitorsInLobby = new Vector<Object>();	//if Visitors can not get a seat, then he/she wait in lobby.
	private Vector<Vector<Integer>> groups = new Vector<Vector<Integer>>();	//In the theater, visitors gather in to certain group for get group tickets.
	
	private int countMovie = 0;	//Throughout the day, how many movies have been shown
	private int countseats = 0;	//To show what seat dose visitor get.
	private int thisMovieVisitors = 0;	//To show how many visitors in this movie
	private boolean movieIsShowing = false;	//If this value is true, no visitors can disturb the movie that is in session
	private boolean VisitorsGoInTheater = true; //if this value is false, no visitors can go in theater
	
	public Monitor(int theaterCapacity, int party_ticket, int totalMovie, long system_start_time) {
		this.theaterCapacity = theaterCapacity;
		this.party_ticket = party_ticket;
		this.totalMovie = totalMovie;
		this.system_start_time = system_start_time;
	}
	
	public void goInTheatre(int i) {
		Object convey = new Object();
		synchronized (convey) {
			while (cannotGoInTheatreNow(convey, i))
					try {	convey.wait(); 
						}
		            catch (InterruptedException e) { 
		               		//continue; 
		               	}
		}
		
		synchronized (visitorsInTheatre) {
			try {
				visitorsInTheatre.wait(); //here, this visitor gets a seat and waits for next movie.
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public synchronized boolean cannotGoInTheatreNow(Object convey, int i) {
		boolean status;
		if(VisitorsGoInTheater == false || countseats >= theaterCapacity) {
			status = true;
			visitorsInLobby.addElement(convey);
			System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] Visitor" + i + ": can not go in theater now, so go to lobby.");
		}
		else{
			status = false;
			synchronized (door) {
					try {
						Thread.sleep(50 + (int)(1+Math.random()*(50)));
						countseats++;
						System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] Visitor" + i + ": successful!!! go in the theater and gets a seat" + countseats);
					} catch (InterruptedException e) {
						e.printStackTrace();
				}
			}
		}
		return status;
	}
	
	public void gatherIntoGroups (int i) {
		Vector<Integer> group;
		synchronized (groups) {
			if (groups.isEmpty()) { 
				group = new Vector<Integer>();
				group.add(i);
				groups.add(group);
			}
			else {
				group = groups.lastElement();
				if (group.size() < party_ticket) { 
					group.add(i);
				}
				else { 
					group = new Vector<Integer>();
					group.add(i);
					groups.add(group);
				}
			}
		}
		synchronized (group) {
			try {
				if(isTheLastGroup()) {
					synchronized (speakerDistributeTickets) {
						speakerDistributeTickets.notify();
					}
				}
				group.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public synchronized boolean isTheLastGroup() {
		thisMovieVisitors--;
		if(thisMovieVisitors == 0)
			return true;
		else {
			return false;
		}
	}
	
	public void goOutTheater(int i) {
		synchronized (door) {
			try {
				Thread.sleep(50 + (int)(1+Math.random()*(50)));
				System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] Visitor" + i + ": already get the ticket and go out theater" );
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void distributeTickets() {
		Vector<Integer> group = new Vector<Integer>();
		int countGroups = 1;
		synchronized (groups) {
			while (!groups.isEmpty()) {
				synchronized (groups.elementAt(0)){
					group = groups.elementAt(0);
					System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] Speaker: gives the tickets for the groups" + countGroups + ", members of this group are Visitors" + group);
					groups.firstElement().notifyAll(); // notify all shuttles in the current group
					groups.removeElementAt(0);
					countGroups++;
				}
			}
		}
	}
	public void speakerWork() {
		
		synchronized (speakerWaiting) {
			try {
				speakerWaiting.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		synchronized(speakerDistributeTickets) {
			if(shortTalkBegin()) {
				try {
					speakerDistributeTickets.wait();
					distributeTickets();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public synchronized boolean shortTalkBegin(){
		if(movieIsShowing == false && countMovie > 0 && countMovie <= totalMovie && countseats > 0) {
			try {
				System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] Speaker: is speeching for the " + countMovie + "th movie's visitors!");
				Thread.sleep(1000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			synchronized (visitorsInTheatre) {
				visitorsInTheatre.notifyAll(); //here, this visitor gets a seat and waits for next movie.
				System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] Speaker: speech is over for the " + countMovie + "th movie." + " All visitors leave the seat and gather into Groups");
			}
			
			thisMovieVisitors = countseats;
			countseats = 0;
			VisitorsGoInTheater = true;
			
			if (visitorsInLobby.size() > 0) {
				while (visitorsInLobby.size() > 0) {
					synchronized (visitorsInLobby.elementAt(0)) {
						visitorsInLobby.elementAt(0).notify();
					}
					visitorsInLobby.removeElementAt(0);
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	public synchronized void movieStart() {
		countMovie++;
		movieIsShowing = true;
	}
	
	public synchronized void movieEnd() {
		movieIsShowing = false;
		if(countMovie == totalMovie) {
			synchronized(theaterIsOpenObject) {
				theaterIsOpen = false;
			}
		}
		synchronized (speakerWaiting) {
			speakerWaiting.notify();
		}
	}
	
	public boolean theaterIsOpen() {
		synchronized(theaterIsOpenObject) {
			if(theaterIsOpen == true)
				return true;
			else
				return false;
		}
	}
	
}