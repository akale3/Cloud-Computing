package edu.sqs;

import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class SQSImplementation {

	private AmazonSQS sqs = null;

	public SQSImplementation() {
		AWSCredentials credentials = null;
		ProfilesConfigFile configFile = new ProfilesConfigFile("./resources/credentials");
		try {
			credentials = new ProfileCredentialsProvider(configFile, "default").getCredentials();
			this.sqs = new AmazonSQSClient(credentials);
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			this.sqs.setRegion(usWest2);
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/home/aditya/.aws/credentials), and is in valid format.", e);
		}
	}

	/**
	 * @param queueName
	 * 
	 *            It deletes that particular queue.
	 */
	public void deleteQueue(String queueName) {
		String queueURL = getQueueURL(queueName);
		this.sqs.deleteQueue(new DeleteQueueRequest(queueURL));
	}

	/**
	 * @param queueName
	 *            It fetches an entry from a Queue and retruns the message list.
	 */
	public List<Message> retrrieveElementFromQueue(String queueName) {
		String queueURL = getQueueURL(queueName);
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueURL);
		List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();

		return messages;
	}

	/**
	 * @param queueName
	 * @param message
	 *            This method insert an entry in a Queue
	 */
	public void insertElementInQueue(String queueName, String message) {
		String queueURL = getQueueURL(queueName);
		this.sqs.sendMessage(new SendMessageRequest(queueURL, message));
	}

	/**
	 * @param queueName
	 *            This method cretaes a queue with specified name.
	 */
	public void createQueue(String queueName) {
		String queueURL = null;
		try {
			queueURL = getQueueURL(queueName);
		} catch (Exception e) {
			System.out.println();
		}
		if (null == queueURL) {
			CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
			sqs.createQueue(createQueueRequest);
		}
	}

	/**
	 * @param queueName
	 * @return URL of a queue having name = queueName
	 */
	public String getQueueURL(String queueName) {
		GetQueueUrlRequest request = new GetQueueUrlRequest(queueName);
		GetQueueUrlResult queueResult = this.sqs.getQueueUrl(request);
		return queueResult.getQueueUrl();
	}

	public AmazonSQS getSqs() {
		return sqs;
	}
}
