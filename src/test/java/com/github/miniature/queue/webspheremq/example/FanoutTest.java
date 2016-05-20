package com.github.miniature.queue.webspheremq.example;

import com.github.mlk.queue.*;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.miniature.queue.webspheremq.WebSphereMQServer;
import com.ibm.mq.MQException;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FanoutTest {
    @Queue(value = "example.queue", queueTypeHint = QueueType.FANOUT_QUEUE)
    interface FanoutExampleQueue {
        @Publish
        void publishMessage(String message);

        @Handle
        void receiveMessage(Function<String, Boolean> function);
    }

    @Test
    public void whenItemPutOnQueueThenAllListenersRelieveACopy() throws InterruptedException {
        final AtomicBoolean oneReceiveMessage = new AtomicBoolean(false);
        final AtomicBoolean twoReceiveMessage = new AtomicBoolean(false);

        try{
            WebSphereMQServer mqls =  new WebSphereMQServer("192.168.0.26", "wwtravelwebapp", 1414, "QM_gvm_presales1");
            WebSphereMQServer mqls2 =  new WebSphereMQServer("192.168.0.26", "wwtravelwebapp", 1414, "QM_gvm_presales1");

            FanoutExampleQueue one = Queuify.builder().decoder(new StringDecoder()).server(mqls).target(FanoutExampleQueue.class);
            FanoutExampleQueue two = Queuify.builder().decoder(new StringDecoder()).server(mqls2).target(FanoutExampleQueue.class);
            FanoutExampleQueue sender = Queuify.builder().encoder(new StringEncoder()).server(mqls).target(FanoutExampleQueue.class);

            one.receiveMessage((x) -> { oneReceiveMessage.set(true); return true; });
            two.receiveMessage((x) -> { twoReceiveMessage.set(true); return true; });


            sender.publishMessage("msg");
            Thread.sleep(1500L);

            assertTrue(oneReceiveMessage.get() && twoReceiveMessage.get());
        }catch (MQException mqe){
            mqe.printStackTrace();
            fail();
        }
    }
}
