package qinfeng.zheng.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @Author ZhengQinfeng
 * @Date 2022/2/26 13:45
 * @dec
 */
public class MyKafkaProducerAcks {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.79.70:9092,node02:9092,node03:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //ack
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        
        // retry,默认是int的最大值
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 5; i++) {
            //同步发送
            RecordMetadata metadata = (RecordMetadata) producer.send(new ProducerRecord("first", "test-" + i)).get();

            System.out.println("主题：" + metadata.topic() + " , 分区：" + metadata.partition());
        }
        producer.close();
        System.out.println("over!~");
    }
}
