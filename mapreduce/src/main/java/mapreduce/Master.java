package mapreduce;

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

import io.grpc.Server;
import io.grpc.ServerBuilder;
import services.Mapper;
import services.Reducer;
import services.ShutDown;
import services.StartMapReduce;


public class Master {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	private static String keyValueIp;
	private static String masterIP;
	private static String keyValuePort;
	private static String masterPort;
	private static Integer mappers;
	private static Integer reducers;
	private static String input;
	private static String output;
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		try {
			ArrayList argsList;
			
			argsList = new ArrayList();
			Collections.addAll(argsList, args);
			if(argsList!=null || !argsList.isEmpty()) {
				if(argsList.get(0)!=null) {
					keyValuePort =argsList.get(0).toString();
				 }
				if(argsList.get(1)!=null) {
					masterPort = argsList.get(1).toString();
				 }
			}
			ConcurrentMap<Object, Object> mapperInfo = new ConcurrentHashMap<>();
		    ConcurrentMap<Object, Object> reducerInfo = new ConcurrentHashMap<>();
		  
			Server server = ServerBuilder.forPort(Integer.parseInt(masterPort))
	                .addService(new StartMapReduce(mapperInfo, reducerInfo))
	                .addService(new ShutDown()).build();
	        server.start();
	        LOGGER.info("Master Server started on port  "+ masterPort);
	        server.awaitTermination();
			 
		} catch (IOException | InterruptedException e) {
			LOGGER.error("Error reading configuration  or server interrupted :: ", e);
		}
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
				keyValuePort =prop.getProperty("keyValuePort","5001");
				masterPort = prop.getProperty("masterPort","6001");
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
