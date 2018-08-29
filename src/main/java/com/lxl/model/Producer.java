package com.lxl.model;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer {

    public static void main(String[] args) {
        String groupName = "producerGroup";
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr("192.168.1.103:9876");
        producer.setCompressMsgBodyOverHowmuch(1000);
        try {
            producer.start();
            for (int i = 1; i <= 800; i++) {
                String msg = "hello 这是RocketMQ消息 " + i;
                Message message = new Message("modelTopic", "TagA", "keys", msg.getBytes());
                SendResult sendResult = producer.send(message);
                System.out.println("这是第" + i + "条消息发出");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }
}
