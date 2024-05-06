# mini-rpc
简化版RPC，使用netty实现远程调用与响应，包含如下功能

1. 自定义协议
2. 编码/解码实现
3. 同步方式调用

单元测试
```java
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
```
