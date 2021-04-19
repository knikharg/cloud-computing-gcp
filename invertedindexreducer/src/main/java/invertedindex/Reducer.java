package invertedindex;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	public static void main(String[] args) {

		String reducerId = "";
		String outputFile = "";
		ArrayList argsList;
		
		try {
			
			argsList = new ArrayList();
			Collections.addAll(argsList, args);
			if(argsList.get(0)!=null) {
				reducerId = argsList.get(0).toString();
		     }
			if(argsList.get(1)!=null) {
				outputFile = argsList.get(1).toString();
		     }
		     LOGGER.info("reducer ID :: " + reducerId );
		     LOGGER.info("output file :: " + outputFile );
			reduce(reducerId, outputFile);

		} catch (IOException e) {
			LOGGER.error("Error reading input at Mapper", e);
			
		}
		LOGGER.error("reducer task for completed ::", reducerId);
	}

	private static void reduce(String reducerId, String outputFile) throws IOException {

		File f = new File(System.getProperty("user.dir"));
		String path = f.getParent();
		BufferedReader in = new BufferedReader(new FileReader(path + "/" + reducerId + ".txt"));

		/*
		 * FileWriter fwOb = new FileWriter(path+"/"+reducerId+".txt", false);
		 * PrintWriter pwOb = new PrintWriter(fwOb, false); pwOb.flush(); pwOb.close();
		 * fwOb.close();
		 */
		HashMap<String, HashMap<String, Integer>> map = new HashMap<>();
		BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" + outputFile + ".txt", true));
		String currLine;
		while ((currLine = in.readLine()) != null) {
			String[] line = currLine.split(" ");
			map.putIfAbsent(line[0],new HashMap<String, Integer>() );
			HashMap<String, Integer> fileMap = map.get(line[0]);
			fileMap.put(line[1], fileMap.getOrDefault(line[1], 0)+1);
		}
		StringBuilder line = new StringBuilder();
		int count=0;
		map.forEach((key, value) -> {
			line.append(key +" [");
			
			map.get(key).forEach((k, v) -> {
				
				line.append(k +" "+ v +",");
			});
			line.deleteCharAt(line.length()-1);
			line.append("]\r\n");
			
		});
		out.append(line.toString());
		out.close();
		in.close();
		

	}

}
