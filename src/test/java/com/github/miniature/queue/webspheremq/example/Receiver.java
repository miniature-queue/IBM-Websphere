package com.github.miniature.queue.webspheremq.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mlk.queue.codex.StringDecoder;
import com.github.miniature.queue.webspheremq.WebSphereMQServer;
import com.ibm.mq.MQException;

public class Receiver {
    public static void main(String... argv) {
        try {
            Server server = new WebSphereMQServer("gvm-presales1", "wwtravelwebapp", 1414, "QM_gvm_presales1");

            MessageQueue mq = Queuify.builder().server(server).decoder(new StringDecoder()).target(MessageQueue.class);

            mq.receiveMessage((x) -> {
                System.out.println(x);
                return true;
            });
            System.out.println("Waiting for messages...");
        }catch(MQException mqe){
            mqe.printStackTrace();
        }
    }
}
