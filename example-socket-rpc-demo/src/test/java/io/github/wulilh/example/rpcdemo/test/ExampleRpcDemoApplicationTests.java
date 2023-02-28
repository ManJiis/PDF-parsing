package io.github.wulilh.example.rpcdemo.test;

import io.github.wulilh.example.rpcdemo.center.Server;
import io.github.wulilh.example.rpcdemo.center.SocketServiceCenter;
import io.github.wulilh.example.rpcdemo.provider.HelloService;
import io.github.wulilh.example.rpcdemo.provider.HelloServiceImpl;
import io.github.wulilh.example.rpcdemo.client.RPCClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.InetSocketAddress;

@SpringBootTest
class ExampleRpcDemoApplicationTests {

    @Test
    void test_socket_rpc() {
        Server serviceServer = new SocketServiceCenter(8088);
        serviceServer.register(HelloService.class, HelloServiceImpl.class);

        new Thread(new Runnable() {
            public void run() {
                try {
                    serviceServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        HelloService service = RPCClient
                .getRemoteProxyObj(HelloService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(service.sayHi("test"));

        serviceServer.stop();
    }

}
