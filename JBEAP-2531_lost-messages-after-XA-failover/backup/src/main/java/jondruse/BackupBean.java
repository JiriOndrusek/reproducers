package jondruse;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionManagement;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.XAConnection;
import javax.jms.XAConnectionFactory;
import javax.jms.XASession;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static javax.ejb.TransactionManagementType.BEAN;

@Startup
@TransactionManagement(BEAN)
@Singleton
public class BackupBean {
    private static final Logger log = Logger.getLogger(BackupBean.class.getName());

    private Queue queue = null;
    private boolean stop = false;
    protected int counter = 0;
    private int commitAfter = 10;

    // consumer.receive(receiveTimeout)
    private int receiveTimeout = 3000;

    @Resource(mappedName = "java:/JmsXA")
    private XAConnectionFactory cf;


    // list of received messages which were received and committed
    private List<Map<String, String>> listOfReceivedMessages = new ArrayList<Map<String, String>>();

    @PostConstruct
    public void start() {

//        try {
//            Thread.sleep(10_000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        stop = false;
        listOfReceivedMessages.clear();
    counter = 0;

        Context context = null;

        XAConnection con = null;

        XASession xaSession;

        XAResource xaRes;

        Session session = null;

        try {



            con = cf.createXAConnection();

            con.start();

            xaSession = con.createXASession();

            session = xaSession.getSession();

            if (queue == null) {
                queue = session.createQueue("TestQueue");
            }

            MessageConsumer consumer = session.createConsumer(queue);

//            // got and start any recovery which is necessary
//            startRecovery();

            // receive messages from queue in XA transaction - commits XA transaction every "commitAfter" message
            while (!stop) {

                try {

                    xaRes = xaSession.getXAResource();

                    // for any exception we throw away uncommitted messages and will try again
                    receiveMessagesInXATransaction(xaRes, consumer);

                } catch (Exception ex) {
                    // in case of failover we have to recreate all JMS objects
                    log.warning("Exception was thrown to xa consumer " + ex);
                    log.warning("Recreating all JMS objects.");
                    stop = true;
                    // we have to recreate all objects so failover is successful TODO check whether it's ok behaviour
                    // it's necessary to close old sessions so no more messages are scheduled for it TODO check whether it's ok behaviour
//                    session.close();
//                    xaSession.close();
//
//                    xaSession = con.createXASession();
//                    session = xaSession.getSession();
//                    consumer = session.createConsumer(queue);

//                    // run recovery of failed transactions -- this just to commit already prepared transactions
//                    runRecoveryScan();

                }
            }
//
//            // we need at least 2 recovery scans
//            for (int i = 0; i < howManyTimesToRecoverTransactionsAfterClientFinishes; i++) {
//                runRecoveryScan();
//                Thread.sleep(60000);
//            }

//            consumer.close();

        } catch (Exception e) {

//            exception = e;
            log.warning("Consumer got exception and ended:" + e);

        } finally {

            // clean up resources
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException ignored) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (JMSException ignored) {
                }
            }
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException ignored) {
                }
            }
        }
    }

    /*
     * Create and begin transaction
     * Enlist xaResource to transaction
     * Receive x messages (message window)
     * Commit transaction
     *
     * Exception is thrown when something bad happens, usually during failover. We don't care about failed transactions
     * as TM should be in charge of it.
     */
    private void receiveMessagesInXATransaction(XAResource xaRes, MessageConsumer consumer) throws Exception {

        // counter contains number of received messages which are already commited
        // numberOfReceivedMessages contains number of messages which are commited + not yet commited
        int numberOfReceivedMessages = counter;

        try {
            // start transaction

            // Step 12. create a transaction
            Xid xid1 = new DummyXid("xa-example1".getBytes(StandardCharsets.UTF_8), 1, UUID.randomUUID().toString().getBytes());

            // Step 14. Begin the Transaction work
            xaRes.start(xid1, XAResource.TMNOFLAGS);
//
//            transaction = txMgr.getTransaction();
//
//            // enlist xa resource to transaction
//            transaction.enlistResource(xaRes);
//            transaction.enlistResource(new DummyXAResource());

            Message message;

            // list of messages which is received in this transaction
            List<Message> receivedMessageWindow = new ArrayList<Message>();

            // while message not null, receive messages
            while ((message = consumer.receive(receiveTimeout)) != null) {

                numberOfReceivedMessages++;

                receivedMessageWindow.add(message);

                log.info("Consumer for node:"
                        + " and queue: " + queue.getQueueName() + " received message: " + message.getJMSMessageID()
                        + " . Number of received commited and uncommitted messages: "
                        + numberOfReceivedMessages + ", number of commited messages: " + counter);

                // if "commitAfter" messages is received break cycle and commit transaction
                if (numberOfReceivedMessages % commitAfter == 0) {
                    break;
                }

            }

            log.info("Going to commit XA transaction. Number of received commited and uncommitted messages will be:: " + numberOfReceivedMessages);
            log.warning("#####################################");

            // Step 23. Stop the work
            xaRes.end(xid1, XAResource.TMSUCCESS);

            // Step 24. Prepare
            xaRes.prepare(xid1);

            // Step 27. Commit!
            xaRes.commit(xid1, false);



//            // add committed messages to list of received messages
            addMessages(listOfReceivedMessages, receivedMessageWindow);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + listOfReceivedMessages.size() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

            // increase number of received messages
            counter += receivedMessageWindow.size();

            // clear messages window
            receivedMessageWindow.clear();

            // if null was received then queue is empty and stop the consumer
            if (message == null) {
                log.info("Setting stop to true");
                stop = true;
            }

        } finally {
        }

    }


    protected void addMessages(List<Map<String, String>> listOfReceivedMessages, List<Message> messages) throws JMSException {
        for (Message m : messages) {
            addMessage(listOfReceivedMessages, m);
        }
    }

    protected void addMessage(List<Map<String, String>> listOfReceivedMessages, Message message) throws JMSException {
        Map<String, String> mapOfPropertiesOfTheMessage = new HashMap<String, String>();
        mapOfPropertiesOfTheMessage.put("messageId", message.getJMSMessageID());
        if (message.getStringProperty("_AMQ_DUPL_ID") != null) {
            mapOfPropertiesOfTheMessage.put("_AMQ_DUPL_ID", message.getStringProperty("_AMQ_DUPL_ID"));
        }
        // this is for MDB test versification (MDB creates new message with inMessageId property)
        if (message.getStringProperty("inMessageId") != null) {
            mapOfPropertiesOfTheMessage.put("inMessageId", message.getStringProperty("inMessageId"));
        }
        if (message.getStringProperty("JMSXGroupID") != null) {
            mapOfPropertiesOfTheMessage.put("JMSXGroupID", message.getStringProperty("JMSXGroupID"));
        }
        mapOfPropertiesOfTheMessage.put("messagePriority", String.valueOf(message.getJMSPriority()));

        listOfReceivedMessages.add(mapOfPropertiesOfTheMessage);
    }

    private String getTransactionStatus(UserTransaction transaction) throws SystemException {
        int status = transaction.getStatus();
        if (status == Status.STATUS_ACTIVE) {
            return "STATUS_ACTIVE";
        } else if (status == Status.STATUS_MARKED_ROLLBACK) {
            return "STATUS_MARKED_ROLLBACK";
        } else if (status == Status.STATUS_PREPARED) {
            return "STATUS_PREPARED";
        } else if (status == Status.STATUS_COMMITTED) {
            return "STATUS_COMMITTED";
        } else if (status == Status.STATUS_ROLLEDBACK) {
            return "STATUS_ROLLEDBACK";
        } else if (status == Status.STATUS_UNKNOWN) {
            return "STATUS_UNKNOWN";
        } else if (status == Status.STATUS_NO_TRANSACTION) {
            return "STATUS_NO_TRANSACTION";
        } else if (status == Status.STATUS_PREPARING) {
            return "STATUS_PREPARING";
        } else if (status == Status.STATUS_COMMITTING) {
            return "STATUS_COMMITTING";
        } else if (status == Status.STATUS_ROLLING_BACK) {
            return "STATUS_ROLLING_BACK";
        } else {
            return "This status is not known to JTA.";
        }
    }

}
