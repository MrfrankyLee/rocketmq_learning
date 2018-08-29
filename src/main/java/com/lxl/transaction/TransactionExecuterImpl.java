package com.lxl.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionExecuter;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.common.message.Message;

public class TransactionExecuterImpl implements LocalTransactionExecuter {
    @Override
    public LocalTransactionState executeLocalTransactionBranch(Message message, Object args) {
        System.out.println(message.toString());
        System.out.println("message:" + new String(message.getBody()));
        System.out.println("args:" + args);
        String tags = message.getTags();

        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
