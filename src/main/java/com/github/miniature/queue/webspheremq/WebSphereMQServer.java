package com.github.miniature.queue.webspheremq;

import com.github.mlk.queue.Server;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.github.miniature.queue.webspheremq.implementation.WebSphereMQServerImplementation;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueueManager;


import java.util.logging.Logger;


public class WebSphereMQServer extends Server {

    private final Logger logger = Logger.getLogger(getClass().getName());

    private final WebSphereMQServerImplementation implementation;

    /** @param host The host to connect to. This will result in a default connection with this host.
     *  @param channel The host to connect to. This will result in a default connection with this host.
     *  @param port The host to connect to. This will result in a default connection with this host.
     *  @param qManager The host to connect to. This will result in a default connection with this host.
     */
    public WebSphereMQServer(String host, String channel, int port, String qManager) throws MQException {
        MQEnvironment.hostname = host;
        MQEnvironment.channel  = channel;
        MQEnvironment.port     = port;

        MQQueueManager qmanager = new MQQueueManager(qManager);
        implementation = new WebSphereMQServerImplementation(qmanager);

    }

    /** @param qmanager A pre-constructed connection for WebSphere MQ
     */
    public WebSphereMQServer(MQQueueManager qmanager) {
        implementation = new WebSphereMQServerImplementation(qmanager);
    }

    @Override
    protected ServerImplementation getImplementation() {
        return implementation;
    }
}
