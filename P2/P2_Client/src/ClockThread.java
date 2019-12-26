import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClockThread implements Runnable{
	
	private String host;
	private int port;
	private String name;
	private int totalMovie = 4;
	private int countMovie = 1;
	
	public ClockThread(String host, int port, String name, int totalMovie){
		this.host = host;
		this.port = port;
		this.name = name;
		this.totalMovie = totalMovie;
		new Thread(this).start();
	}
		
	//this receive method means that server already gets and run method that is sent by this thread.
	public void receive(InputStream inputStream) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(inputStream);
		String str = (String) ois.readObject();
		System.out.println(str);
	}
	
	public void run(){
		try {
			//connect with server
			Socket socket = new Socket(host, port);
		    OutputStream outputStream = socket.getOutputStream();
		    ObjectOutputStream oos = new ObjectOutputStream(outputStream);
		    MethodTable mt = new MethodTable(name, "NONE" , countMovie);
		    oos.writeObject(mt);
		    oos.flush();
		    
		    //waiting for server reply
		    InputStream inputStream = socket.getInputStream();		    
		    ObjectInputStream ois = new ObjectInputStream(inputStream);
		   	String str = (String) ois.readObject();
		    System.out.println(str);
		    
		    //Clock uses methods from Server's database.
			while(countMovie <= totalMovie) {
				try {
					Thread.sleep(3000);
					//outputStream = socket.getOutputStream();
				    oos = new ObjectOutputStream(outputStream);
					mt.setMethodName("movieStart");
					oos.writeObject(mt);
					oos.flush();
					receive(inputStream);
					
					Thread.sleep(3000);
					//outputStream = socket.getOutputStream();
				    oos = new ObjectOutputStream(outputStream);
					mt.setMethodName("movieEnd");
					oos.writeObject(mt);
					oos.flush();
					receive(inputStream);

					countMovie++;
					mt.setCount(countMovie);
					Thread.sleep(3000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//tell server, clock is done already.
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
