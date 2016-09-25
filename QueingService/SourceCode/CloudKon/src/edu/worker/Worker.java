package edu.worker;

import edu.dynamodb.DynamoDbImpl;
import edu.sqs.SQSImplementation;

public class Worker {

	public static void main(String[] args) {

		SQSImplementation sqsImplementation = new SQSImplementation();
		String taskQueue = args[1];
		String responseQueue = args[2];
		String tableName = "MyTable";

		// Create table
		DynamoDbImpl dynamoDbImpl = new DynamoDbImpl();
		dynamoDbImpl.createTable(tableName);
		
		// create queues
		sqsImplementation.createQueue(taskQueue);
		sqsImplementation.createQueue(responseQueue);
		
		// Multi threaded worker
		int noOfThreads = Integer.parseInt(args[4]);
		for (int i = 0; i < noOfThreads; i++) {
			Thread thread = new Thread(new WorkerImpl(sqsImplementation, taskQueue, responseQueue, dynamoDbImpl, tableName));
			thread.start();
		}
		
		
	}
}
