package edu.client;

import java.io.File;

import edu.executor.SchedulerImpl;

public class Client implements Runnable {

	private SchedulerImpl schedulerImpl = null;
	private String workloadFilePath = null;

	public Client(SchedulerImpl schedulerImpl, String workloadFilePath) {
		this.schedulerImpl = schedulerImpl;
		this.workloadFilePath = workloadFilePath;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * Its submit task to a request queue
	 */
	@Override
	public void run() {
		File file = new File(workloadFilePath);
		schedulerImpl.addTaskToQueue(file);
	}
}
