package com.lxl.transaction;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionCheckListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

public class Producer {

    public static void main(String[] args) throws MQClientException {
        // 创建一个生产者组
        String groupName = "Transaction_group";

        // 创建生产者
        TransactionMQProducer producer = new TransactionMQProducer(groupName);

        // 创建链接服务
        producer.setNamesrvAddr("192.168.1.103:9876");

        //事物会看最小并发数
        producer.setCheckThreadPoolMinSize(5);
        //事物回看最大并发数
        producer.setCheckThreadPoolMaxSize(20);
        // 队列数
        producer.setCheckRequestHoldMax(2000);

        producer.setTransactionCheckListener(new TransactionCheckListener() {
            @Override
            public LocalTransactionState checkLocalTransactionState(MessageExt messageExt) {
                System.out.println("state -- " + new String(messageExt.getBody()));
                // if数据库入库真实发生变化 则再次提交状态
                // 如果 数据库没有发生变化,则直接忽略该数据回滚即可
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        producer.start();

        TransactionExecuterImpl transactionExecuter = new TransactionExecuterImpl();
        try {
            for (int i = 0; i <= 10; i++) {
                String msg = "Hello RocketMQ " + i;
                Message message = new Message("TransactionTopic", "TagA", "keys", msg.getBytes());
                SendResult result = producer.sendMessageInTransaction(message, transactionExecuter, 1);
                System.out.println("sendResult :" + result);
            }
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        producer.shutdown();
    }
}
