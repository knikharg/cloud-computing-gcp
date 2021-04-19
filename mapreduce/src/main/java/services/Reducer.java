package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mapreduce.TaskStatus;

public class Reducer implements Runnable{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	ConcurrentMap<Object, TaskStatus> statusMap = new ConcurrentHashMap<Object, TaskStatus>();
	private Object reducerId;
	private String jar;
	private String output;
	@Override
	public void run() {
	LOGGER.info("Reducer task initialized :: " + (String) reducerId);
		BufferedReader reader;
		TaskStatus  curr = (TaskStatus) statusMap.get(reducerId);
		curr.setTaskStatus("IN_PROGRESS");
		try {
			ProcessBuilder pb =
					   new ProcessBuilder("java", "-jar", "../"+this.jar,String.valueOf(reducerId) , output);
					
			File f = new File(System.getProperty("user.dir"));
			String path = f.getParent();
			File output = new File(f.getAbsoluteFile()+"/"+reducerId+"log.log");
					 pb.redirectErrorStream(true);
					 pb.redirectOutput(Redirect.appendTo(output));
					 Process p = pb.start();
					 assert pb.redirectInput() == Redirect.PIPE;
					 assert pb.redirectOutput().file() == output;
					 assert p.getInputStream().read() == -1;
		    //int exitVal = proc.waitFor();
			curr.setIsCompleted(true);
			curr.setTaskStatus("COMPLETED");
			
			p.destroy();
			
			LOGGER.info("Mapper task over ::" + reducerId);
		} catch (IOException e) {

			e.printStackTrace();
		} 
		
		

	}

	public Reducer(ConcurrentMap<Object, TaskStatus> statusMap, Object reducerId,String jar, String output) {

		this.statusMap = statusMap;
		this.reducerId = reducerId;
		this.jar = jar;
		this.output = output;
		
	}
	

}
