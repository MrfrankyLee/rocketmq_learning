package com.lxl.shunxu;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Consumer {
    public static void main(String[] args) throws Exception {
        Consumer consumer = new Consumer();
    }


    public Consumer() throws Exception {
        // 创建消费者组
        String group = "order_consumer";
        // 创建一个消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(group);
        // 链接集群
        consumer.setNamesrvAddr("106.12.112.217:9876");
        // 设置从什么地方开始消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 订阅消息主题
        consumer.subscribe("Thistopic", "tagA");
        //注册监听
        consumer.registerMessageListener(new Listener());
        consumer.start();
        System.out.println("consumer Started.");
    }

    class Listener implements MessageListenerOrderly {
        private Random random = new Random();

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
            //设置自动提交
            consumeOrderlyContext.setAutoCommit(true);
            for (MessageExt messageExt : list) {
                System.out.println(new String(messageExt.getBody()));
            }
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(5));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}
