/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jondruse;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Logger;

/**
 * <p>
 * A simple Message Driven Bean that asynchronously receives and processes the messages that are sent to the queue.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 */
@MessageDriven(name = "HelloWorldQueueMDB", activationConfig = {
       @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/test") })
public class HelloWorldQueueMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(HelloWorldQueueMDB.class.toString());

    @EJB
    StatefullBean statefullBean;
    /**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message rcvMessage) {
        TextMessage msg = null;
        String text = null;
try {
    if (rcvMessage instanceof TextMessage) {
        msg = (TextMessage) rcvMessage;
        text = msg.getText();
        LOGGER.info("Received Message: --------------------------" + text + "-----------------------------------");
//        count = statefullBean.add();
    } //else {
//                LOGGER.warning("Message of wrong type: " + rcvMessage.getClass().getName());
//            }

    try {
        long millis = Long.parseLong(System.getProperty("JBEAP-13613-sleep", "10000"));

        Thread.sleep((long)(millis/**Math.random()*/));
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    LOGGER.info("------------- wake up "+text);

} catch( JMSException e) {
    e.printStackTrace();
}
        }
}
