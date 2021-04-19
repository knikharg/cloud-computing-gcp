package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

	private Socket socket = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private static final String EXIT = "EXIT";
	private static final String SET = "SET";

	public Client(String address, int port) throws IOException {
		Scanner clientInput = new Scanner(System.in);
		try {
			String dataReceived;
			socket = new Socket(address, port);
			System.out.println("[Client] :: Connected");
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());

			while (true) {
				if (clientInput.hasNext()) {
					String currLine = clientInput.nextLine(); // read client input
					if (currLine.equalsIgnoreCase(EXIT)) { // if curr input EXIt close connection
						System.out.println("[Client]:: Connection closed");
						break;
					}

					if (( currLine.startsWith(SET)  || currLine.startsWith("set")) && clientInput.hasNext()) {
						currLine += " "+  clientInput.nextLine();
					}
					// System.out.println("[Client] :: Input received : " + currLine);
					output.writeUTF(currLine);
					output.flush();

					if ((dataReceived = input.readUTF()) != null) {
						System.out.println(dataReceived); // print received data
					}
				}
			}

		} catch (IOException i) {
			i.printStackTrace();
		} finally {
			output.close();
			input.close();
			clientInput.close();
			socket.close();
		}

	}

	public static void main(String args[]) throws UnknownHostException {
		try {
			InetAddress host = InetAddress.getLocalHost();
			Client client = new Client(host.getHostAddress(), 5001);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
