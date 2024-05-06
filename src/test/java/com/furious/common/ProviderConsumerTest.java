package com.furious.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.furious.consumer.ConsumerServiceDiscovery;
import com.furious.provider.Provider;
import com.furious.provider.ProviderServiceRegistry;

import lombok.SneakyThrows;

/**
 * @author furious 2024/5/6
 */
public class ProviderConsumerTest {

    @Test
    @SneakyThrows
    public void test() {
        int threadNum = 20;
        CountDownLatch latch = new CountDownLatch(threadNum);

        //server
        ProviderServiceRegistry.INSTANCE.register(HelloService.class, new HelloServiceImpl());
        Provider provider = new Provider();
        provider.start();

        //client
        ConsumerServiceDiscovery discovery = new ConsumerServiceDiscovery("localhost", 8888);
        HelloService helloService = discovery.get(HelloService.class);
        final AtomicInteger atomic = new AtomicInteger();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                String req = "hhh_" + atomic.incrementAndGet();
                String resp = helloService.sayHello(req);
                System.err.println("req:" + req + " resp:" + resp);
                latch.countDown();
            }).start();
        }
        latch.await();
        provider.close();
        discovery.close();
    }
}
