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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Logger;

public class ClientUtil {
    private static final Logger log = Logger.getLogger(ClientUtil.class.getName());

    // Set up all the default values
    private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_DESTINATION = "jms/queue/TestQueue";

    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PASSWORD = "W3lcome!";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";



    public static void send(String connectionFactory_jndi, int count, Function<Integer, String> messageProducer, Function<Integer, Map<String, String>> propertiesProducer) {

        Context namingContext = null;

        try {
            String userName = System.getProperty("username", DEFAULT_USERNAME);
            String password = System.getProperty("password", DEFAULT_PASSWORD);

            // Set up the namingContext for the JNDI lookup
            final Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, System.getProperty(Context.PROVIDER_URL, PROVIDER_URL));
            env.put(Context.SECURITY_PRINCIPAL, userName);
            env.put(Context.SECURITY_CREDENTIALS, password);
            namingContext = new InitialContext(env);

            // Perform the JNDI lookups
            String connectionFactoryString = System.getProperty("connection.factory", connectionFactory_jndi);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            Destination destination = (Destination) namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");



            try (Connection con = connectionFactory.createConnection();Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
                MessageProducer producer = session.createProducer(destination);
                con.start();


                log.info("Sending " + count + " messages with content: " );



                for (int i = 0; i<count; i++) {

                    TextMessage message = session.createTextMessage(messageProducer.apply(i)+" " + i);
                    Map<String, String> properties = propertiesProducer.apply(i);
                    for (Map.Entry<String, String> e : properties.entrySet()) {
                        message.setStringProperty(e.getKey(), e.getValue());
                    }

                    producer.send(message);
                    log.info(" Send " + message.getText());
                }

                MessageConsumer consumer = session.createConsumer(destination);

                List<Message> receivedMessages = new ArrayList<Message>();
                Message message;
                do {
                    message = consumer.receive(1000);

                    if (message != null) {
                        log.info("<><><><><><> Received " +((TextMessage)message).getText());
                        receivedMessages.add(message);
                    }
                } while (message != null);

                if(receivedMessages.size() != 1) {
                    throw new IllegalStateException("Consumer should receive only 1 message");
                }

            }
        } catch (Exception e) {
            log.severe(e.getMessage());
            e.printStackTrace();
        } finally {
            if (namingContext != null) {
                try {
                    namingContext.close();
                } catch (NamingException e) {
                    log.severe(e.getMessage());
                }
            }
        }


    }

}
