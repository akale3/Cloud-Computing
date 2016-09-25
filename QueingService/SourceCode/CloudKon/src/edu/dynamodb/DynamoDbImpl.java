package edu.dynamodb;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;

public class DynamoDbImpl {

	private AmazonDynamoDBClient dynamoDB = null;

	public DynamoDbImpl() {
		AWSCredentials credentials = null;
		ProfilesConfigFile configFile = new ProfilesConfigFile("./resources/credentials");
		try {
			credentials = new ProfileCredentialsProvider(configFile, "default").getCredentials();
			this.dynamoDB = new AmazonDynamoDBClient(credentials);
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			this.dynamoDB.setRegion(usWest2);
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (/home/aditya/.aws/credentials), and is in valid format.", e);
		}
	}

	/**
	 * @param tableName
	 *            it creates a table in a dynamoDB with given tableName. If
	 *            table already exist it just return without doing anything.
	 */
	public void createTable(String tableName) {
		try {

			// Create table if it does not exist yet
			if (Tables.doesTableExist(dynamoDB, tableName)) {
				System.out.println("Table " + tableName + " is already ACTIVE");
			} else {
				// Create a table with a primary hash key named 'name', which
				// holds a string
				CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
						.withKeySchema(new KeySchemaElement().withAttributeName("TaskId").withKeyType(KeyType.HASH))
						.withAttributeDefinitions(new AttributeDefinition().withAttributeName("TaskId")
								.withAttributeType(ScalarAttributeType.S))
						.withProvisionedThroughput(
								new ProvisionedThroughput().withReadCapacityUnits(100L).withWriteCapacityUnits(100L));
				TableDescription createdTableDescription = dynamoDB.createTable(createTableRequest)
						.getTableDescription();
				System.out.println("Created Table: " + createdTableDescription);

				// Wait for it to become active
				System.out.println("Waiting for " + tableName + " to become ACTIVE...");
				Tables.awaitTableToBecomeActive(dynamoDB, tableName);
			}

		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with AWS, "
					+ "such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param tableName
	 * @param taskId
	 *            It insert that taskId into a given table
	 */
	public void insertElementInTable(String tableName, String taskId) {
		// Add an item
		Map<String, AttributeValue> item = newItem(taskId);
		PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
		PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
	}

	private static Map<String, AttributeValue> newItem(String taskId) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("TaskId", new AttributeValue(taskId));
		item.put("isTaskExecuted", new AttributeValue("0"));
		return item;
	}

	/**
	 * @param tableName
	 * @param taskId
	 *            Checks whether table with tableName contains an entry for that
	 *            particular taskId.
	 * @returns true if found the entry otherwise returns false.
	 */
	public boolean checkIfEntryExist(String tableName, String taskId) {
		// Scan items for movies with a year attribute greater than 1985
		HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
		Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
				.withAttributeValueList(new AttributeValue().withS(taskId));
		scanFilter.put("TaskId", condition);
		ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
		ScanResult scanResult = dynamoDB.scan(scanRequest);
		System.out.println("Result: " + scanResult);
		if (scanResult.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param tableName
	 *            Delete a given table name
	 */
	public void deleteTable(String tableName) {
		this.dynamoDB.deleteTable(tableName);
	}
}
