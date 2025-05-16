package com.ejb;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import jakarta.jms.Topic;

@Stateless
public class LogProducerBean {

    @Resource(lookup = "java:/JmsXA")            // your XA ConnectionFactory JNDI
    private JMSContext jmsContext;

    @Resource(lookup = "java:/jms/topic/logTopic") // your Topic bound to RabbitMQ log.exchange
    private Topic logTopic;

    /**
     * Publish a log entry to the log topic.
     *
     * @param serviceName the name of the service (e.g. "Seller")
     * @param severity    one of "Info", "Warning", "Error"
     * @param message     the log message
     */
    public void log(String serviceName, String severity, String message) {
        try {
            TextMessage jmsMsg = jmsContext.createTextMessage(message);
            // You can also embed the service/severity in the body or as properties
            jmsMsg.setStringProperty("service", serviceName);
            jmsMsg.setStringProperty("severity", severity);

            jmsContext.createProducer()
                    .send(logTopic, jmsMsg);
        } catch (JMSException e) {
            // handle or log internally
            e.printStackTrace();
        }
    }
}
