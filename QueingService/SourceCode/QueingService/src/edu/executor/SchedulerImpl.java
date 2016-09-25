package edu.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.task.Task;

public class SchedulerImpl {

	private Queue<Task> taskQueue = null;
	private Queue<Task> responseQueue = null;
	private ConcurrentHashMap<String, Boolean> taskMapper = null;
	private int taskCount = 0;
	private int totalTaskAdded = 0;
	private static SchedulerImpl schedulerImpl = null;

	private SchedulerImpl() {

	}

	/**
	 * This Method initialize all queue and Map
	 */
	public void initialize() {
		taskQueue = new ConcurrentLinkedQueue<Task>();
		responseQueue = new ConcurrentLinkedQueue<Task>();
		taskMapper = new ConcurrentHashMap<String, Boolean>();
	}

	
	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public Queue<Task> getTaskQueue() {
		return taskQueue;
	}

	public Queue<Task> getResponseQueue() {
		return responseQueue;
	}

	public ConcurrentHashMap<String, Boolean> getTaskMapper() {
		return taskMapper;
	}

	/**
	 * @param file
	 * This method reads the file and insert it into a request queue
	 */
	public void addTaskToQueue(File file) {

		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = bufferedReader.readLine();
			while (null != line && !line.isEmpty()) {
				String[] taskDetails = line.split(" ");
				Task task = new Task();
				task.setTaskId(taskDetails[0]);
				task.setExecutionTime(taskDetails[1]);
				task.setExecuted(false);
				taskCount++;
				totalTaskAdded++;
				getTaskQueue().add(task);
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Total task added by Client = " + totalTaskAdded);
	}
	
	public static SchedulerImpl getSchedulerImplInstance() {
		if (null == schedulerImpl) {
			schedulerImpl = new SchedulerImpl();
		}
		return schedulerImpl;
	}

}
