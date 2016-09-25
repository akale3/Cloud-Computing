package edu.executor;

public class CheckResponseQueue implements Runnable {

	private SchedulerImpl schedulerImpl = null;

	public CheckResponseQueue(SchedulerImpl schedulerImpl) {
		this.schedulerImpl = schedulerImpl;
	}

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
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
		System.out.println("****** Total time for executing all task = " + (endTime - (startTime-1000)) + " milliseconds");

	}

}
