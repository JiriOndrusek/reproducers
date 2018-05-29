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

import java.util.logging.Logger;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Client {
    private static final Logger log = Logger.getLogger(Client.class.getName());

    // Set up all the default values
    private static final String DEFAULT_MESSAGE = "Hello, World!";
    private static final String DEFAULT_CONNECTION_FACTORY = "jms/RemoteConnectionFactory";
    private static final String DEFAULT_DESTINATION = "jms/queue/TestQueue";

    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PASSWORD = "W3lcome!";
    private static final String INITIAL_CONTEXT_FACTORY = "org.wildfly.naming.client.WildFlyInitialContextFactory";
    private static final String PROVIDER_URL = "http-remoting://127.0.0.1:8080";


    private static final String DEFAULT_MESSAGE_COUNT = "1";
    private static final int DEFAULT_TRASACTIONS_COUNT = 1;



    public static void main(String[] args) {

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
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            Destination destination = (Destination) namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");

            int count = Integer.parseInt(System.getProperty("message.count", DEFAULT_MESSAGE_COUNT));
            String content = System.getProperty("message.content", DEFAULT_MESSAGE);

            try (Connection con = connectionFactory.createConnection();Session session = con.createSession(true, Session.SESSION_TRANSACTED);JMSContext context = connectionFactory.createContext(userName, password)) {
                log.info("Sending " + count + " messages with content: " );


                // Send the specified number of messages
                for (int i = 0; i < DEFAULT_TRASACTIONS_COUNT; i++) {

                    for (int j = 0; j<count; j++) {
                        System.out.println("Sending "+j+"/"+i);
                        context.createProducer().send(destination, j+"/t"+i);
                    }

                    session.commit();
                    log.info("Transaction "+i+" commited");
//                    String msg = i+ "";
//                    for(int j =0 ; j<i; j++) {
//                        msg = msg +"x";
//                    }
//                    context.createProducer().send(destination, new byte[MESSAGE_SIZE]);
//                    context.createProducer().send(destination, "" + (i+1) + "  zxzxzyuyuyuyuyuyuyuyuyxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzx ");
//                    context.createProducer().send(destination, "" + (i+1) + "");

                }

//                // Create the JMS consumer
//                JMSConsumer consumer = context.createConsumer(destination);
//                // Then receive the same number of messages that were sent
//                for (int i = 0; i < count; i++) {
//                    String text = consumer.receiveBody(String.class, 5000);
//                    log.info("Received message with content " + text);
//                }
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


//        Client2.main(new String [0]);
    }

    public static void main2(String[] args) {

        for(int i = 0;  i< 10000; i=i+1000) {
            send(i, 1000);
        }

    }

    public static void send(int from, int count) {

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
            String connectionFactoryString = System.getProperty("connection.factory", DEFAULT_CONNECTION_FACTORY);
            log.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
            ConnectionFactory connectionFactory = (ConnectionFactory) namingContext.lookup(connectionFactoryString);
            log.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");

            String destinationString = System.getProperty("destination", DEFAULT_DESTINATION);
            log.info("Attempting to acquire destination \"" + destinationString + "\"");
            Destination destination = (Destination) namingContext.lookup(destinationString);
            log.info("Found destination \"" + destinationString + "\" in JNDI");


            try (JMSContext context = connectionFactory.createContext(userName, password)) {
                log.info("Sending " + count + " messages with content: " );


                // Send the specified number of messages
                for (int i = 0; i < count; i++) {
                    String msg = (from+i)+ "";
//                    for(int j =0 ; j<i; j++) {
//                        msg = msg +"x";
//                    }
//                    context.createProducer().send(destination, new byte[MESSAGE_SIZE]);
//                    context.createProducer().send(destination, "" + (i+1) + "  zxzxzyuyuyuyuyuyuyuyuyxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzxzx ");
//                    context.createProducer().send(destination, "" + (i+1) + "");
                    context.createProducer().send(destination, msg);
                }

//                // Create the JMS consumer
//                JMSConsumer consumer = context.createConsumer(destination);
//                // Then receive the same number of messages that were sent
//                for (int i = 0; i < count; i++) {
//                    String text = consumer.receiveBody(String.class, 5000);
//                    log.info("Received message with content " + text);
//                }
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
