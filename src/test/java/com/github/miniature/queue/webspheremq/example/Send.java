package com.github.miniature.queue.webspheremq.example;

import com.github.mlk.queue.Queuify;
import com.github.mlk.queue.Server;
import com.github.mlk.queue.codex.StringEncoder;
import com.github.miniature.queue.webspheremq.WebSphereMQServer;
import com.ibm.mq.MQException;

public class Send {

    public static void main(String... argv) {
        try {
            Server server = new WebSphereMQServer("gvm-presales1", "wwtravelwebapp", 1414, "QM_gvm_presales1");
            MessageQueue mq = Queuify.builder().server(server).encoder(new StringEncoder()).target(MessageQueue.class);

        /*for(String s : argv) {
            mq.publishMessage(s);
        }*/
            for (int i = 0; i < 50; i++) {
                mq.publishMessage("Hello WebSphere MQ");
            }
        }catch(MQException mqe){
            mqe.printStackTrace();
        }

    }
}
