package com.github.miniature.queue.webspheremq.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.miniature.queue.webspheremq.WebSphereMQServer;
import com.ibm.mq.MQException;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WorkerTest {
    @Queue(value = "/worker-example", queueTypeHint = QueueType.WORKER_QUEUE)
    interface WorkerExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);

        try {
            WebSphereMQServer mqls = new WebSphereMQServer("gvm-presales1", "wwtravelwebapp", 1414, "QM_gvm_presales1");
            WebSphereMQServer mqls2 = new WebSphereMQServer("gvm-presales1", "wwtravelwebapp", 1414, "QM_gvm_presales1");

            WorkerExampleQueue one = Queuify.builder().decoder(new StringDecoder()).server(mqls).target(WorkerExampleQueue.class);
            WorkerExampleQueue two = Queuify.builder().decoder(new StringDecoder()).server(mqls2).target(WorkerExampleQueue.class);

            WorkerExampleQueue sender = Queuify.builder().encoder(new StringEncoder()).server(mqls).target(WorkerExampleQueue.class);

            one.receiveMessage((x) -> {
                oneReceiveMessage.set(true);
                return true;
            });
            two.receiveMessage((x) -> {
                twoReceiveMessage.set(true);
                return true;
            });


            sender.publishMessage("msg");
            Thread.sleep(500L);

            assertFalse(oneReceiveMessage.get() && twoReceiveMessage.get());
            assertTrue(oneReceiveMessage.get() || twoReceiveMessage.get());
        }catch (MQException mqe){
            mqe.printStackTrace();
        }
    }
}
