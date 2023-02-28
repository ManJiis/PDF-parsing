package io.github.wulilh.simplerpc.remoting.transport.socket;

import io.github.wulilh.simplerpc.common.constants.RpcConstants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuliling Created By 2023-02-28 23:38
 **/
@Slf4j
public class SocketRpcServer {
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        // TODO: 2023/3/1
        server.bind(new InetSocketAddress(RpcConstants.PORT));
        System.out.println("start server");
        try {
            while (true) {
                // 1.监听客户端的TCP连接，接到TCP连接后将其封装成task，由线程池执行
                executor.execute(new SocketRpcRequestTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }
}
