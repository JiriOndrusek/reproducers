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
package org.jboss.as.quickstarts.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Definition of the two JMS destinations used by the quickstart
 * (one queue and one topic).
 */
@JMSDestinationDefinitions(
    value = {
        @JMSDestinationDefinition(
            name = "java:/queue/HELLOWORLDMDBQueue",
            interfaceName = "javax.jms.Queue",
            destinationName = "HelloWorldMDBQueue"
        ),
        @JMSDestinationDefinition(
            name = "java:/topic/HELLOWORLDMDBTopic",
            interfaceName = "javax.jms.Topic",
            destinationName = "HelloWorldMDBTopic"
        )
    })
/**
 * <p>
 * A simple servlet 3 as client that sends several messages to a queue or a topic.
 * </p>
 *
 * <p>
 * The servlet is registered and mapped to /HelloWorldMDBServletClient using the {@linkplain WebServlet
 * @HttpServlet}.
 * </p>
 *
 * @author Serge Pagop (spagop@redhat.com)
 *
 */
@WebServlet("/test")
public class HelloWorldMDBServletClient extends HttpServlet {

    private static final long serialVersionUID = -8314035702649252239L;


    @Inject
    private JMSContext context;

    @Resource(lookup = "java:jboss/exported/jms/queue/TestQueue")
    private Queue testQueue;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.write("<h1>Test</h1>");
        try {
            final Destination destination = testQueue;

            out.write("<p>Sending 10 messages to <em>" + destination + "</em></p>");

            JMSProducer producer = context.createProducer();

            for (int i = 0; i < 10; i++) {
                String text = "This is message " + (i + 1);
                TextMessage message = context.createTextMessage("This is message " + (i + 1));
                message.setStringProperty("_AMQ_LVQ_NAME", "property01");
                producer.send(destination, text);
                out.write("Message (" + i + "): " + text + "</br>");
            }

            out.write("</br></br> Receiving messages ... </br>");

            JMSConsumer consumer = context.createConsumer(destination);

            List<Message> receivedMessages = new ArrayList<Message>();
            Message message;
            do {
                message = consumer.receive(1000);

                if (message != null) {
                    out.write("Received " +((TextMessage)message).getText() + "</br>");
                    receivedMessages.add(message);
                }
            } while (message != null);

            if(receivedMessages.size() != 1) {
                out.write("<h1 style=\"color:red;\">!!!!ERROR!!!!</h1>Consumer should receive only 1 message");
            } else {
                out.write("<h1 style=\"color:green;\">OK</h1>");
            }


        } catch (JMSException e) {
            out.write("ERROR " + e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
