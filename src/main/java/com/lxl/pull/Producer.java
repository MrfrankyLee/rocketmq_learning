package com.lxl.pull;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

public class Producer {

    public static void main(String[] args) {
        startSend();
    }

    public static void startSend() {
        DefaultMQProducer producer = new DefaultMQProducer("thisGroupName1");
        producer.setNamesrvAddr("192.168.1.103:9876");
        try {
            producer.start();
            for (int i = 1; i < 31; i++) {
                String msg = "this is pull Message " + i;
                Message message = new Message("pullTopic", "Tags", "keys", msg.getBytes());
                SendResult sendResult = producer.send(message);
                System.out.println(sendResult);
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }
}
