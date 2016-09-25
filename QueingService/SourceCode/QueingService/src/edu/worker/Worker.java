package edu.worker;

import edu.executor.SchedulerImpl;
import edu.task.Task;

public class Worker implements Runnable {

	SchedulerImpl schedulerImpl = null;

	public Worker(SchedulerImpl schedulerImpl) {

		this.schedulerImpl = schedulerImpl;
	}

	@Override
	public void run() {
		// it continuously check for the request queue unless the queue is empty
		while (true) {
			if (null != schedulerImpl.getTaskQueue() && (!schedulerImpl.getTaskQueue().isEmpty())) {
				// removing element from a request queue
				Task task = schedulerImpl.getTaskQueue().remove();
				if (null != task && !schedulerImpl.getTaskMapper().contains(task.getTaskId())) {
					schedulerImpl.getTaskMapper().put(task.getTaskId(), true);
					try {
						Thread.sleep(Integer.parseInt(task.getExecutionTime()));
						task.setExecuted(true);
						int taskCount = schedulerImpl.getTaskCount();
						schedulerImpl.setTaskCount(--taskCount);
						// adding task to a response queue
						schedulerImpl.getResponseQueue().add(task);
					} catch (NumberFormatException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				break;
			}
		}

	}

}
