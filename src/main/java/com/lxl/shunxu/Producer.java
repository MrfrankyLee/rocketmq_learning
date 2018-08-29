package com.lxl.shunxu;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Producer {
    public static void main(String[] args) throws Exception {
        // 创建消息的群组名称
        String groupName = "order_producer";

        // 创建生产者
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        // 设置链接集群地址
        producer.setNamesrvAddr("106.12.112.217:9876");
        producer.start();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm");
        String dateStr = sdf.format(date);

        for (int i = 0; i < 5; i++) {
            //设置消息体  加时间戳
            String body = dateStr + "  1号队列 hello RocketMQ：" + i;
            // 设置消息消息参数
            Message message = new Message("Thistopic", "tagA", "key" + i, body.getBytes());
            // 发送数据  使用顺序消费 必须自己实现MessageQueueSelector 保证消息进入到同一个队列
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                    Integer id = (Integer) arg;
                    System.out.println("id=" + id);
                    return list.get(id);
                }
            }, 1); // 1 是队列的下标
            System.out.println("sendResult:" + sendResult);
        }

        for (int i = 0; i < 5; i++) {
            //设置消息体  加时间戳
            String body = dateStr + "2号队列 hello RocketMQ：" + i;
            // 设置消息消息参数
            Message message = new Message("Thistopic", "tagA", "key" + i, body.getBytes());
            // 发送数据  使用顺序消费 必须自己实现MessageQueueSelector 保证消息进入到同一个队列
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                    Integer id = (Integer) arg;
                    System.out.println("id=" + id);
                    return list.get(id);
                }
            }, 2); // 1 是队列的下标
            System.out.println("sendResult:" + sendResult);
        }

        for (int i = 0; i < 5; i++) {
            //设置消息体  加时间戳
            String body = dateStr + "3号队列 hello RocketMQ：" + i;
            // 设置消息消息参数
            Message message = new Message("Thistopic", "tagA", "key" + i, body.getBytes());
            // 发送数据  使用顺序消费 必须自己实现MessageQueueSelector 保证消息进入到同一个队列
            SendResult sendResult = producer.send(message, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                    Integer id = (Integer) arg;
                    System.out.println("id=" + id);
                    return list.get(id);
                }
            }, 3); // 1 是队列的下标
            System.out.println("sendResult:" + sendResult);
        }
        producer.shutdown();
    }
}
