package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private Socket          socket;
    private ServerSocket    server; 
    private DataInputStream input;
    private DataOutputStream output;
	private int count;
    public Server(int port) throws IOException, ClassNotFoundException {
    	socket = null; 
    	input = null;
    	count =0;
    	try {
    		server = new ServerSocket(port);
    		while(true) {
    			System.out.println("[Server] started :: Waiting for client");
    			socket = server.accept(); // different socket after accept 
    			System.out.println("[Server] :: Connection accepted " + "client ID " + count);
    			input = new DataInputStream( 
    		                new BufferedInputStream(socket.getInputStream()));  
    			output = new DataOutputStream(socket.getOutputStream());
    			Thread t = new Handler(socket, input, output,++count); 
                t.start(); 
                
    		}
            
		} catch (IOException e) {
			
			System.out.println("[Server] :: Error establishing connection ");
			e.printStackTrace();
		} finally {
			input.close(); 
			output.close();
            socket.close();
		}
    }
   
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		Server s = new Server(5001);
	}

}
