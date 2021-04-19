package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mapreduce.TaskStatus;

public class Mapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	ConcurrentMap<Object, TaskStatus> statusMap = new ConcurrentHashMap<Object, TaskStatus>();
	private Object mapperId;
	private Object mapperValue;
	private String jar;
	private String className;

	/*
	 * @Override public void run() { LOGGER.	info("Mapper task initialized :: " +
	 * (String) mapperId); BufferedReader reader; try { TaskStatus curr =
	 * (TaskStatus) statusMap.get(mapperId); curr.setTaskStatus("IN_PROGRESS");
	 * 
	 * ProcessBuilder processBuilder = new ProcessBuilder(new String[] { "java",
	 * "-jar", this.jar , "\\r\\n",String.valueOf(mapperId) }); processBuilder =
	 * processBuilder.redirectErrorStream(true);
	 * 
	 * ProcessBuilder pb = new ProcessBuilder("java", "-jar",
	 * "../"+this.jar,String.valueOf(mapperId) );
	 * 
	 * File f = new File(System.getProperty("user.dir")); //String path =
	 * f.getParent(); File output = new
	 * File(f.getAbsoluteFile()+"/"+mapperId+"log.log");
	 * pb.redirectErrorStream(true); pb.redirectOutput(Redirect.appendTo(output));
	 * Process p = pb.start(); assert pb.redirectInput() == Redirect.PIPE; assert
	 * pb.redirectOutput().file() == output; assert p.getInputStream().read() == -1;
	 * //int exitVal = proc.waitFor(); curr.setIsCompleted(true);
	 * curr.setTaskStatus("COMPLETED"); LOGGER.info("Process Exit value:: " +
	 * p.exitValue()); //int status = proc.waitFor(); p.destroy();
	 * LOGGER.info("Mapper task over ::" + mapperId); //System.exit(0); } catch
	 * (IOException e) {
	 * 
	 * e.printStackTrace(); }
	 * 
	 * 
	 * 
	 * }
	 */

	private static void exhaustStream(InputStream stream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
		// reader.lines().forEach(s -> LOGGER.info(s));
	}

	public Mapper(ConcurrentMap<Object, TaskStatus> statusMap, Object mapperId, String jar) {

		this.statusMap = statusMap;
		this.mapperId = mapperId;

		this.jar = jar;

	}

}
