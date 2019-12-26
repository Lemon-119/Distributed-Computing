import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    private static int serverPort = 55533;
	private static int theaterCapacity = 8;
	private static int party_ticket = 3;
	private static int totalMovie = 4;
	private static ServerSocket server;
	
	public static void main(String[] args) throws IOException {
		long system_start_time = System.currentTimeMillis();
		Monitor mo = new Monitor(theaterCapacity, party_ticket, totalMovie, system_start_time);
		
		System.out.println("-------------------- Server is open now! waits for clients! --------------------");
		server = new ServerSocket(serverPort);
		while (true) {
            Socket socket = server.accept();
            new SubServerThread(socket, mo, system_start_time);
        }
	}
}
