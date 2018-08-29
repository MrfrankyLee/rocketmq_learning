package com.lxl.pull;

import org.apache.rocketmq.client.consumer.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

public class PullConsumer {
    public static void main(String[] args) throws MQClientException {
        final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("Groupname1");
        scheduleService.getDefaultMQPullConsumer().setNamesrvAddr("192.168.1.103:9876");
        scheduleService.setMessageModel(MessageModel.CLUSTERING);
        scheduleService.registerPullTaskCallback("pullTopic", new PullTaskCallback() {
            @Override
            public void doPullTask(MessageQueue mq, PullTaskContext context) {
                MQPullConsumer consumer = context.getPullConsumer();
                System.out.println("拉取消息中:" + mq.getQueueId() + " ,拉取的topici是:" + mq.getTopic());
                try {
                    // 获取从哪里拉取
                    long offset = consumer.fetchConsumeOffset(mq, false);
                    if (offset < 0)
                        offset = 0;

                    PullResult pullResult = consumer.pull(mq, "*", offset, 32);
                    switch (pullResult.getPullStatus()) {
                        case FOUND:
                            List<MessageExt> list = pullResult.getMsgFoundList();
                            for (MessageExt messageExt : list) {
                                System.out.println("拉取到的消息是：" + new String(messageExt.getBody()));
                            }
                            break;
                        case NO_MATCHED_MSG:
                            break;
                        case NO_NEW_MSG:
                        case OFFSET_ILLEGAL:
                            break;
                        default:
                            break;
                    }

                    // 存储Offset，客户端每隔5s会定时刷新到Broker
                    consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());

                    // 设置再过3000ms后重新拉取
                    context.setPullNextDelayTimeMillis(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        scheduleService.start();
    }
}
