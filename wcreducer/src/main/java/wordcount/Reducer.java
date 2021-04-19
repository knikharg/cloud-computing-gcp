package wordcount;

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
		String path = f.getAbsolutePath();
		BufferedReader in = new BufferedReader(new FileReader(path + "/" + reducerId + ".txt"));

		
		Map<String, Integer> map = new HashMap<String, Integer>();
		BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" + outputFile+ ".txt"));
		String currLine;
		while ((currLine = in.readLine()) != null) {
			String[] line = currLine.split(" ");
			map.put(line[0], map.getOrDefault(line[0], 0) + 1);
		}
		StringBuffer outputContent = new StringBuffer();
		map.forEach((key, value) -> {
			outputContent.append(key + " " + value + "\r\n");

		});
		out.append(outputContent.toString());
		out.close();
		in.close();

	}
	
}
