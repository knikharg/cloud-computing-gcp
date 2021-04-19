package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.grpc.stub.StreamObserver;
import stub.KeyValueStoreGrpc.KeyValueStoreImplBase;
import stub.Keyvalue.APIResponse;
import stub.Keyvalue.Key;
import stub.Keyvalue.KeyValue;

public class KeyValueStoreService extends KeyValueStoreImplBase  {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	private static final int SUCCESS = 200;
	private static final int INVALID = 400;
	private ConcurrentMap<Object, Object> map;
	private String filePath;
	private Integer clientID;
	public KeyValueStoreService(ConcurrentMap<Object, Object> map, Integer clientID) {
		this.map = map;
		this.filePath =  "keyValueStore.txt";
		this.clientID = clientID;
		persistData("READ");
	}
	
	@Override
	public void get(Key request, StreamObserver<KeyValue> responseObserver) {
		String key = request.getKey();
		KeyValue.Builder response = KeyValue.newBuilder();
		if(key!=null && map.get(key)!=null) {
			response.setKey(key);
			response.setValue((String)map.get(key));
			response.setResponseCode(SUCCESS);
		}else {
			LOGGER.error("Invalid key");
			response.setResponseCode(INVALID);
		}
		responseObserver.onNext(response.build());
		responseObserver.onCompleted();
	}

	

	@Override
	public void set(KeyValue request, StreamObserver<APIResponse> responseObserver) {
		LOGGER.info("Inside SET");
		String key = request.getKey();
		String value = request.getValue();
		LOGGER.debug("Key:: Value:: "+  key + value);
		APIResponse.Builder response = APIResponse.newBuilder();
        map.put(key, value);

		/*
		 * try { persistData(key, value); } catch (Exception e) {
		 * LOGGER.error(e.toString());
		 * responseObserver.onNext(response.setResponseCode(400).build());
		 * responseObserver.onCompleted(); }
		 */

        responseObserver.onNext(response.setResponseCode(200).build());
        responseObserver.onCompleted();
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
			if (option.equals("WRITE")) {
				FileOutputStream fileOutput = new FileOutputStream(file);
				ObjectOutputStream out = new ObjectOutputStream(fileOutput);
				out.writeObject(map);
				out.close();
				fileOutput.close();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} 

	}


}
