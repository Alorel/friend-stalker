package cw.cmm529.util;

import cmm529.coursework.friend.model.Subscription;
import cmm529.coursework.friend.model.SubscriptionRequest;
import cmm529.coursework.friend.model.User;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.amazonaws.services.dynamodbv2.transactions.Transaction;
import com.amazonaws.services.dynamodbv2.transactions.TransactionManager;

/**
 * DynamoDB shorthands
 *
 * @author a.molcanovas@gmail.com
 */
@SuppressWarnings("deprecation")
public abstract class Dynamo {

    /**
     * Dynamo client
     */
    private final static AmazonDynamoDB client;

    /**
     * Dynamo transaction manager
     */
    private final static TransactionManager transactionMgr;

    /**
     * Transaction table name
     */
    private final static String transactionsTable = "_tx";

    /**
     * Transaction image table name
     */
    private final static String imagesTable = "_tx-images";

    /**
     * Endpoint URL
     */
    private final static String endpoint = "http://localhost:8000";

    static {
        final AWSCredentials cred = new DefaultAWSCredentialsProviderChain().getCredentials();
        client = new AmazonDynamoDBClient(cred);
        client.setEndpoint(endpoint);

        final Class<?>[] tables = new Class<?>[]{
                Subscription.class,
                SubscriptionRequest.class,
                User.class
        };

        try {
            for (final Class<?> clazz : tables) {
                System.out.printf("Trying to create table for %s%n", clazz.getSimpleName());
                try {
                    createTable(Subscription.class);
                    System.out.printf("Created table for %s%n", clazz.getSimpleName());
                } catch (final ResourceInUseException e) {
                    System.out.printf("Table for %s already exists%n", clazz.getSimpleName());
                }
            }

            System.out.println("Creating transaction table");
            TransactionManager.verifyOrCreateTransactionTable(client, transactionsTable, 50, 50, 25L);

            System.out.println("Creating transaction image table");
            TransactionManager.verifyOrCreateTransactionImagesTable(client, imagesTable, 50, 50, 25L);

            transactionMgr = new TransactionManager(client, transactionsTable, imagesTable);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a table for the given entity
     *
     * @param clazz The entity class
     */
    private static void createTable(final Class<?> clazz) {
        final CreateTableRequest rq = newMapper().generateCreateTableRequest(clazz);
        rq.setProvisionedThroughput(new ProvisionedThroughput(50L, 50L));
        client.createTable(rq);
    }

    /**
     * Start a new transaction
     *
     * @return The created transaction object
     */
    public static Transaction newTransaction() {
        return transactionMgr.newTransaction();
    }

    /**
     * Create a new DB mapper
     *
     * @return The created mapper
     */
    public static DynamoDBMapper newMapper() {
        return new DynamoDBMapper(client);
    }
}
