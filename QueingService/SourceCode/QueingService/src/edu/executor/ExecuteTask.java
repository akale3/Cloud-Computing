package edu.executor;

import edu.client.Client;
import edu.worker.Worker;

public class ExecuteTask {

	public static void main(String[] args) {
		SchedulerImpl schedulerImpl = SchedulerImpl.getSchedulerImplInstance();
		schedulerImpl.initialize();

		int noOfWorkerThread = Integer.parseInt(args[3]);
		String workloadFilePath = args[5];

		// Run client
		Thread clientThread = new Thread(new Client(schedulerImpl, workloadFilePath));
		System.out.println("Started Adding Jobs...");
		clientThread.start();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long startTime = System.currentTimeMillis();
		// Run Worker
		for (int i = 0; i < noOfWorkerThread; i++) {
			Thread workerThread = new Thread(new Worker(schedulerImpl));
			workerThread.start();
		}
		System.out.println("Workers Started...");

		// Checking for all task execution
		while (true) {
			int count = schedulerImpl.getTaskCount();
			System.out.print("");
			if (count != 0) {
				continue;
			} else {
				break;
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println(
				"****** Total time for executing all task = " + (endTime - (startTime)) + " milliseconds******");
	}

}
