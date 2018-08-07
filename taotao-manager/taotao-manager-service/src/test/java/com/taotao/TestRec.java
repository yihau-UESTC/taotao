package com.taotao;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;

public class TestRec {
    public static void main(String[] args) throws JMSException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        //1、创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.203:61616");
        //2、获取连接
        Connection connection = factory.createConnection();
        connection.start();
        //3、创建session,//不开启事务，消息签收模式-自动
        final Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
        //4、创建destination，可以是queue或者topic
        Queue hello = session.createQueue("queue");
        //前面几步相同
        // 5、创建消费者
        MessageConsumer consumer = session.createConsumer(hello);
        //6、接收消息，分为同步和异步，也可以称为pull/push
//        try {
//            TextMessage receive = (TextMessage)consumer.receive();
//            System.out.println(receive.getText());
//            session.commit();
//        }catch (Exception e){
//            session.rollback();
//        }

        //异步
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage)message;
                    try {
                        System.out.println(textMessage.getText());
                        session.commit();
                    } catch (JMSException e) {
                        e.printStackTrace();
                        try {
                            session.rollback();
                        } catch (JMSException e1) {
                            e1.printStackTrace();
                        }
                    }finally {
                        latch.countDown();
                    }
                }
            }
        });
        latch.await();
        //8、关闭连接
        if(connection != null)
            connection.close();
    }
}
