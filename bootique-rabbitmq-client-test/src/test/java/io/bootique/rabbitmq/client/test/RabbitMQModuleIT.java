package io.bootique.rabbitmq.client.test;

import com.rabbitmq.client.*;
import io.bootique.BQRuntime;
import io.bootique.rabbitmq.client.RabbitMQModule;
import io.bootique.rabbitmq.client.channel.ChannelFactory;
import io.bootique.rabbitmq.client.connection.ConnectionFactory;
import io.bootique.test.junit.BQTestFactory;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivasiuk on 20.05.17.
 */
public class RabbitMQModuleIT {

    private final static String ROUTING_KEY = "key";
    private final static String TEST_MESSAGE = "Hi there!";
    private String cache;

    @Rule
    public BQTestFactory testFactory = new BQTestFactory();

    @Test
    public void testFullFlow() throws IOException {
        BQRuntime runtime = testFactory.app("-c", "classpath:config.yml")
                .module(RabbitMQModule.class)
                .createRuntime()
                .getRuntime();

        ConnectionFactory connectionFactory = runtime.getInstance(ConnectionFactory.class);
        ChannelFactory channelFactory = runtime.getInstance(ChannelFactory.class);

        Connection sendConnection = connectionFactory.forName("conn1");
        Channel sendChannel = channelFactory.openChannel(sendConnection, "exch1", "queue1", ROUTING_KEY);

        Connection receiveConnection = connectionFactory.forName("conn2");
        Channel receiveChannel = channelFactory.openChannel(receiveConnection, "exch1", "queue1", ROUTING_KEY);

        sendChannel.basicPublish("", "queue1",  null, TEST_MESSAGE.getBytes());
        receiveChannel.basicConsume("queue1", true, customConsumer(receiveChannel));
        assertEquals(TEST_MESSAGE, cache);
    }

    private DefaultConsumer customConsumer(Channel channel) {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                cache = new String(body);
            }
        };
    }
}
