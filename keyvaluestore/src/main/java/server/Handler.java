package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Handler extends Thread {

	final DataInputStream input;
	final DataOutputStream output;
	final Socket s;
	private static ConcurrentMap<Object, Object> map = new ConcurrentHashMap<Object, Object>();
	final static String SET = "SET";
	final static String GET = "GET";
	final static String STORED = "STORED\r\n";
	final static String NOTSTORED = "NOT-STORED\r\n";
	final static String EXIT = "EXIT";
	final static String filePath = "keyValueStore.txt";
	final static String optionWrite = "WRITE";
	final static String INVALID_COMMAND = "INVALID COMMAND\r\n";
	final static String INVALID_KEY = "INVALID KEY\r\n";
	int clientID;
	public Handler(Socket s, DataInputStream input, DataOutputStream output, int clientID)
			throws ClassNotFoundException, IOException {
		this.s = s;
		this.input = input;
		this.output = output;
		this.clientID = clientID;
		map = new ConcurrentHashMap<Object, Object>();
		persistData("READ");
	}

	@Override
	public void run() {
		String received;
		String toReturn = null;
		try {
			while (true) {

				received = input.readUTF(); // command received
				String[] commands = received.split(" ");
				//System.out.println("[Client Thread]:" + clientID + " Input received :: " + Arrays.toString(commands));
				if (commands != null) {
					String command = commands[0];
					String key = commands[1];
					StringBuilder value = new StringBuilder();
					if (SET.equalsIgnoreCase(command) && commands.length >= 4) {
						//System.out.println("[Client Thread]:: " + clientID +" Inside SET");
						Integer bytes = Integer.parseInt(commands[2].trim());
						//System.out.println(bytes);
						int itr = 3;
						while (itr < commands.length && value.length()<bytes) {
							value.append(commands[itr]);
							if(itr>3) value.append(" ");
							itr++;
						}
						//System.out.println("[Client Thread]:" + clientID + " Value length"+ value.length() + " bytes " + bytes  );
						if (value.length() != bytes) {
							toReturn = NOTSTORED;
						} else if (key != null && bytes != null && value != null) {
							map.put(key, value.toString());
							persistData(optionWrite);
							toReturn = STORED;
						}
					} else if (GET.equalsIgnoreCase(command) && commands.length == 2 && key != null) {
						if (map.containsKey(key)) {

							toReturn = "VALUE " + key + " " + String.valueOf(map.get(key)).length() + "\r\n"
									+ String.valueOf(map.get(key)) + "\r\n" + "END\r\n";
						} else {
							toReturn = "VALUE " + key + " " + 0 + "\r\n"
									+"" + "\r\n" + "END\r\n";
						}
					} else if (EXIT.equalsIgnoreCase(commands[0])) {
						System.out.println("[Client Thread]:: " + clientID + "  closing connection");
					} else {
						toReturn = INVALID_COMMAND;
					}
				} else {
					toReturn = INVALID_COMMAND;
				}
				System.out.println(toReturn);
				if (toReturn != null && !toReturn.isEmpty()) {
					output.writeUTF(toReturn);
				}
				output.flush();

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {

				this.output.close();
				this.input.close();
				this.s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void persistData(String option) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {

				if (!file.createNewFile()) {
					throw new IOException("[Client Thread]:" + clientID + "Couldn't create a new file " + filePath);
				}
			}
			if (option.equals(optionWrite)) {
				FileOutputStream fileOutput = new FileOutputStream(file);
				ObjectOutputStream out = new ObjectOutputStream(fileOutput);
				out.writeObject(map);
				out.close();
				fileOutput.close();
			} else {
				FileInputStream fileIn = new FileInputStream(file);
				if (file.length() != 0) {
					ObjectInputStream in = new ObjectInputStream(fileIn);
					map = (ConcurrentMap<Object, Object>) in.readObject();
					in.close();
				}
				fileIn.close();
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 

	}

}