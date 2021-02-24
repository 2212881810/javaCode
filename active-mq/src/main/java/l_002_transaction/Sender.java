package l_002_transaction;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/21 22:24
 * @dec 生产者
 */
public class Sender {
    public static void main(String[] args) throws Exception {

        // 1. 建立工厂对象，
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                ActiveMQConnectionFactory.DEFAULT_USER,
                ActiveMQConnectionFactory.DEFAULT_PASSWORD,
                "tcp://localhost:61616"
        );

        //2 从工厂里拿一个连接
        Connection connection = activeMQConnectionFactory.createConnection();

        connection.start();

        //3 从连接中获取Session(会话)
        // true 表示开启事务
        Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
        // 从会话中获取目的地(Destination)消费者会从这个目的地取消息
        Queue queue = session.createQueue("f");


        //从会话中创建消息提供者
        MessageProducer producer = session.createProducer(queue);

        //从会话中创建文本消息(也可以创建其它类型的消息体)
        for (int i = 0; i < 100; i++) {
            TextMessage message = session.createTextMessage("msg: " + i);

            // 通过消息提供者发送消息到ActiveMQ
            producer.send(message);
            // 事务提交。 在开启事务的前提下，如果不用commit提交，那么消息是不可能发送到broker
//            session.commit();// 每发送一次消息，提交1次
        }
        // 一次性提交
        session.commit();

        // 回滚
//        session.rollback();

        // 关闭连接
        connection.close();
        System.out.println("exit");

    }
}
