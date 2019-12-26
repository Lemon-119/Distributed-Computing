import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VisitorsThread implements Runnable{
	
	private String host;
	private int port;
	private String name;
	private int id;
	
	public VisitorsThread(String host, int port, String name, int id){
		this.host = host;
		this.port = port;
		this.name = name;
		this.id = id;
		new Thread(this).start();
	}

	//this receive method means that server already gets and run method that is sent by this thread.
	public void receive(InputStream inputStream) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		String str = (String) ois.readObject();
		System.out.println(str);
	}
	
	//both visitors and speaker have to check if theater Is open or not. only clock thread can close theater
	public boolean theaterIsOpen(OutputStream outputStream, InputStream inputStream) throws IOException, ClassNotFoundException {
		ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		MethodTable mt = new MethodTable(name, id, "theaterIsOpen", 0);
	    oos.writeObject(mt);
	    oos.flush();
	    receive(inputStream);
	    
	    ObjectInputStream ois = new ObjectInputStream(inputStream);
		String str = (String) ois.readObject();
		if(str.equals("TRUE")) {
			return true;
		}
		else{
			return false;
		}
		
	}
	public void run() {

		try {
			//connect with server
			Socket socket = new Socket(host, port);
		    OutputStream outputStream = socket.getOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		    MethodTable mt = new MethodTable(name, id, "NONE" , 1);
		    oos.writeObject(mt);
		    oos.flush();
		    
		    //waiting for server reply
		    InputStream inputStream = socket.getInputStream();		    
		    ObjectInputStream ois = new ObjectInputStream(inputStream);
		   	String str = (String) ois.readObject();
		    System.out.println(str);
		    
		    //Visitor uses methods from Server's database.
		    if(theaterIsOpen(outputStream, inputStream)) {
		    	
		    	oos = new ObjectOutputStream(outputStream);
			    mt = new MethodTable(name, id, "goInTheatre" , 1);
			    oos.writeObject(mt);
			    oos.flush();
			    receive(inputStream);
			    
		    	oos = new ObjectOutputStream(outputStream);
			    mt = new MethodTable(name, id, "gatherIntoGroups" , 1);
			    oos.writeObject(mt);
			    oos.flush();
			    receive(inputStream);
			    
		    	oos = new ObjectOutputStream(outputStream);
			    mt = new MethodTable(name, id, "goOutTheater" , 1);
			    oos.writeObject(mt);
			    oos.flush();
			    receive(inputStream);
		    	
		    }
		    
		    
		    //75% visitor watching movie again.
		   if((int)(1+Math.random()*(100)) <= 75 && theaterIsOpen(outputStream, inputStream)) {
		    	oos = new ObjectOutputStream(outputStream);
			    mt = new MethodTable(name, id, "goInTheatre" , 2);
			    oos.writeObject(mt);
			    oos.flush();
			    receive(inputStream);
			    
		    	oos = new ObjectOutputStream(outputStream);
			    mt = new MethodTable(name, id, "gatherIntoGroups" , 2);
			    oos.writeObject(mt);
			    oos.flush();
			    receive(inputStream);
			    
		    	oos = new ObjectOutputStream(outputStream);
			    mt = new MethodTable(name, id, "goOutTheater" , 2);
			    oos.writeObject(mt);
			    oos.flush();
			    receive(inputStream);
		   }
		    
		    
		    
		    
		    //tell server, this visitor is done already.
			oos = new ObjectOutputStream(outputStream);
			mt.setMethodName("DONE");
			oos.writeObject(mt);
			oos.flush();
			receive(inputStream);
		    
			//close all
			oos.close();
			ois.close();
		    outputStream.close();
		    inputStream.close();		    
		    socket.close();	
		    
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

		

}
