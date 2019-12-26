import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SubServerThread implements Runnable{
	
	private Socket socket;
	private Monitor mo = null;
	private long system_start_time;
	
	public SubServerThread(Socket socket, Monitor mo, long system_start_time) {
		this.socket = socket;
		this.mo = mo;
		this.system_start_time = system_start_time;
		new Thread(this).start();
	}
	
	//for clock and speaker
	private void msg(String name, String message) {
        System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] " + name + ": " + message);
    }
	
	//for visitors because more than one visitors
	private void msg2(String name, int id, String message) {
        System.out.println("[" + (System.currentTimeMillis() - system_start_time) + "] " + name +  id +": " + message);
    }
	
	//only out put time.
	private String msg3() {
        return("[" + (System.currentTimeMillis() - system_start_time) + "] ");
    }
	
	//reply for clock, speaker or visitor.
	private void reply( OutputStream outputStream, MethodTable mt) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);		
	    if(mt.getID() == -1) {
	    	oos.writeObject(msg3() + "Hi, "+ mt.getThreadName() + ", Server already recevies and run your method that is " + mt.getMethodName());	
	    }
	    else {
	    	oos.writeObject(msg3() + "Hi, "+ mt.getThreadName() + mt.getID() + ", Server already recevies and run your method that is " + mt.getMethodName());
	    }
		oos.flush();
	}
	
	//reply speaker or visitor that theater is open or not
	private void reply2(OutputStream outputStream, MethodTable mt, boolean isOpen) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		if(isOpen) {
			oos.writeObject("TRUE");
		}
		else {
			oos.writeObject("FALSE");
		}
		oos.flush();
	}
	
	public void run() {
		try {
			
			//waits and listen for client thread
			InputStream inputStream = socket.getInputStream();			
			ObjectInputStream ois = new ObjectInputStream(inputStream);
		    MethodTable mt = (MethodTable) ois.readObject();		    
		    if(mt.getID() == -1) {
		    	msg("SERVER", " connects with " + mt.getThreadName() + " Thread from Client");
		    }
		    else {
		    	msg("SERVER", " connects with " + mt.getThreadName()+ mt.getID() + " Thread from Client");
		    }
		    
		    //reply thread of client
		    OutputStream outputStream = socket.getOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		    if(mt.getID() == -1) {
		    	oos.writeObject(msg3() + "Hello, "+ mt.getThreadName() + ", you already connect with Server!!!");	
		    }
		    else {
		    	oos.writeObject(msg3() + "Hello, "+ mt.getThreadName() + mt.getID() + ", you already connect with Server!!!");
		    }
		    oos.flush();

		    
		    //3 case
		    //if this thread is clock
		    if(mt.getThreadName().equals("Clock")) {
		    	msg(mt.getThreadName(), "--------------------------------------------------");
				while(true) {
					inputStream = socket.getInputStream();
					ois = new ObjectInputStream(inputStream);
				    mt = (MethodTable) ois.readObject();			    
				    if(mt.getMethodName().equals("movieStart")) {
				    	reply(outputStream, mt);
						mo.movieStart();
						msg(mt.getThreadName(), "--------------------------------------------------");
						msg(mt.getThreadName(), "The " + mt.getCount() +"th movie is begining.");
				    }
				    
				    else if(mt.getMethodName().equals("movieEnd") ) {
				    	reply(outputStream, mt);
						mo.movieEnd();
						msg(mt.getThreadName(), "--------------------------------------------------");
						msg(mt.getThreadName(), "The "+ mt.getCount() +"th movie is over.");
				    }
					
				    
					if(mt.getMethodName().equals("DONE") ) {
						reply(outputStream, mt);
						break;
					}
				}
				msg(mt.getThreadName(), "Today all movies are over. Theatre is Closed Now!-------------------------------");		    	
		    }
		    
		    
		    //if it is Speaker thread
		    else if(mt.getThreadName().equals("Speaker")) {
		    	msg(mt.getThreadName(), "go to the theatre, already for speech!");
		    	while(true) {
		    		inputStream = socket.getInputStream();
					ois = new ObjectInputStream(inputStream);
				    mt = (MethodTable) ois.readObject();	
		    		
				    if(mt.getMethodName().equals("theaterIsOpen")) {
				    	reply(outputStream, mt);
						reply2(outputStream, mt, mo.theaterIsOpen());
				    }
				    
				    else if(mt.getMethodName().equals("speakerWork") ) {
				    	reply(outputStream, mt);
				    	mo.speakerWork();
				    }
		    		
		    		if(mt.getMethodName().equals("DONE") ) {
						reply(outputStream, mt);
						break;
					}
		    	}	    	
		    }
		    
		    
		    //if it is Visitor thread
		    else if(mt.getThreadName().equals("Visitor")){			
		    	msg(mt.getThreadName(), "arrives at theater.");
		    	boolean notSecondWatching = true;
		    	while(true) {
		    		inputStream = socket.getInputStream();
					ois = new ObjectInputStream(inputStream);
				    mt = (MethodTable) ois.readObject();	
				    
		    		if(notSecondWatching == true && mt.getCount() == 2) {
		    			msg2(mt.getThreadName(), mt.getID(), "is so impressed with the presentation, want to see movie again!");
		    			notSecondWatching = false;
		    		}
		    		
				    if(mt.getMethodName().equals("theaterIsOpen")) {
				    	reply(outputStream, mt);
						reply2(outputStream, mt, mo.theaterIsOpen());
				    }
				    
				    else if(mt.getMethodName().equals("goInTheatre") ) {
				    	reply(outputStream, mt);
				    	mo.goInTheatre(mt.getID());
				    }
				    
				    else if(mt.getMethodName().equals("gatherIntoGroups") ) {
				    	reply(outputStream, mt);
				    	mo.gatherIntoGroups(mt.getID());
				    }
				    
				    else if(mt.getMethodName().equals("goOutTheater") ) {
				    	reply(outputStream, mt);
				    	mo.goOutTheater(mt.getID());
				    }
		    		
		    		if(mt.getMethodName().equals("DONE") ) {
						reply(outputStream, mt);
						break;
					}
		    	}
		    	
		    	
		    }
		    
		    //close all
		    oos.close();
		    ois.close();
		    inputStream.close();
		    outputStream.close();
		    socket.close();
		    
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}  

	}

}
