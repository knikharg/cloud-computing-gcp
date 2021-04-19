package grpcserver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import service.KeyValueStoreService;
import stub.Master;

public class KeyValueStoreServer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	private static String keyValueIp;
	private static String masterIP;
	private static String keyValuePort;
	private static Integer masterPort;
	private static Integer mappers;
	private static Integer reducers;
	private static String input;
	private static String output;
	public static void main(String args[]) throws IOException, InterruptedException {
		 int clientID =0;
		// readConfigProperties();
		 
		 ArrayList argsList;
			
			argsList = new ArrayList();
			Collections.addAll(argsList, args);
			if(argsList!=null || !argsList.isEmpty()) {
				if(argsList.get(0)!=null) {
					keyValuePort = argsList.get(0).toString();
				 }
			}
			ConcurrentMap<Object,Object> map = new ConcurrentHashMap<Object,Object>();
			
			Server server = ServerBuilder.forPort(Integer.parseInt(keyValuePort)).addService( new KeyValueStoreService(map,clientID)).build();
	        server.start();

	        LOGGER.info("Key Value Server started on port  "+ keyValuePort);
	        server.awaitTermination();
		
	}
	private static void readConfigProperties() {
		try {
			Properties prop = new Properties();
			InputStream config = Master.class.getClassLoader()
					.getResourceAsStream("configuration.properties");
			if (config != null) {
				prop.load(config);
				masterIP = prop.getProperty("masterIP");
				keyValueIp = prop.getProperty("keyValueIp");
				keyValuePort = prop.getProperty("keyValuePort","5001");
				masterPort = Integer.parseInt(prop.getProperty("masterPort","6001"));
				mappers = Integer.parseInt(prop.getProperty("mappers","5"));
				reducers = Integer.parseInt(prop.getProperty("reducers","5"));
			} else {
				LOGGER.error("Configuration file empty");
			}
		} catch (IOException e) {
			LOGGER.error("Error reading configuration properties",e);
		}
	}
}
