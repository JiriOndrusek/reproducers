
package jondruse;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
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
 * A MdbWithRemoteOutQueueNoRebalancing used for lodh tests. Used in RemoteJcaTestCase.
 * <p>
 * This mdb reads messages from queue "InQueue" and sends to queue "OutQueue".
 *
 * @author <a href="pslavice@jboss.com">Pavel Slavicek</a>
 * @author <a href="mnovak@redhat.com">Miroslav Novak</a>
 * @version $Revision: 1.1 $
 */
@MessageDriven(name = "mdb1",
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
    public static AtomicInteger numberOfProcessedMessages = new AtomicInteger();

    @Resource(mappedName = "java:/JmsXA")
    private ConnectionFactory cf;

    @Resource
    private MessageDrivenContext context;

    @Override
    public void onMessage(Message message) {

        Connection con = null;
        Session session = null;

        try {

            long time = System.currentTimeMillis();
            int counter = 0;
            try {
                counter = Integer.parseInt(((TextMessage)message).getText());
            } catch (Exception e) {
                log.log(Level.INFO, e.getMessage(), e);
            }
            String messageInfo = message.getJMSMessageID() + ", count:" + counter;

            log.info(" Start of message:" + messageInfo);

            for (int i = 0; i < (5 + 5 * Math.random()); i++) {
                try {
                    Thread.sleep((int) (100 + 100 * Math.random()));
                } catch (InterruptedException ex) {
                }
            }

            con = cf.createConnection();

            session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

            if (queue == null) {
                queue = session.createQueue("OutQueue");
            }

            con.start();

            String text = message.getJMSMessageID() + " processed by: " + hashCode();
            MessageProducer sender = session.createProducer(queue);
            TextMessage newMessage = session.createTextMessage(text);
            newMessage.setStringProperty("inMessageId", message.getJMSMessageID());
            newMessage.setStringProperty("_AMQ_DUPL_ID", message.getStringProperty("_AMQ_DUPL_ID"));
            sender.send(newMessage);

            messageInfo = messageInfo + ". Sending new message with inMessageId: " + newMessage.getStringProperty("inMessageId")
                    + " and messageId: " + newMessage.getJMSMessageID();

            log.info("End of " + messageInfo + " in " + (System.currentTimeMillis() - time) + " ms");

            if (numberOfProcessedMessages.incrementAndGet() % 100 == 0)
                log.info(messageInfo + " in " + (System.currentTimeMillis() - time) + " ms");

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