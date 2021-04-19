package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.media.MediaHttpDownloader.DownloadState;
import com.google.api.client.googleapis.media.MediaHttpUploader.UploadState;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Metadata;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import mapreduce.MasterHelper;
import mapreduce.TaskStatus;
import stub.KeyValueStoreGrpc;
import stub.Keyvalue;
import stub.Keyvalue.KeyValue;
import stub.Master;
import stub.Master.APIResponse;
import stub.Master.Config;
import stub.StartMapReduceGrpc.StartMapReduceImplBase;

public class StartMapReduce extends StartMapReduceImplBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	static ConcurrentMap<Object, Object> mapperInfo = new ConcurrentHashMap<>();
	static ConcurrentMap<Object, Object> reducerInfo = new ConcurrentHashMap<>();
	static ConcurrentMap<Object, TaskStatus> statusMap = new ConcurrentHashMap<Object, TaskStatus>();
	private static final String MAPPER = "m"; 
	private static final String REDUCER = "r"; 
	private static final String PROJECT_ID = "kasturi-nikharge";
    private static ExecutorService executor;
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
	public StartMapReduce(ConcurrentMap<Object, Object> mapperInfo, ConcurrentMap<Object, Object> reducerInfo) {
		this.mapperInfo = mapperInfo;
		this.reducerInfo = reducerInfo;
		//int coreCnt = Runtime.getRuntime().availableProcessors();
		executor= Executors.newFixedThreadPool(100);
	}

	@Override
	public void runMapred(Config request, StreamObserver<APIResponse> responseObserver) {
		 keyValueIp = request.getKeyValueIp();
		 masterIP = request.getMasterIP();
		 keyValuePort = request.getKeyValuePort();
		 masterPort = request.getMasterPort();
		 mappers = request.getMappers();
		 reducers = request.getReducers();
		 input = request.getInput();
		 output = request.getOutput();
		 mapperjar = request.getMapperjar();
		 reducerjar = request.getReducerjar();
		// String[] inputFileList =
		 
		 LOGGER.info("Inside runMapred :: Master");
		 
		 
		KeyValueStoreGrpc.KeyValueStoreBlockingStub keyValStore = establishKVStoreConnection(keyValueIp, keyValuePort);
		List<String> inputFileList = Arrays.asList(input.split(","));
		try {
			try {
				splitData(inputFileList, keyValStore ,mappers, MAPPER);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * KeyValue keyValue =
			 * keyValStore.get(Keyvalue.Key.newBuilder().setKey(output).build()); String
			 * outputString = keyValue.getValue(); int response =
			 * keyValue.getResponseCode(); if(response!=200) {
			 * LOGGER.error("Error fetching output from key value store"); } File f = new
			 * File(System.getProperty("user.dir")); String path =f.getAbsolutePath(); try {
			 * BufferedWriter out = new BufferedWriter(new FileWriter(path + "/" +
			 * output+".txt")); out.append(outputString); out.close();
			 */
	      
	        	
				MasterHelper.uploadToGCloud("worker-startup.sh");			
				spawnTasks(MAPPER, keyValStore);
				markAllTasks(keyValStore,MAPPER);
				//blockUntilTaskOver(MAPPER,keyValStore);
				deleteInstances();
				
				LOGGER.info("Assigning reducer tasks");
				assignTasks(MAPPER,keyValStore);
				spawnTasks(REDUCER, keyValStore); 
				LOGGER.info("Checkin reducer task completion");
				markAllTasks(keyValStore,REDUCER); 
				//blockUntilTaskOver(REDUCER,keyValStore);

				deleteInstances();
				
				LOGGER.info("Returning to user program");
				responseObserver.onNext(Master.APIResponse.newBuilder().setResponseCode(200).build());
		        responseObserver.onCompleted();
				
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				responseObserver.onNext(Master.APIResponse.newBuilder().setResponseCode(400).build());
		        responseObserver.onCompleted();
			}
	        
	        
	
		
	}

	private void splitData(List<String> input, KeyValueStoreGrpc.KeyValueStoreBlockingStub keyValStore, int nodes,String function)
			throws IOException {
		// count lines in data and split equally amongst nodes
		int cnt = 0;
		int fileID = 0;
		List<String> keyList = new ArrayList<>();
		File f = new File(System.getProperty("user.dir"));
		String path = f.getParent();
		BufferedWriter writer = new BufferedWriter(new FileWriter(path+"/"+"combined.txt"));
		for (String file : input) {
			FileReader fileIn;
			BufferedReader in;
			try {
				String currLine;
				String localFile = MasterHelper.downloadFROMGCloud(file);
				fileIn = new FileReader(localFile);
				in= new BufferedReader(fileIn);
				
				while ((currLine = in.readLine()) != null) {
					String[] words =currLine.split("\\s");
					cnt+= words.length;
					for(String word :words) {
						if(word.replaceAll("[\\p{Punct}&&[^0-9]&&[^-]]", "")!="" && !word.replaceAll("[\\p{Punct}&&[^0-9]&&[^-]]", "").isEmpty()) {
							String currWord = word.replaceAll("[\\p{Punct}&&[^0-9]&&[^-]]", "").trim().toLowerCase();
							writer.append(currWord+" "+file +"\r\n");
						}
						
					}
					
				}
				fileIn.close();
				in.close();
			} catch (FileNotFoundException e) {

				LOGGER.error("Error reading opening input file ", e);
			}
			
			
		}
		//String localFile = MasterHelper.uploadToGCloud(path+"/"+"combined.txt");
		writer.close();
		
		LOGGER.info("Total input keys :: " + cnt);
			int tasks = cnt++;
			int taskSize;
		 	if (tasks % nodes == 0) {
		 		taskSize = tasks / nodes;
		    } else {
		    	taskSize = (tasks+ nodes) / nodes;
		    }
		 	LOGGER.info("taskSize :: " + taskSize);
	        int count = 0;
	        int nodeId=0;
	        
	       
	        BufferedReader in;
				try {
					String currLine;
					in = new BufferedReader(new FileReader(path+"/"+"combined.txt"));
					
					while(nodeId<mappers) {
						BufferedWriter write = new BufferedWriter(new FileWriter(path+"/"+function+""+nodeId+".txt"));
						StringBuffer file = new StringBuffer();
						count = 0;
					
							
							while ((currLine = in.readLine()) != null && count<taskSize) {
								file.append(currLine +"\r\n");
								count++;
							}
						
						KeyValue setRequest =KeyValue.newBuilder().setKey(function+""+nodeId).setValue(file.toString()).build();
						stub.Keyvalue.APIResponse response = keyValStore.set(setRequest);
						 if (response.getResponseCode() != 200) {
							  LOGGER.error("Error saving to keyValueStore "); 
						 }
						 statusMap.put(function+""+nodeId, new TaskStatus(false, "NOT_STARTED", "m"+nodeId));
						nodeId++;
						write.append(file.toString());
						write.close();
					}
					in.close();
				} catch (FileNotFoundException e) {

					LOGGER.error("Error reading opening input file ", e);
				}
			
				
	}

	  private void blockUntilTaskOver(String function, KeyValueStoreGrpc.KeyValueStoreBlockingStub keyValStore) throws InterruptedException, ExecutionException {
	        boolean isRunning = statusMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsCompleted());
	        LOGGER.info("Waiting for mapper tasks");
	        while (!isRunning) {
	       

	            try {
	                executor.submit(() -> {
	                    boolean bool = true;
	                    while (bool) {
	                        bool = !statusMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsCompleted());
	                        try {
	                            Thread.sleep(1000);
	                        } catch (InterruptedException ignored) {}
	                    }
	                }).get( 1000*30, TimeUnit.MILLISECONDS);
	            } catch (TimeoutException ignored) {}
	            finally {
	                isRunning = statusMap.entrySet().stream().allMatch(entry -> entry.getValue().getIsCompleted());
	            }
	        }
	    }
	
	private void spawnTasks(String function, KeyValueStoreGrpc.KeyValueStoreBlockingStub  keyValStore) {
		if(function.equals(MAPPER)) {
			
			HashMap<Object, TaskStatus> mappertasks = (HashMap<Object, TaskStatus>) statusMap.entrySet().stream()
					.filter(e-> e.getValue().getTaskStatus().equals("NOT_STARTED")).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
					if(mappertasks.size()!=0) {
						mappertasks.forEach(
								(k,v)->{
									
									LOGGER.info("Trigger mapper creation " + k.toString());
									Metadata data = setMetadata(function, k.toString(), keyValStore);
									Future<Integer> futureInt = (Future<Integer>) (executor.submit(() ->
									{
					                GcpComputeVm keyValueVm  = new GcpComputeVm(v.getInstanceID(), data);
					        		 try {
										keyValueVm.createVMInstance(v.getInstanceID());
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
											
					        		
									}));
									
									
									/*
									 * if(statusMap.get(k).getIsCompleted()) { assigntasksToReducers(k,
									 * reducerInfo,statusMap); }
									 */
									 
									 
									 
									 
								});
					}
			
		}else {
			HashMap<Object, TaskStatus> reducerTasks = (HashMap<Object, TaskStatus>) statusMap.entrySet().stream()
					.filter(e-> e.getValue().getTaskStatus().equals("NOT_STARTED")).collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
					if(reducerTasks.size()!=0) {
						reducerTasks.forEach(
								(k,v)->{
									
									Metadata data = setMetadata(function, k.toString(), keyValStore);
									LOGGER.info("Trigger mapper creation " + k.toString());
									Future<Integer> futureInt = (Future<Integer>) (executor.submit(() ->
									{
						                GcpComputeVm keyValueVm  = new GcpComputeVm(v.getInstanceID(), data);
						        		 try {
											keyValueVm.createVMInstance(v.getInstanceID());
										} catch (Exception e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
												
									}));
									
									
									
								});
					}
		}
			
		
		
	}
	private void assignTasks(String function,KeyValueStoreGrpc.KeyValueStoreBlockingStub  keyValStore) {
		int count =0;
		while(count<mappers) {
			assignTasksToReducers(function+""+count+"_im",keyValStore); 
			count++;
		}
		writeToReducerIntermediate(keyValStore);
	}
	
	/**
	 * hashing logic for each mapper
	 * @param mapperId
	 */
	private void assignTasksToReducers(String mapperId,KeyValueStoreGrpc.KeyValueStoreBlockingStub  keyValStore) {
		
		KeyValue keyValuePair = keyValStore.get(Keyvalue.Key.newBuilder().setKey(mapperId).build());
		String input = keyValuePair.getValue();
		if(input==null || input =="") {
			LOGGER.debug("key not found" + mapperId);
		}
		/*
		 * File f = new File(System.getProperty("user.dir")); String path =
		 * f.getAbsolutePath(); try { BufferedWriter out = new BufferedWriter(new
		 * FileWriter(path + "/" + mapperId+".txt")); out.append(input); out.close(); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
		/*
		 * String currLine; try (BufferedReader reader = new BufferedReader(new
		 * FileReader(path+"/"+mapperId+".txt"))) {
		 * 
		 * //Get Stream with lines from BufferedReader while((currLine=
		 * reader.readLine())!=null) { mapToNode(currLine); }
		 * 
		 * 
		 * 
		 * } catch (IOException ioException) { ioException.printStackTrace(); }
		 * LOGGER.info("Reducer tasks assigned "); reader.close();
		 */
		String lines[] = input.split("\\r?\\n");
		for(String line: lines) {
			mapToNode(line); 
		}
		LOGGER.info("Reducer tasks assigned ");
		
	}
	
	private void mapToNode(String line) {
		String[] lineList = line.split(" ");
		int sum=0;
		 for(char c :lineList[0].toCharArray())
		    {
		      int asciiValue = c;
		      sum = sum+ asciiValue;
		    }	
		String reducerId =REDUCER +(sum%reducers);
		reducerInfo.putIfAbsent(reducerId, new StringBuilder());
		StringBuilder s = (StringBuilder) reducerInfo.get(reducerId);
		s.append(line.toLowerCase()+"\r\n");
		reducerInfo.put(reducerId, s);
		 statusMap.put(reducerId, new TaskStatus(false, "NOT_STARTED", reducerId));
	}
	private static void writeToReducerIntermediate(KeyValueStoreGrpc.KeyValueStoreBlockingStub  keyValStore) {
		try {
			File f = new File(System.getProperty("user.dir"));
			String path = f.getAbsolutePath();
		
			reducerInfo.forEach((key, value) -> {
				try {
					LOGGER.info("Reducer file written  :: "+  path+"/"+key+".txt" );
				BufferedWriter writer = new BufferedWriter(new FileWriter(path+"/"+key+".txt"));
				
					writer.append(value.toString());
					writer.close();
					KeyValue setRequest =KeyValue.newBuilder().setKey(key.toString()).setValue(value.toString()).build();
					stub.Keyvalue.APIResponse response = keyValStore.set(setRequest);
					 if (response.getResponseCode() != 200) {
						  LOGGER.error("Error saving to keyValueStore "); 
					 }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			});
			
			
		} catch (Exception e) {
			LOGGER.error("Error reading opening input file ", e);
		}
		
	}
	
	public static Metadata setMetadata(String function, String ID, KeyValueStoreGrpc.KeyValueStoreBlockingStub  keyValStore) {
		Metadata metadata = new Metadata();
		if (function.equals(MAPPER)) {
			
			List<Metadata.Items> itemsList = new ArrayList<>();
			itemsList.add(new Metadata.Items().setKey("startup-script-url")
					.setValue(String.format("gs://%s/%s-startup.sh", PROJECT_ID, "worker")));
			itemsList.add(new Metadata.Items().setKey("jar").setValue(mapperjar));
			itemsList.add(new Metadata.Items().setKey("id").setValue(ID));
			itemsList.add(new Metadata.Items().setKey("output").setValue(ID+"_im"));
			itemsList.add(new Metadata.Items().setKey("kvIp").setValue(keyValueIp));
			itemsList.add(new Metadata.Items().setKey("kvPort").setValue(keyValuePort.toString()));
			itemsList.add(new Metadata.Items().setKey("workerjar").setValue("Worker-0.0.1-SNAPSHOT-jar-with-dependencies.jar"));
			metadata.setItems(itemsList);

		} else {

			List<Metadata.Items> itemsList = new ArrayList<>();
			itemsList.add(new Metadata.Items().setKey("startup-script-url")
					.setValue(String.format("gs://%s/%s-startup.sh", PROJECT_ID, "worker")));
			itemsList.add(new Metadata.Items().setKey("jar").setValue(reducerjar));
			itemsList.add(new Metadata.Items().setKey("id").setValue(ID));
			itemsList.add(new Metadata.Items().setKey("output").setValue(output));
			itemsList.add(new Metadata.Items().setKey("kvIp").setValue(keyValueIp));
			itemsList.add(new Metadata.Items().setKey("kvPort").setValue(keyValuePort.toString()));
			itemsList.add(new Metadata.Items().setKey("workerjar").setValue("Worker-0.0.1-SNAPSHOT-jar-with-dependencies.jar"));
			
			metadata.setItems(itemsList);

		}
		return metadata;
	}
	
	public void markAllTasks(KeyValueStoreGrpc.KeyValueStoreBlockingStub keyValStore, String function)
			throws InterruptedException, ExecutionException {
		if (function.equals(MAPPER)) {
			for (int i = 0; i < mappers; i++) {
				checkForTaskCompletion(keyValStore, function + "" + i);
			}
		} else {
			for (int i = 0; i < reducers; i++) {
				checkForTaskCompletion(keyValStore, function + "" + i);
			}
		}
	}
	
	public static void checkForTaskCompletion(KeyValueStoreGrpc.KeyValueStoreBlockingStub  keyValStore, String key) throws InterruptedException, ExecutionException {
		
		String input ="";
		KeyValue keyValue;
		if(key.startsWith(MAPPER)) {
			 keyValue = keyValStore.get(Keyvalue.Key.newBuilder().setKey(key+"_im").build());
			 input = keyValue.getValue();
			 
			 int response = keyValue.getResponseCode();
				LOGGER.info("Key response for task ID  :: " + key + " ::"+ response);
		        LOGGER.info("Waiting for task tasks :: " + key);
		        while (response!=200) {
		       

		            try {
		                executor.submit(() -> {
		                    int responseCode = 400;
		                    while (responseCode==200) {
		                    	KeyValue keyValue1= keyValStore.get(Keyvalue.Key.newBuilder().setKey(key+"_im").build());
		                	
		                    	responseCode= keyValue1.getResponseCode();
		                        try {
		                            Thread.sleep(10 * 1000);
		                        } catch (InterruptedException ignored) {}
		                    }
		                }).get(1000*3, TimeUnit.MILLISECONDS);
		            } catch (TimeoutException ignored) {}
		            finally {
		            	keyValue = keyValStore.get(Keyvalue.Key.newBuilder().setKey(key+"_im").build());
		            	response = keyValue.getResponseCode();
		            }
		        }
				statusMap.get(key).setTaskStatus("COMPLETED");
				statusMap.get(key).setIsCompleted(true);
				LOGGER.info("Completed mapper tasks ::  " + key);
			 
		}else {
			 keyValue = keyValStore.get(Keyvalue.Key.newBuilder().setKey(key+"_status").build());
			 input = keyValue.getValue();

			 LOGGER.info("Task status :: "+ input);
			 
			 int response = keyValue.getResponseCode();
			
		        while (response!=200) {
		       

		            try {
		                executor.submit(() -> {
		                    int responseCode = 400;
		                    while (responseCode!=200) {
		                    	KeyValue keyValue1= keyValStore.get(Keyvalue.Key.newBuilder().setKey(key+"_status").build());
		                	
		                    	responseCode= keyValue1.getResponseCode();
		                        try {
		                            Thread.sleep(10 * 1000);
		                        } catch (InterruptedException ignored) {}
		                    }
		                }).get(1000*3, TimeUnit.MILLISECONDS);
		            } catch (TimeoutException ignored) {}
		            finally {
		            	keyValue = keyValStore.get(Keyvalue.Key.newBuilder().setKey(key+"_status").build());
		            	response = keyValue.getResponseCode();
		            	 input = keyValue.getValue();
		            }
		        }
				statusMap.get(key).setTaskStatus("COMPLETED");
				statusMap.get(key).setIsCompleted(true);
				LOGGER.info("Completed task tasks ::  " + key);
		}
		
		
	}
	
	private static KeyValueStoreGrpc.KeyValueStoreBlockingStub establishKVStoreConnection(String ipAdress, int port) {
		ManagedChannel channel = ManagedChannelBuilder.forAddress(ipAdress, port).usePlaintext().build();
		return KeyValueStoreGrpc.newBlockingStub(channel);
	}
	
	public static void deleteInstances() throws ExecutionException, InterruptedException {
	        List<Future<Integer>> listFutures = new ArrayList<>();
	        for (Entry<Object, TaskStatus> entry : statusMap.entrySet()) {
	            String key = (String) entry.getKey();
	            listFutures.add((Future<Integer>) executor.submit(() -> new  GcpComputeVm(key,null).deleteInstance()));
	        

	        for (Future<Integer> future : listFutures) {
	            Integer status = future.get();
	            if (status != 0) {
	                LOGGER.error("Error cleaning instance clean up some of the instances");
	            }
	        }
	        statusMap.clear();
	    }
	}
}
