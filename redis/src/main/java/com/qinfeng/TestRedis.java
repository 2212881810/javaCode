package com.qinfeng;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestRedis {

    /**
     * springboot集成的原生的redisTemplate
     */
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 自定义的redisTemplate
     */
    @Autowired
    @Qualifier("template")
    private StringRedisTemplate myTemplate;

    @Autowired
    ObjectMapper objectMapper;


    public void testRedis() {


        // 第1种方式
//        stringRedisTemplate.opsForValue().set("hello01","china");
//        System.out.println(stringRedisTemplate.opsForValue().get("hello01"));


        // 第2种方式
        RedisConnection conn = redisTemplate.getConnectionFactory().getConnection();
        conn.set("hello02".getBytes(), "world2".getBytes());
        System.out.println(new String(conn.get("hello02".getBytes())));


//        HashOperations<String, Object, Object> hash = myTemplate.opsForHash();
//        hash.put("sean","name","zhouzhilei");
//        hash.put("sean","age","22");
//
//        System.out.println(hash.entries("sean"));


        Person p = new Person();
        p.setName("zhangsan");
        p.setAge(16);


//        myTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));  // 将此句代码移动创建



        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);
        myTemplate.opsForHash().putAll("sean01", jm.toHash(p));

        Map map = myTemplate.opsForHash().entries("sean01");
        Person per = objectMapper.convertValue(map, Person.class);
        System.out.println(per.getName());


        // 发布消息
        myTemplate.convertAndSend("ooxx", "hello");

        // 订阅消息
        RedisConnection cc = myTemplate.getConnectionFactory().getConnection();
        cc.subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());

        while (true) {
            myTemplate.convertAndSend("ooxx", "hello  from wo zi ji ");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }


}
