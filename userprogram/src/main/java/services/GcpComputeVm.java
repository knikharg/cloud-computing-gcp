package services;

import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.ComputeScopes;
import com.google.api.services.compute.model.AccessConfig;
import com.google.api.services.compute.model.AttachedDisk;
import com.google.api.services.compute.model.AttachedDiskInitializeParams;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import com.google.api.services.compute.model.Metadata;
import com.google.api.services.compute.model.NetworkInterface;
import com.google.api.services.compute.model.Operation;
import com.google.api.services.compute.model.ServiceAccount;


import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
 * Code taken from : https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/compute/cmdline/src/main/java/ComputeEngineSample.java
 */
public class GcpComputeVm {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	private static final String APPLICATION_NAME = "MapReduce-GCP";

	  /** Set PROJECT_ID to your Project ID from the Overview pane in the Developers console. */
	  private static final String PROJECT_ID = "kasturi-nikharge";

	  /** Set Compute Engine zone. */
	  private static final String ZONE_NAME = "us-central1-f";

	  /** Set the name of the sample VM instance to be created. */
	  private static final long OPERATION_TIMEOUT_MILLIS = 60 * 1000;
	  //private static final String instanceName = "my-sample-instance";

	  /** Global instance of the HTTP transport. */
	  private static HttpTransport httpTransport;

	  /** Global instance of the JSON factory. */
	  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	  

	  /** Set the name of the sample VM instance to be created. */
	  private  String instanceName;
	  
	  private Metadata metadata;
	  
	  public GcpComputeVm(String instanceName, Metadata data) {
		this.instanceName = instanceName;
		this.metadata = data;
	}


	  public String createVMInstance(String instanceName) throws Exception{
		  Compute compute = authenticateUser();
		  // List out instances, looking for the one created by this sample app.
	      boolean foundOurInstance = printInstances(compute);

	      Operation op;
	      if (foundOurInstance) {
	        op = deleteInstance(compute, instanceName);
	      } else {
	        op = startInstance(compute, instanceName);
	      }

	      // Call Compute Engine API operation and poll for operation completion status
	      System.out.println("Waiting for operation completion...");
	      Operation.Error error = blockUntilComplete(compute, op, OPERATION_TIMEOUT_MILLIS);
	      if (error == null) {
	        LOGGER.info("Instance creation Success! :: ", instanceName );
	      } else {
	    	  LOGGER.error(error.toPrettyString());
	      }
	      return getInstanceIP(compute);
	    }
	 
		  
	  
	  
	public  String getInstanceIP(Compute compute) throws IOException {
	    InstanceList instanceList =    compute.instances().list(PROJECT_ID, ZONE_NAME).execute();
        
	    if (instanceList==null || instanceList.getItems() == null || instanceList.getItems().size() < 1) {
	        LOGGER.info("No instances found. Sign in to the Google Developers Console and create "
	                + "an instance at: https://console.developers.google.com/");
	    }
	    Optional<Instance> optionalInstance = instanceList.getItems().stream()
                .filter(instance -> instance.getName().equalsIgnoreCase(instanceName)).findAny();

        return optionalInstance.map(instance ->
                instance.getNetworkInterfaces().get(0).getAccessConfigs().get(0).getNatIP())
                .orElse(null);
	}
	  
	  public Compute authenticateUser() throws IOException, GeneralSecurityException{
		  // Authenticate using Google Application Default Credentials.
		  httpTransport =  GoogleNetHttpTransport.newTrustedTransport();
	      GoogleCredentials credential = GoogleCredentials.getApplicationDefault();
	      if (credential.createScopedRequired()) {
	        List<String> scopes = new ArrayList<>();
	        // Set Google Cloud Storage scope to Full Control.
	        scopes.add(ComputeScopes.DEVSTORAGE_FULL_CONTROL);
	        // Set Google Compute Engine scope to Read-write.
	        scopes.add(ComputeScopes.COMPUTE);
	        credential = credential.createScoped(scopes);
	      }
	      HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credential);
	      // Create Compute Engine object for listing instances.
	      
	      return new Compute.Builder(httpTransport, JSON_FACTORY, requestInitializer)
	              .setApplicationName(APPLICATION_NAME)
	              .build();
	      
	  }
	  
	 

		  // [START list_instances]
		  /**
		   * Print available machine instances.
		   *
		   * @param compute The main API access point
		   * @return {@code true} if the instance created by this sample app is in the list
		   */
		  public boolean printInstances(Compute compute) throws IOException {
		    System.out.println("================== Listing Compute Engine Instances ==================");
		    Compute.Instances.List instances = compute.instances().list(PROJECT_ID, ZONE_NAME);
		    InstanceList list = instances.execute();
		    boolean found = false;
		    if (list.getItems() == null) {
		      System.out.println(
		          "No instances found. Sign in to the Google Developers Console and create "
		              + "an instance at: https://console.developers.google.com/");
		    } else {
		      for (Instance instance : list.getItems()) {
		        System.out.println(instance.toPrettyString());
		        if (instance.getName().equals(this.instanceName)) {
		          found = true;
		        }
		      }
		    }
		    return found;
		  }
		  // [END list_instances]

		  // [START create_instances]
		  private  Operation startInstance(Compute compute, String instanceName) throws IOException {
		    System.out.println("================== Starting New Instance ==================");

		    // Create VM Instance object with the required properties.
		    Instance instance = new Instance();
		    instance.setName(instanceName);
		    instance.setMachineType(
		        String.format(
		            "https://www.googleapis.com/compute/v1/projects/%s/zones/%s/machineTypes/n1-standard-1",
		            PROJECT_ID, ZONE_NAME));
		    // Add Network Interface to be used by VM Instance.
		  
	        NetworkInterface networkInterface = new NetworkInterface();
	        networkInterface.setKind("compute#networkInterface");
	        String region = ZONE_NAME.split("-")[0] + "-" + ZONE_NAME.split("-")[1];
	        networkInterface.setSubnetwork("projects/" + PROJECT_ID + "/regions/" + region + "/subnetworks/default");
	        List<AccessConfig> accessConfigs = new ArrayList<>();
	        AccessConfig accessConfig = new AccessConfig();
	        accessConfig.setKind("compute#accessConfig");
	        accessConfig.setName("External NAT");
	        accessConfig.setType("ONE_TO_ONE_NAT");
	        accessConfig.setNetworkTier("PREMIUM");
	        accessConfigs.add(accessConfig);
	        networkInterface.setAccessConfigs(accessConfigs);
	        
	        instance.setNetworkInterfaces(Collections.singletonList(networkInterface));
	        //Set Disks
	        List<AttachedDisk> attachedDisks = new ArrayList<>();
	        AttachedDisk attachedDisk = new AttachedDisk();
	        AttachedDiskInitializeParams attachedDiskInitializeParams = new AttachedDiskInitializeParams();
	        attachedDiskInitializeParams.setSourceImage("projects/kasturi-nikharge/global/images/custom-image");
	        attachedDiskInitializeParams.setDiskType("projects/" + PROJECT_ID + "/zones/" + ZONE_NAME + "/diskTypes/pd-standard");
	        attachedDiskInitializeParams.setDiskSizeGb((long) 10);
	        attachedDisk.setInitializeParams(attachedDiskInitializeParams);
	        attachedDisk.setAutoDelete(true);
	        attachedDisk.setBoot(true);
	        attachedDisk.setDeviceName(instanceName);
	        attachedDisk.setKind("compute#attachedDisk");
	        attachedDisk.setMode("READ_WRITE");
	        attachedDisk.setType("PERSISTENT");
	        attachedDisks.add(attachedDisk);
	        instance.setDisks(attachedDisks);

	        
		    // Initialize the service account to be used by the VM Instance and set the API access scopes.
		    ServiceAccount account = new ServiceAccount();
		    account.setEmail("56584533772-compute@developer.gserviceaccount.com");
		    List<String> scopes = new ArrayList<>();
		    scopes.add("https://www.googleapis.com/auth/devstorage.full_control");
		    scopes.add("https://www.googleapis.com/auth/compute");
		    account.setScopes(scopes);
		    instance.setServiceAccounts(Collections.singletonList(account));

		    // Optional - Add a startup script to be used by the VM Instance.
		    Metadata meta = new Metadata();
		    //Metadata.Items item = new Metadata.Items();
		    //item.setKey("startup-script-url");
		    // If you put a script called "vm-startup.sh" in this Google Cloud Storage
		    // bucket, it will execute on VM startup.  This assumes you've created a
		    // bucket named the same as your PROJECT_ID.
		    // For info on creating buckets see:
		    // https://cloud.google.com/storage/docs/cloud-console#_creatingbuckets
		    //item.setValue(String.format("gs://%s/vm-startup.sh", PROJECT_ID));
		    //meta.setItems(Collections.singletonList(item));
		    instance.setMetadata(this.metadata);

		    System.out.println(instance.toPrettyString());
		    Compute.Instances.Insert insert = compute.instances().insert(PROJECT_ID, ZONE_NAME, instance);
		    return insert.execute();
		  }
		  // [END create_instances]

		  private Operation deleteInstance(Compute compute, String instanceName) throws Exception {
		    System.out.println(
		        "================== Deleting Instance " + instanceName + " ==================");
		    Compute.Instances.Delete delete =
		        compute.instances().delete(PROJECT_ID, ZONE_NAME, instanceName);
		    return delete.execute();
		  }

		  private InstanceList listInstances(Compute compute) throws IOException {
		        Compute.Instances.List instances = compute.instances().list(PROJECT_ID, ZONE_NAME);
		        return instances.execute();
		    }

		  
		
		  
		  public int deleteInstance() {
		        try {
		            Compute compute = authenticateUser();
		            boolean foundOurInstance = checkInstance(compute);
		            
		  	      Operation op;
		  	      

		            if (foundOurInstance) {
		                op = deleteInstance(compute, instanceName);
		                System.out.println(
		    			        "================== Deleting Instance " + instanceName + " ==================");
		    			   
		            }
		            return 0;
		        } catch (Throwable e) {
		            LOGGER.error(e.getMessage());
		            return -1;
		        }
		    }
		// [START list_instances]
		    private boolean checkInstance(Compute compute) throws IOException {
		        InstanceList instanceList = listInstances(compute);
		        if (instanceList.getItems() == null || instanceList.getItems().size() < 1) {
		            LOGGER.info("No instances found. Sign in to the Google Developers Console and create "
		                    + "an instance at: https://console.developers.google.com/");
		        }
		        return  instanceList.getItems().stream().anyMatch((instance -> instance.getName().equals(instanceName)));
		    }
		    // [END list_instances]
		  // [START wait_until_complete]
		  /**
		   * Wait until {@code operation} is completed.
		   *
		   * @param compute the {@code Compute} object
		   * @param operation the operation returned by the original request
		   * @param timeout the timeout, in millis
		   * @return the error, if any, else {@code null} if there was no error
		   * @throws InterruptedException if we timed out waiting for the operation to complete
		   * @throws IOException if we had trouble connecting
		   */
		  public static Operation.Error blockUntilComplete(
		      Compute compute, Operation operation, long timeout) throws Exception {
		    long start = System.currentTimeMillis();
		    final long pollInterval = 5 * 1000;
		    String zone = operation.getZone(); // null for global/regional operations
		    if (zone != null) {
		      String[] bits = zone.split("/");
		      zone = bits[bits.length - 1];
		    }
		    String status = operation.getStatus();
		    String opId = operation.getName();
		   
		    while (operation != null && !status.equals("DONE")) {
		      Thread.sleep(pollInterval);
		      long elapsed = System.currentTimeMillis() - start;
		      if (elapsed >= timeout) {
		        throw new InterruptedException("Timed out waiting for operation to complete");
		      }
		      System.out.println("waiting...");
		      if (zone != null) {
		        Compute.ZoneOperations.Get get = compute.zoneOperations().get(PROJECT_ID, zone, opId);
		        operation = get.execute();
		      } else {
		        Compute.GlobalOperations.Get get = compute.globalOperations().get(PROJECT_ID, opId);
		        operation = get.execute();
		      }
		      if (operation != null) {
		        status = operation.getStatus();
		      }
		    }
		    return operation == null ? null : operation.getError();
		  }
		  // [END wait_until_complete]
		}