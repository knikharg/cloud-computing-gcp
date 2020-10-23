package worker;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import stub.KeyValueStoreGrpc;
import stub.Keyvalue;
import stub.Keyvalue.KeyValue;


public class Worker {
	//ConcurrentMap<Object, TaskStatus> statusMap = new ConcurrentHashMap<Object, TaskStatus>();
	private static String Id;
	private static String jar;
	private static  String keyValueIp;
	private static String keyValuePort;
	private static String output;
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		ArrayList argsList;
		
		argsList = new ArrayList();
		Collections.addAll(argsList, args);
		if(argsList!=null || !argsList.isEmpty()) {
			if(argsList.get(0)!=null) {
				jar = argsList.get(0).toString();
			 }
			if(argsList.get(1)!=null) {
				Id = argsList.get(1).toString();
			 }
			if(argsList.get(2)!=null) {
				keyValueIp = argsList.get(2).toString();
			 }
			if(argsList.get(3)!=null) {
				keyValuePort = argsList.get(3).toString();
			 }
			if(argsList.get(4)!=null) {
				output = argsList.get(4).toString();
			 }
		}
		
		
		KeyValueStoreGrpc.KeyValueStoreBlockingStub kvstub = establishKVStoreConnection( keyValueIp,  keyValuePort);
		KeyValue keyValuePair = kvstub.get(Keyvalue.Key.newBuilder().setKey(Id).build());
		String input = keyValuePair.getValue();
		File f = new File(System.getProperty("user.dir"));
		String path = f.getAbsolutePath();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" + Id+".txt"));
			out.append(input);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		executeProcess();
		Thread.sleep(10*1000);
		BufferedReader in = new BufferedReader(new FileReader(path + "/" + output + ".txt"));
		StringBuffer outputContent = new StringBuffer();
		String currLine="";
		while ((currLine = in.readLine()) != null) {
			outputContent.append(currLine+"\r\n");
		}
		KeyValue setRequest =KeyValue.newBuilder().setKey(output).setValue(outputContent.toString()).build();
		stub.Keyvalue.APIResponse response = kvstub.set(setRequest);
		 if (response.getResponseCode() != 200) {
			  LOGGER.error("Error saving to keyValueStore "); 
		 }
		 if(Id.startsWith("r")) {
			 KeyValue Request =KeyValue.newBuilder().setKey(Id+"_status").setValue("COMPLETED").build();
				stub.Keyvalue.APIResponse response1 = kvstub.set(setRequest);
				 if (response1.getResponseCode() != 200) {
					  LOGGER.error("Error saving status to keyValueStore "); 
				 }
		 }
		
		
	}
	
	
	
	
	public static void executeProcess(){
		BufferedReader reader; 
		try { 
				  
			ProcessBuilder pb =
					   new ProcessBuilder("java", "-jar", jar,String.valueOf(Id) , output);
					
			File f = new File(System.getProperty("user.dir"));
			String path = f.getParent();
			File output = new File(f.getAbsoluteFile()+"/"+Id+"log.log");
					 pb.redirectErrorStream(true);
					 pb.redirectOutput(Redirect.appendTo(output));
					 Process p = pb.start();
					 assert pb.redirectInput() == Redirect.PIPE;
					 assert pb.redirectOutput().file() == output;
					 assert p.getInputStream().read() == -1;
	}catch(Exception e) {
		
	}
	
}
	
	  public static KeyValueStoreGrpc.KeyValueStoreBlockingStub establishKVStoreConnection(String ipAdress, String port) {
		  
	  ManagedChannel channel = ManagedChannelBuilder.forAddress(ipAdress,Integer.parseInt(port)).usePlaintext().build(); return
	  KeyValueStoreGrpc.newBlockingStub(channel); 
	  }
	 
	
}
