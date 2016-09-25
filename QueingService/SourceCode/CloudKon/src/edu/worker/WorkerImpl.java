package edu.worker;

import java.util.List;

import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;

import edu.dynamodb.DynamoDbImpl;
import edu.sqs.SQSImplementation;

public class WorkerImpl implements Runnable {

	private SQSImplementation sqsImplementation = null;
	private String taskQueue = null;
	private String responseQueue = null;
	private DynamoDbImpl dbImpl = null;
	private String tableName = null;

	public WorkerImpl(SQSImplementation sqsImplementation, String queueName, String responseQueue, DynamoDbImpl dbImpl, String tableName) {
		this.sqsImplementation = sqsImplementation;
		this.taskQueue = queueName;
		this.responseQueue = responseQueue;
		this.dbImpl = dbImpl;
		this.tableName = tableName;
	}

	@Override
	public void run() {
		while (true) {
			// take element from request queue
			List<Message> messages = this.sqsImplementation.retrrieveElementFromQueue(this.taskQueue);
			if (null != messages && messages.size() > 0) {
				for (Message message : messages) {
					String[] messageParts = message.getBody().split(" ");
					// check for entry in a dynamoDB table
					boolean isEntryExist = this.dbImpl.checkIfEntryExist(this.tableName, messageParts[0]);
					if (!isEntryExist) {
						// insert task in a dynamoDB table
						this.dbImpl.insertElementInTable(tableName, messageParts[0]);
						try {
							Thread.sleep(Integer.parseInt(messageParts[1]));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						// insert element in response queue
						this.sqsImplementation.insertElementInQueue(responseQueue, messageParts[0] + " 0");
						// Delete a message from a request queue
						String messageReceiptHandle = message.getReceiptHandle();
						this.sqsImplementation.getSqs().deleteMessage(new DeleteMessageRequest(
								sqsImplementation.getQueueURL(this.taskQueue), messageReceiptHandle));
					}else{
						System.out.println("Duplicate Entry Found" + messageParts[0]);
					}
				}

			} 
		}
	}

}
