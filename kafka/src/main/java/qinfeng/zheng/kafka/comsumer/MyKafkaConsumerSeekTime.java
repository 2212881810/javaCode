package qinfeng.zheng.kafka.comsumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

/**
 * @Author ZhengQinfeng
 * @Date 2022/2/27 12:06
 * @dec 指定时间进行消费
 */
public class MyKafkaConsumerSeekTime {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "node01:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group.id.001");

        // 关闭自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);


        // 1.创建消费者对象
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        // 2. 订阅主题
        List<String> topics = new ArrayList<>();
        topics.add("first");
        kafkaConsumer.subscribe(topics);

        // 指定位置进行消费
        Set<TopicPartition> topicPartitions = kafkaConsumer.assignment();


        // 保证分区方案已经实施完毕
        while (topicPartitions.size() == 0) {
            kafkaConsumer.poll(Duration.ofMillis(1));
            topicPartitions = kafkaConsumer.assignment();
        }


        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        for (TopicPartition topicPartition : topicPartitions) {
            // 消费1天前的数据
            timestampsToSearch.put(topicPartition, System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000);
        }

        Map<TopicPartition, OffsetAndTimestamp> offsetAndTimestampMap = kafkaConsumer.offsetsForTimes(timestampsToSearch);

        for (TopicPartition topicPartition : topicPartitions) {
            // 通过时间转换成offset进行位置指定消费
            kafkaConsumer.seek(topicPartition, offsetAndTimestampMap.get(topicPartition).offset());
        }


        // 3.拉取数据
        while (true) {

            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(record);
            }
            //同步提交
            kafkaConsumer.commitSync();
            // 异步提交
            //kafkaConsumer.commitAsync();

        }

    }
}
