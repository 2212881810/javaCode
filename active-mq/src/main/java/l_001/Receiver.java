package l_001;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQMessageConsumer;

/**
 * 顺序消费
 */
public class Receiver {

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
//		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		// 从会话中获取目的地(Destination)消费者会从这个目的地取消息
		Queue queue = session.createQueue("f");
		
		
		//从会话中创建消息提供者
		
		MessageConsumer consumer = session.createConsumer(queue);
		//从会话中创建文本消息(也可以创建其它类型的消息体)
		


		while (true) {
			// receive会阻塞，直到有接收到消息
			TextMessage receive = (TextMessage)consumer.receive();

			System.out.println("TextMessage:" + receive.getText());
			// 手动ack
			receive.acknowledge();
			
		}


	}
}
