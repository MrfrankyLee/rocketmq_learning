package com.lxl.quickstart;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class Consume {
    public static void main(String[] args) throws Exception {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("quickstart_consumer");
        // 设置链接的集群地址
        consumer.setNamesrvAddr("106.12.112.217:9876");
        //从队列的什么地方开始消费(第一次启动)
        // 非第一次启动按照上次的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        //消费者主题订阅
        consumer.subscribe("lixiaole", "*");
        /*consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
                MessageExt messageExt = list.get(0);

                try {
                    String topic = messageExt.getTopic();
                    String msgboy  = new String(messageExt.getBody(),"utf-8");
                    String tags = messageExt.getTags();

                    System.out.println("主题是--"+topic+",消息是--"+msgboy+",tags是--"+tags);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });*/
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                System.out.println("--------------------" + list.size());
                try {
                    for (MessageExt messageExt : list) {
                        String topic = messageExt.getTopic();
                        String msgbody = new String(messageExt.getBody(), "UTF8");
                        String tags = messageExt.getTags();

                        System.out.println("主题是--" + topic + ",消息是--" + msgbody + ",tags是--" + tags);
                    }
                } catch (Exception e) {
                   /* System.out.println("发起了第"+messageExt.getReconsumeTimes()+"次消息重试");
                    //e.printStackTrace();
                    if(messageExt.getReconsumeTimes() > 2){
                        System.out.println("已经重试了2两次了  不重试啦   886 就当它已经成功了吧");

                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;*/
                    e.printStackTrace();
                }
                System.out.println("消费成功.....................");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();

    }
}
