
package jondruse;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 */
@MessageDriven(name = "mdb-live",
        activationConfig = {
                @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
                @ActivationConfigProperty(propertyName = "rebalanceConnections", propertyValue = "false"),
                @ActivationConfigProperty(propertyName = "hA", propertyValue = "true"),
                @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/InQueue")})
@TransactionManagement(value = TransactionManagementType.CONTAINER)
@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
public class Mdb implements MessageListener {

    private static final long serialVersionUID = 2770941392406343837L;
    private static final Logger log = Logger.getLogger(Mdb.class.getName());
    private Queue queue = null;
    private static final int  COUNT_OF_COMMITS = 2;
    private static final int  COUNT_OF_MESSAGES = 2;
    public static AtomicInteger numberOfProcessedMessages = new AtomicInteger();

    @Resource(mappedName = "java:jboss/exported/jms/RemoteConnectionFactory")
    private ConnectionFactory cf;

    @Resource
    private MessageDrivenContext context;

    @EJB
    Bean bean;



    @Override
    public void onMessage(Message message) {

        Connection con = null;
        Session session = null;

        try {

            log.info("<><><><><><>" + ((TextMessage)message).getText());

            bean.start();

//            log.info(" Starting to send messages to testQueue...");
//
//            con = cf.createConnection();
//            session = con.createSession(true, Session.SESSION_TRANSACTED);
//
//            for(int i =0; i< COUNT_OF_COMMITS; i++) {
//
//
//
//
//                if (queue == null) {
//                    queue = session.createQueue("TestQueue");
//                }
//
//                con.start();
//
//                for(int j = 0; j < COUNT_OF_MESSAGES; j++) {
//
//                    String text = message.getJMSMessageID() + " processed by: " + hashCode();
//                    MessageProducer sender = session.createProducer(queue);
//                    TextMessage newMessage = session.createTextMessage(text);
//                    newMessage.setStringProperty("inMessageId", message.getJMSMessageID());
//                    newMessage.setStringProperty("_AMQ_DUPL_ID", message.getStringProperty("_AMQ_DUPL_ID"));
//                    sender.send(newMessage);
//
//                    log.info("Sending "+j+"th message of "+i+"th commit");
//                }
//
//                session.commit();
//                log.info("Transaction "+i+"commited");
//
//            }
        } catch (Exception t) {
            log.warning(t.getMessage());
            this.context.setRollbackOnly();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (JMSException e) {
                    log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}