package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Tests {

	private static DataInputStream input;
	private static DataOutputStream output;

	// static Random r = new Random();
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		String dataReceived;
		List<String> SETCommands = new ArrayList<>();
		List<String> GETCommands = new ArrayList<>();

		for (int i = 0; i < 500; i++) {
			System.out.println("Test connection " + i);
			try {
				InetAddress host = InetAddress.getLocalHost();
				Socket s = new Socket(host.getHostAddress(), 5001);
				output = new DataOutputStream(s.getOutputStream());
				input = new DataInputStream(s.getInputStream());
				for (int j = 1; j < 9; j++) {
					SETCommands.add("SET CLIENT"+i+ "KEY"+ j + " 6 "+ "VALUE"+j);
				}

				for (int j = 1; j < 9; j++) {
					GETCommands.add("GET CLIENT"+i+ "KEY"+ j);
				}
				for (String command : SETCommands) {
					System.out.println(command);
					output.writeUTF(command);
					output.flush();
					if ((dataReceived = input.readUTF()) != null) {
						System.out.println(dataReceived);
					}
				}
				for (String command : GETCommands) {
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
