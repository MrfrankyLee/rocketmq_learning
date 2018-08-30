package com.lxl.quickstart;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Productor {

    public static void main(String[] args) throws Exception {
        // 创建producer对象
        DefaultMQProducer producer = new DefaultMQProducer("quickStartproducer");
        producer.setNamesrvAddr("192.168.1.103:9876;192.168.1.105:9876");

        producer.start();
        for (int i = 1; i < 21; i++) {
            Message message = new Message("lixiaole",
                    "Tag1",
                    "kkk",
                    ("hello rocketMq :" + i).getBytes()
            );
            // System.out.println(message.toString());
            SendResult result = producer.send(message);
            System.out.println(result);
        }
        producer.shutdown();
    }
}
