package trigger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.compute.model.Metadata;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import services.GcpComputeVm;
import stub.KeyValueStoreGrpc;
import stub.Keyvalue;
import stub.Keyvalue.KeyValue;
import stub.Master.Config;
import stub.StartMapReduceGrpc;

public class UserProgram {
	private static String keyValueIp;
	private static String masterIP;
	private static Integer keyValuePort;
	private static Integer masterPort;
	private static Integer mappers;
	private static Integer reducers;
	private static String input;
	private static String output;
	private static String mapperjar;
	private static String reducerjar;
	private static String masterInstance;
	private static String keyvalueInstance;
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	private static final String PROJECT_ID = "kasturi-nikharge";

	public static void main(String[] args) throws Exception {

		// read properties
		readConfigProperties();
		long begin = System.currentTimeMillis();
		uploadToGCloud(masterInstance + "-startup.sh");
		uploadToGCloud(keyvalueInstance + "-startup.sh");

		// init cluster - start vms for master and k-v store
		initCluster(masterInstance, masterPort, keyvalueInstance, keyValuePort);
		LOGGER.info("Master  && Key-ValueInstance created");
		Thread.sleep(120 * 1000);

		// keyValueIp = "130.211.226.142"; masterIP = "35.223.86.133";

		String[] inputFiles = input.split(",");
		StringBuilder paths = new StringBuilder();
		for (String file : inputFiles) {
			paths.append(uploadToGCloud(file) + ",");

		}
		LOGGER.info(" Gcloud paths for input files :: " + paths);

		ManagedChannel kvStoreChannel = ManagedChannelBuilder.forAddress(keyValueIp, keyValuePort).usePlaintext()
				.build();
		KeyValueStoreGrpc.KeyValueStoreBlockingStub kvStoreBlockingStub = KeyValueStoreGrpc
				.newBlockingStub(kvStoreChannel);

		KeyValue setRequest = KeyValue.newBuilder().setKey("input").setValue(paths.toString()).build();
		stub.Keyvalue.APIResponse response = kvStoreBlockingStub.set(setRequest);
		if (response.getResponseCode() != 200) {
			LOGGER.error("Error saving input file to key value store ");
		}

		ManagedChannel channelMapReduce = ManagedChannelBuilder.forAddress(masterIP, masterPort).usePlaintext().build();

		StartMapReduceGrpc.StartMapReduceBlockingStub mapred = StartMapReduceGrpc.newBlockingStub(channelMapReduce);
		Config request = Config.newBuilder().setMappers(mappers).setReducers(reducers).setInput(input).setOutput(output)
				.setMasterIP(masterIP).setKeyValueIp(keyValueIp).setKeyValuePort(keyValuePort).setMasterPort(masterPort)
				.setMapperjar(mapperjar).setReducerjar(reducerjar).build();
		stub.Master.APIResponse responseMapred = mapred.runMapred(request);

		if (responseMapred.getResponseCode() != 200) {
			LOGGER.error("Error initiating split for map reduce task ");
		}
		KeyValue keyValuePair = kvStoreBlockingStub.get(Keyvalue.Key.newBuilder().setKey(output).build());
		String OutputString = keyValuePair.getValue();
		File f = new File(System.getProperty("user.dir"));
		String path = f.getAbsolutePath();
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" + output + ".txt"));
			out.append(OutputString);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.info("Deleting instances :: ", masterInstance, keyvalueInstance);

		int op = new GcpComputeVm(masterInstance, null).deleteInstance();
		int op1 = new GcpComputeVm(keyvalueInstance, null).deleteInstance();

		long end = System.currentTimeMillis();

		long totalTime = TimeUnit.MINUTES.convert(end-begin, TimeUnit.MILLISECONDS);
		LOGGER.info("Time taken for task ::"+totalTime );
		System.exit(0);

	}

	private static void readConfigProperties() {
		try {
			Properties prop = new Properties();
			InputStream config = UserProgram.class.getClassLoader().getResourceAsStream("configuration.properties");
			if (config != null) {
				prop.load(config);

				/*
				 * masterIP = prop.getProperty("master.ip"); keyValueIp =
				 * prop.getProperty("keyValue.ip");
				 */

				keyValuePort = Integer.parseInt(prop.getProperty("keyValue.port", "5001"));
				masterPort = Integer.parseInt(prop.getProperty("master.port", "6001"));
				mappers = Integer.parseInt(prop.getProperty("mappers", "5"));
				reducers = Integer.parseInt(prop.getProperty("reducers", "5"));
				mapperjar = prop.getProperty("mapper.jar");
				reducerjar = prop.getProperty("reducer.jar");
				input = prop.getProperty("input");
				output = prop.getProperty("output");
				masterInstance = prop.getProperty("master.instance");
				keyvalueInstance = prop.getProperty("kv.instance");
			} else {
				LOGGER.error("Configuration file empty");
			}
		} catch (IOException e) {
			LOGGER.error("Error reading configuration properties", e);
		}
	}

	public static void initCluster(String masterInstance, Integer masterPort, String keyvalueInstance,
			Integer keyValuePort) throws Exception {
		Metadata kvMetaData = new Metadata();
		List<Metadata.Items> itemsList = new ArrayList<>();
		itemsList.add(new Metadata.Items().setKey("startup-script-url")
				.setValue(String.format("gs://%s/%s-startup.sh", PROJECT_ID, keyvalueInstance)));

		itemsList.add(new Metadata.Items().setKey("kvPort").setValue(keyValuePort.toString()));
		kvMetaData.setItems(itemsList);
		GcpComputeVm keyValueVm = new GcpComputeVm(keyvalueInstance, kvMetaData);
		keyValueIp = keyValueVm.createVMInstance(keyvalueInstance);

		LOGGER.info("Key value store instance launched wth IP" + keyValueIp);

		Thread.sleep(10 * 1000);

		Metadata masterMetadata = new Metadata();
		List<Metadata.Items> itemsList1 = new ArrayList<>();
		itemsList1.add(new Metadata.Items().setKey("startup-script-url")
				.setValue(String.format("gs://%s/%s-startup.sh", PROJECT_ID, masterInstance)));
		itemsList1.add(new Metadata.Items().setKey("kvPort").setValue(keyValuePort.toString()));
		itemsList1.add(new Metadata.Items().setKey("masterPort").setValue(masterPort.toString()));
		masterMetadata.setItems(itemsList1);

		GcpComputeVm masterVm = new GcpComputeVm(masterInstance, masterMetadata);
		masterIP = masterVm.createVMInstance(masterInstance);
		LOGGER.info("Master instance launched wth IP" + masterIP);

	}

	public static String uploadToGCloud(String filePath) {
		String[] name = filePath.split("/");
		String fileName = name[name.length - 1];
		Storage storage = StorageOptions.getDefaultInstance().getService();
		BlobId blobId = BlobId.of(PROJECT_ID, fileName);
		Blob blob = storage.get(blobId);

		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

		try {
			storage.create(blobInfo, Files.readAllBytes(new File(filePath).toPath()));
		} catch (IOException e) {
			// LOGGER.error("Error reading the file. File path may be wrong, filePath: {}",
			// fileName);
		}
		return "gs://" + PROJECT_ID + "/" + fileName;
	}

	/*
	 * private static KeyValueStoreGrpc.KeyValueStoreBlockingStub
	 * establishKVStoreConnection(String ipAdress, int port) {
	 * 
	 * }
	 */

}
