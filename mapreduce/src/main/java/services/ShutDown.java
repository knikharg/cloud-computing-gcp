package services;

import java.io.File;

import io.grpc.stub.StreamObserver;
import stub.Master.APIResponse;
import stub.Master.Config;
import stub.ShutDownGrpc.ShutDownImplBase;

public class ShutDown extends ShutDownImplBase {
	private static Integer mappers;
	private static Integer reducers;
	@Override
	public void shutDown(Config request, StreamObserver<APIResponse> responseObserver) {
		
		File f = new File(System.getProperty("user.dir")); 
		String path =f.getParent(); 
		 mappers = request.getMappers();
		 reducers = request.getReducers();
		 
		/*
		 * try { //Files.delete(path+""); } catch(IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace();
		 * 
		 * }
		 */
				 
			}


}
