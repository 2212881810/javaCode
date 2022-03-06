package qinfeng.zheng.kafka.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * @Author ZhengQinfeng
 * @Date 2022/2/26 13:45
 * @dec
 */
public class MyKafkaProducerCallBackWithPartitioner {

    public static void main(String[] args) {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.79.70:9092,node02:9092,node03:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        // 指定分区
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "qinfeng.zheng.kafka.partition.MyPartitioner");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 5; i++) {
            producer.send(new ProducerRecord("first", "xxx-" + i), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        System.out.println("主题："+ recordMetadata.topic()+",分区:"+ recordMetadata.partition());
                    }
                }
            });


        }
        producer.close();
        System.out.println("over!~");
    }
}
