package io.bootique.rabbitmq.client.test;

import org.apache.qpid.server.Broker;
import org.apache.qpid.server.BrokerOptions;

import java.io.File;

/**
 * Created by ivasiuk on 20.05.17.
 */
public class InMemoryBrokerManager {
    private static final String INITIAL_CONFIG_PATH = "./src/test/resources/broker.conf";
    private final Broker broker = new Broker();

    public void startBroker() throws Exception {
        final BrokerOptions brokerOptions = new BrokerOptions();
        brokerOptions.setInitialConfigurationLocation(new File(INITIAL_CONFIG_PATH).getCanonicalPath());

        broker.startup(brokerOptions);
    }

    public void stopBroker() {
        broker.shutdown();
    }
}
