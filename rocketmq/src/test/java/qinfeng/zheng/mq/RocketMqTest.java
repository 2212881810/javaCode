package qinfeng.zheng.mq;

import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.admin.TopicStatsTable;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.TopicList;
import org.apache.rocketmq.common.protocol.route.TopicRouteData;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Author ZhengQinfeng
 * @Date 2021/11/28 17:20
 * @dec
 */
public class RocketMqTest {

    @Test
    public void testProducer() throws Exception {


        DefaultMQProducer producer = new DefaultMQProducer("test-group");
        // Specify name server addresses.
        producer.setNamesrvAddr("192.168.79.70:9876");
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 10; i++) {

            Message message = new Message();


            message.setTopic("test-topic");
            message.setTags("test-tag-a");
            message.setBody(("0000 " + i).getBytes());

            // broker把消费存储之后才会响应client ,默认就是true
            //message.setWaitStoreMsgOK(true);
            //
            //
            ////同步发送
            //SendResult sendResult = producer.send(message);
            //
            //// 异常
            //producer.send(message, new SendCallback() {
            //    @Override
            //    public void onSuccess(SendResult sendResult) {
            //        System.out.println(sendResult);
            //    }
            //
            //    @Override
            //    public void onException(Throwable throwable) {
            //        System.out.println(throwable);
            //    }
            //});
            //
            //// 只生产一次， 不管结果如何
            //producer.sendOneway(message);


            // 消息存储在什么地方有3个维度： topic , broker , queueId

            MessageQueue messageQueue = new MessageQueue("test-topic", "node01", 0);
            // 通过messageQueue可以将消息发送给指定的topic ,指定的broker ,指定queue
            producer.send(message, messageQueue);

        }


    }

    @Test
    public void testConsumerPoll() throws Exception {

        DefaultLitePullConsumer consumer = new DefaultLitePullConsumer("c-g-2");
        consumer.setNamesrvAddr("192.168.79.70:9876");
        consumer.start();
        // 获取topic下的所有queue
        //Collection<MessageQueue> messageQueues = consumer.fetchMessageQueues("test-topic");
        //messageQueues.forEach(messageQueue -> {
        //    System.out.println(messageQueue);
        //});
        //
        //// 消费所有messageQueues中的消息
        //consumer.assign(messageQueues);

        // 消费指定broker 指定queue中的消息

        Collection<MessageQueue> queues = new ArrayList<>();
        //因为producer生产消息是生产到("test-topic", "node01", 0), 所以只能消费此queue中消息,否则是消费不到msg的
        MessageQueue mq = new MessageQueue("test-topic", "node01", 0);

        queues.add(mq);
        consumer.assign(queues);
        // seek 从那条消息开始进行消费!!!!!!!!!!!!!!!!!!
        //consumer.seek(mq, 3);


        // 读取消息
        List<MessageExt> poll = consumer.poll();

        poll.forEach(messageExt -> {
            byte[] body = messageExt.getBody();
            System.out.println(new String(body));
        });


        System.in.read();

    }


    /**
     * push模型订阅消息
     *
     * @throws Exception
     */
    @Test
    public void testConsumerPush() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("test-consume-group");
        consumer.setNamesrvAddr("192.168.79.70:9876");
        // 从头开始消费
        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        //订阅topic
        //consumer.subscribe("test-topic", "*");
        //consumer.subscribe("test-topic", "test-tag-a");
        //订阅指定topic的指定tag
        consumer.subscribe("test-topic", "test-tag-b");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                list.forEach(messageExt -> {
                    byte[] body = messageExt.getBody();

                    System.out.println(new String(body));
                });


                //消费成功
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.printf("Consumer Started.%n");
        System.in.read();

    }

    @Test
    public void testAdmin() throws Exception {
        DefaultMQAdminExt admin = new DefaultMQAdminExt();
        admin.setNamesrvAddr("192.168.79.70:9876");
        admin.start();
        TopicList topicList = admin.fetchAllTopicList();
        Set<String> queues = topicList.getTopicList();
        queues.forEach(s -> System.out.println(s));
        System.out.println();


        TopicStatsTable topicStatsTable = admin.examineTopicStats("test-topic");
        System.out.println(topicStatsTable);

        // topic的路由元数据
        TopicRouteData topicRouteData = admin.examineTopicRouteInfo("test-topic");
        System.out.println(topicRouteData);

    }

}
