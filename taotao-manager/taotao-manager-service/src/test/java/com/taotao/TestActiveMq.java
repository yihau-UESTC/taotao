package com.taotao;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;

public class TestActiveMq {
    @Test
    public void testSendQueue() throws JMSException {
        //建立连接工厂
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("tcp://192.168.0.203:61616");
        //获取一个连接
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //使用连接获创建一个session,一般不开启事务
        Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        //创建session的生产者，中间人(有queue和topic两种)， 和信息。
        Destination destination = session.createQueue("test-queue");
        MessageProducer producer = session.createProducer(destination);
        TextMessage message = session.createTextMessage();
        message.setText("hello queue!!!");
        //发送消息
        producer.send(message);
        //关闭资源
        producer.close();
        session.close();
        connection.close();
    }
    @Test
    public void testQueueConsumer()throws Exception{
        final CountDownLatch latch = new CountDownLatch(1);
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.203:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination queue = session.createQueue("test-queue");
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage){
                    TextMessage t = (TextMessage) message;
                    try {
                        System.out.println(t.getText());
                        latch.countDown();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("consumer has start!");
        latch.await();
        consumer.close();
        session.close();
        connection.close();
    }
    @Test
    public void testTopicProducer()throws Exception{
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.203:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic("test-topic");
        MessageProducer producer = session.createProducer(topic);
        TextMessage message = session.createTextMessage();
        message.setText("hello topic");
        producer.send(message);
        producer.close();
        session.close();
        connection.close();
    }

    @Test
    public void testTopicConsumer()throws Exception{
        final CountDownLatch latch = new CountDownLatch(1);
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.203:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination topic = session.createTopic("test-topic");
        MessageConsumer consumer = session.createConsumer(topic);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage){
                    TextMessage t = (TextMessage) message;
                    try {
                        System.out.println(t.getText());
                        latch.countDown();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        System.out.println("consumer has start!");
        latch.await();
        consumer.close();
        session.close();
        connection.close();
    }

}
