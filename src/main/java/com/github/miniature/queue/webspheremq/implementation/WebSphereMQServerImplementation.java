package com.github.miniature.queue.webspheremq.implementation;

import com.github.mlk.queue.Queue;
import com.github.mlk.queue.QueueException;
import com.github.mlk.queue.QueueType;
import com.github.mlk.queue.implementation.ServerImplementation;
import com.ibm.mq.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSphereMQServerImplementation implements ServerImplementation {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private MQQueueManager qmanager;
    private ReentrantLock lock = new ReentrantLock();


    public WebSphereMQServerImplementation(MQQueueManager qmanager) {
        this.qmanager = qmanager;
    }

    @Override
    public void publish(Queue queue, byte[] message) throws QueueException {
        if(queue.queueTypeHint().equals(QueueType.WORKER_QUEUE)) {
            try {
                //ToDo: Implement other queue semantics

            } catch (Exception e) {
                throw new QueueException("failed to enqueue onto queue: " + queue.value(), e);
            }
        } else  {
            int openOptions = MQC.MQOO_OUTPUT + MQC.MQOO_FAIL_IF_QUIESCING;
            try {
                MQQueue mqueue = qmanager.accessQueue(queue.value(),
                        openOptions,
                        null,           // default q manager
                        null,           // no dynamic q name
                        null);         // no alternate user id

                MQMessage sendmsg               = new MQMessage();
                sendmsg.format                  = MQC.MQFMT_STRING;
                MQPutMessageOptions pmo = new MQPutMessageOptions();  // accept the defaults, same as MQPMO_DEFAULT constant
                sendmsg.clearMessage();
                sendmsg.messageId     = MQC.MQMI_NONE;
                sendmsg.correlationId = MQC.MQCI_NONE;
                sendmsg.write(message);

                // put the message on the queue

                mqueue.put(sendmsg, pmo);

            }catch (Exception e){
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    @Override
    public void listen(Queue queue, Function<byte[], Boolean> action) throws QueueException {
        logger.log(Level.INFO, "Registering listener");
        int openOptions = MQC.MQOO_INQUIRE + MQC.MQOO_FAIL_IF_QUIESCING + MQC.MQOO_INPUT_SHARED;



        try {

            MQQueue mqueue = qmanager.accessQueue(queue.value(),
                    openOptions,
                    null,           // default q manager
                    null,           // no dynamic q name
                    null);         // no alternate user id



            while(true) {
                MQMessage message = new MQMessage();
                try {
                    if (mqueue.getCurrentDepth() > 0) {
                        mqueue.get(message);
                        byte[] b = new byte[message.getMessageLength()];
                        message.readFully(b);
                        action.apply(b);
                        message.clearMessage();
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                } catch (MQException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                } catch (Exception e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
                // break;
                Thread.sleep(250L);
                Thread.yield();
            }
        } catch (Exception mqe) {
            logger.log(Level.SEVERE, mqe.getMessage());

        }



    }

    @Override
    public void close() {
        try{
            qmanager.close();
        }catch(Exception e){
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /*
    private NonBlockingClient getConnection() throws IOException, TimeoutException {
        lock.lock();
        try {
            if (client == null) {
                throw new QueueException("Client currently null", null);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }
    */
}
