package com.github.miniature.queue.webspheremq.example;

import com.github.mlk.queue.Handle;
import com.github.mlk.queue.Publish;
import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueType;

import java.util.function.Function;

@Queue(value = "example.queue", queueTypeHint = QueueType.FANOUT_QUEUE)
public interface MessageQueue {
    @Publish
    void publishMessage(String message);

    @Handle
    void receiveMessage(Function<String, Boolean> function);
}
