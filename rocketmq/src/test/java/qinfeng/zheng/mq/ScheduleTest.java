package qinfeng.zheng.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.util.List;

/**
 * @Author ZhengQinfeng
 * @Date 2021/11/29 21:40
 * @dec 延迟队列测试
 */
public class ScheduleTest {


    @Test
    public void testProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
        // Launch producer
        producer.start();
        int totalMessagesToSend = 100;
        for (int i = 0; i < totalMessagesToSend; i++) {
            Message message = new Message("TestTopic", ("Hello scheduled message " + i).getBytes());
            // This message will be delivered to consumer 10 seconds later.
            // 延迟级别， message没有直接进入TestTopic队列，而是先放到了延迟队列中
            message.setDelayTimeLevel(3);
            // Send the message
            producer.send(message);
        }

        // Shutdown producer after use.
        producer.shutdown();
    }


    /**
     * 消息方代码没有什么变化！
     *
     * @throws Exception
     */
    @Test
    public void testConsumer() throws Exception {

        // Instantiate message consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ExampleConsumer");
        // Subscribe topics
        consumer.subscribe("TestTopic", "*");
        // Register message listener
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt message : messages) {
                    // Print approximate delay time period
                    System.out.println("Receive message[msgId=" + message.getMsgId() + "] " + (System.currentTimeMillis() - message.getStoreTimestamp()) + "ms later");
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });



        // Launch consumer
        consumer.start();
    }

}
 

