package mapreduce;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
/**
 * 
 * @author kasturi
 * Code taken from https://cloud.google.com/endpoints/docs/grpc/grpc-service-config
 */
public class MasterHelper    {
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
	private static final String PROJECT_ID = "kasturi-nikharge";
	public static String uploadToGCloud(String filePath) {
		String[] name = filePath.split("/");
		String fileName = name[name.length-1];
        Storage storage =  StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of(PROJECT_ID, fileName);
        Blob blob = storage.get(blobId);
        
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();

        try {
            storage.create(blobInfo, Files.readAllBytes(new File(fileName).toPath()));
        } catch (IOException e) {
            LOGGER.error("Error reading the file. File path may be wrong, filePath: {}", fileName);
        }
        return "gs://"+PROJECT_ID+ "/"+fileName;
	}
	
	public static String downloadFROMGCloud(String filePath) {
		
		String[] name = filePath.split("/");
		String fileName = name[name.length-1];
		Path destFilePath = Paths.get(System.getProperty("user.dir")+"/"+fileName);
        Storage storage = StorageOptions.getDefaultInstance().getService();

        Blob blob = storage.get(BlobId.of(PROJECT_ID ,fileName));
        blob.downloadTo(destFilePath);

        
        return destFilePath.toString();
	}
	
	/*
	 * public static void main(String[] args) {
	 * System.out.println(downLoadFROMGCloud(
	 * "gs://kasturi-nikharge/prideandprejudice.txt"));
	 * 
	 * }
	 */
}
