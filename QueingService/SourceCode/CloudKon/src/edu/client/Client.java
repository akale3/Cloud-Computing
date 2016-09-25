package edu.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;

import edu.dynamodb.DynamoDbImpl;
import edu.sqs.SQSImplementation;

public class Client {

	private static int totalTaskAdded;

	public static void main(String[] args) {

		SQSImplementation sqsImplementation = new SQSImplementation();
		DynamoDbImpl dynamoDbImpl = new DynamoDbImpl();

		String taskQueue = args[1];
		String responseQueueName = args[2];
		String tableName = "MyTable";

		long startTime = System.currentTimeMillis();
		String workLoadFile = args[4];
		File file = new File(workLoadFile);
		// add task to a request queue.
		addTaskToQueue(file, sqsImplementation, taskQueue);

		// continuously polls for the responses
		int count = 0;
		Set<String> taskSet = new HashSet<String>();
		while (true) {
			List<Message> messages = sqsImplementation.retrrieveElementFromQueue(responseQueueName);
			if (null != messages && messages.size() > 0) {
				for (Message message : messages) {
					String parts[] = message.getBody().split(" ");
					if (!taskSet.contains(parts[0])) {
						taskSet.add(parts[0]);
						count++;
						String messageReceiptHandle = message.getReceiptHandle();
						sqsImplementation.getSqs().deleteMessage(new DeleteMessageRequest(
								sqsImplementation.getQueueURL(responseQueueName), messageReceiptHandle));
					}
				}
			}
			if (totalTaskAdded == count) {
				break;
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Total time for executing tasks: " + (endTime - startTime));

		// deleting all created queues and table
		/*sqsImplementation.deleteQueue("MyQueue");
		sqsImplementation.deleteQueue("responseQueue");
		dynamoDbImpl.deleteTable(tableName);*/
	}

	/**
	 * @param file
	 * @param sqsImplementation
	 * @param taskQueue
	 *            This function adds a task to a request queue
	 */
	public static void addTaskToQueue(File file, SQSImplementation sqsImplementation, String taskQueue) {
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String line = bufferedReader.readLine();
			while (null != line && !line.isEmpty() && !"".equals(line)) {
				totalTaskAdded++;
				sqsImplementation.insertElementInQueue(taskQueue, line);
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Total task added by Client = " + totalTaskAdded);
	}
}
