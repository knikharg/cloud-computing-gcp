package mapreduce;

public class TaskStatus {
	
	private Boolean isCompleted;
	private String taskStatus;
	private String instanceID;
	public String getInstanceID() {
		return instanceID;
	}


	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}


	public TaskStatus( Boolean isCompleted, String taskStatus, String instanceID) {
		
		this.isCompleted = isCompleted;
		this.taskStatus = taskStatus;
		this.instanceID = instanceID;
	}
	
	
	public String getTaskStatus() {
		return taskStatus;
	}


	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}


	public Boolean getIsCompleted() {
		return isCompleted;
	}
	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

}
