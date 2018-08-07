package com.taotao;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.command.ActiveMQQueue;
import org.junit.Test;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestActiveMq {

    @Test
    public void sendHelloQueue()throws JMSException{
        //1、创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.203:61616");
        //2、获取连接
        ActiveMQPrefetchPolicy policy = new ActiveMQPrefetchPolicy();
        policy.setQueuePrefetch(10);
        ((ActiveMQConnectionFactory) factory).setPrefetchPolicy(policy);
        Connection connection = factory.createConnection();

        connection.start();

        //3、创建session,//不开启事务，消息签收模式-自动
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        //4、创建destination，可以是queue或者topic
        Destination hello = session.createQueue("queue");
        //5、创建生产者
        MessageProducer producer = session.createProducer(hello);
        //6、设置消息持久化模式,不设置持久化的话在MQ挂掉时会丢失消息，持久化会使消息存在磁盘中
        //持久化支持/kahadb/leveldb/jdbc/在activeMQ服务端配置方式n
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        //7、创建消息，发送

            TextMessage msg = session.createTextMessage();
            msg.setText("hello" );
//            msg.setIntProperty("num", 5);
            producer.send(msg);

        session.commit();
        //8、关闭连接
        if(connection != null)
            connection.close();
    }
    @Test
    public void recHelloQueue() throws JMSException, InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        //1、创建连接工厂
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://192.168.1.203:61616");
        //2、获取连接
        Connection connection = factory.createConnection();
        connection.start();
        //3、创建session,//不开启事务，消息签收模式-自动
        final Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        //4、创建destination，可以是queue或者topic
//        final ActiveMQQueue activeMQQueue = new ActiveMQQueue("queue?condumer.exclusive=true");

        Queue hello = session.createQueue("queue");

        //前面几步相同
        // 5、创建消费者
        MessageConsumer consumer = session.createConsumer(hello);
        //6、接收消息，分为同步和异步，也可以称为pull/push
//        try {
//            TextMessage receive = (TextMessage)consumer.receive();
//            System.out.println(receive.getText());
//            int a = 1/0;
//            session.commit();
//        }catch (Exception e){
//            session.rollback();
//        }

//        //异步
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage){
                    TextMessage textMessage = (TextMessage)message;
                    try {
                        System.out.println(textMessage.getText());
                        int a = 1/0;
                        session.commit();
                        latch.countDown();
                    } catch (JMSException e) {
                        try {
                            session.rollback();
//                            textMessage.acknowledge();
                        } catch (JMSException e1) {
                            e1.printStackTrace();
                        }
                    }finally {

                    }
                }
            }
        });
        latch.await();
        //8、关闭连接
        if(connection != null)
            connection.close();
    }


    @Test
    public void testSendQueue() throws JMSException {
        //建立连接工厂
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory("tcp://192.168.1.203:61616");
        //获取一个连接
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //使用连接获创建一个session,这里是是否启用事务和签收模式
        Session session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
        //创建session的生产者，中间人(有queue和topic两种)， 和信息。
        Destination destination = session.createQueue("test-queue?consumer.prefetchSize=10");
        MessageProducer producer = session.createProducer(destination);
        //设置消息持久化，这是不持久化，重启后消息会丢失，可以设置持久化到kahadb/leveldb/jdbc
        //ID:yihau-5163-1533348451526-1:1:1:1:1
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        /**
         * 5种消息对象
         * StreamMessage
         * MapMessage
         * TextMessage
         * ObjectMessage
         * BytesMessage
         */
        try {
//            for (int j = 0; j < 2; j++) {
            for (int i = 0; i < 100; i++) {
                TextMessage message = session.createTextMessage();
                message.setText("hello queue!!!" + i);
//                    message.setStringProperty("JMSXGroupID", "Group" + j);
//                    message.setIntProperty("JMSXGroupSeq", 2 - i);//用作取消，不能作为排序
                //发送消息
                producer.send(message);

                if (i == 2) {
                    int a = 1;
//                    throw new RuntimeException();
                }

            }
//            }
            session.commit();
        } catch (Exception e) {
            session.rollback();
        }

        //关闭资源
        producer.close();
        session.close();
        connection.close();
    }

    @Test
    public void testQueueConsumer() throws Exception {
        final CountDownLatch latch = new CountDownLatch(2);
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("" +
                "tcp://192.168.1.203:61616" +
                "?jms.optimizeAcknowledge=true&jms.optimizeAcknowledgeTimeOut=100000");
        final Connection connection = connectionFactory.createConnection();
        connection.start();
        final Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        final Destination queue = session.createQueue("test-queue?consumer.prefetchSize=10");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {


                    MessageConsumer consumer = session.createConsumer(queue);
//        TextMessage receive = (TextMessage) consumer.receive();
//                   MessageConsumer consumer = session.createConsumer(queue, "num > 1");
                    //拉模式，也是同步模式
                    try {
                        for (int i = 0; i < 1000; i++) {
                            TextMessage receive = (TextMessage) consumer.receive();
                            //通过判断消息的Redelivered来判断消息是否被传送过
                            boolean jmsRedelivered = receive.getJMSRedelivered();
//                       System.out.println(jmsRedelivered);
                            System.out.println(Thread.currentThread().getName() + receive.getText());
                            receive.acknowledge();
                            if (i == 2) {
                                int a = 1;
//                           throw new RuntimeException();
                            }
//                            Thread.sleep(1000);
//                            session.commit();
                        }


                    } catch (Exception e) {
//                        session.rollback();
                    }
                    //手动签收
//                   receive.acknowledge();
                    connection.close();
                    latch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(runnable);
        executorService.execute(runnable);
//        executorService.execute(runnable);
        //推模式，也是异步模式
//        consumer.setMessageListener(new MessageListener() {
//            @Override
//            public void onMessage(Message message) {
//                if (message instanceof TextMessage){
//                    TextMessage t = (TextMessage) message;
//                    try {
//                        System.out.println(t.getText());
//                        latch.countDown();
//                    } catch (JMSException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        System.out.println("consumer has start!");
        latch.await();
        executorService.shutdown();
//        latch.await();
//        consumer.close();
//        session.close();
//        connection.close();
    }

    @Test
    public void testTopicProducer() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.203:61616");
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
    public void testTopicConsumer() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.1.203:61616");

        Connection connection = connectionFactory.createConnection();
        connection.setClientID("consumer1");
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination topic = session.createTopic("test-topic");

//        MessageConsumer consumer = session.createConsumer(topic);
        final TopicSubscriber consumer = session.createDurableSubscriber((Topic) topic, "consumer1");
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                if (message instanceof TextMessage) {
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
