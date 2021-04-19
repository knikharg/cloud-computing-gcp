package client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import server.Handler;
public class Test2 {

	private static DataInputStream input;
	private static DataOutputStream output;

	// static Random r = new Random();
	@SuppressWarnings("resource")
		public static void main(String[] args) throws IOException {
			String dataReceived;
			List<String> listOfSETCommands = new ArrayList<>();
			List<String> listOfGETCommands = new ArrayList<>();

			for (int i = 0; i < 200; i++) {
				System.out.println("Test connection " + i);
				try {
					InetAddress host = InetAddress.getLocalHost();
					Socket s = new Socket(host.getHostAddress(), 5001);
					output = new DataOutputStream(s.getOutputStream());
					input = new DataInputStream(s.getInputStream());
					for (int j = 1; j < 9; j++) {
						listOfSETCommands.add("SET CLIENT"+i+ "KEY"+ j + " 6 "+ "VALUE"+j);
					}

					for (int j = 1; j < 9; j++) {
						listOfGETCommands.add("GET CLIENT"+i+ "KEY"+ j);
					}
					for (String command : listOfSETCommands) {
						System.out.println(command);
						output.writeUTF(command);
						output.flush();
						if ((dataReceived = input.readUTF()) != null) {
							System.out.println(dataReceived);
						}
					}
					for (String command : listOfGETCommands) {
						output.writeUTF(command);
						output.flush();
						if ((dataReceived = input.readUTF()) != null) {
							System.out.println(dataReceived);
						}
					}

				} catch (IOException e) {

					e.printStackTrace();
				}

			}

		}

}
