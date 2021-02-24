package l_001;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @Author ZhengQinfeng
 * @Date 2021/2/21 22:24
 * @dec  生产者
 */
public class Sender {
    public static void main(String[] args) throws Exception{

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
        /*
            false ： 不使用事务 ; 如果开启事务，第2参数无用了，源码中写死了
            Session.AUTO_ACKNOWLEDGE 自动ack
            Session.CLIENT_ACKNOWLEDGE 手动ack

         */
//        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        // 从会话中获取目的地(Destination)消费者会从这个目的地取消息
        Queue queue = session.createQueue("f");


        //从会话中创建消息提供者
        MessageProducer producer = session.createProducer(queue);

        //从会话中创建文本消息(也可以创建其它类型的消息体)
        for (int i = 0; i < 100; i++) {
            TextMessage message = session.createTextMessage("msg: " + i);
            // 通过消息提供者发送消息到ActiveMQ
            Thread.sleep(1);
            producer.send(message);
            // 手动ack
             message.acknowledge();

        }

        // 关闭连接
        connection.close();
        System.out.println("exit");

    }
}
