package wordcount;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

	public static void main(String[] args) {
		ArrayList argsList;
		String mapperId = "";
		String output = "";

		try {

			argsList = new ArrayList();
			Collections.addAll(argsList, args);
			if (argsList.get(0) != null) {
				mapperId = argsList.get(0).toString();
			}
			if (argsList.get(1) != null) {
				output = argsList.get(1).toString();
			}
			LOGGER.info("Mapper ID " + mapperId);
			map(mapperId, output);

		} catch (IOException e) {
			LOGGER.error("Error reading input at Mapper", e);
			// e.printStackTrace();
		}
		LOGGER.info("Mapper task for completed ::", mapperId);
	}

	private static void map(String mapperId, String output) throws IOException {
		LOGGER.info("Inside map function :: mapper ID :: " + mapperId);
		File f = new File(System.getProperty("user.dir"));
		String path = f.getAbsolutePath();
		BufferedReader in = new BufferedReader(new FileReader(path + "/" + mapperId + ".txt"));

		BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" + output + ".txt"));
		String currLine;
		StringBuffer outputContent = new StringBuffer();
		while ((currLine = in.readLine()) != null) {
			outputContent.append(currLine + " " + 1 + "\r\n");
		}
		LOGGER.info("Intermediate file location :: mapperID :: "+ mapperId+ path + "/" + output + ".txt");
		out.append(outputContent.toString());
		out.close();
		in.close();

	}

}