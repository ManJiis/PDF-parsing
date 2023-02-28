package io.github.wulilh.example.rpcdemo.center;

import java.net.Socket;

/**
 * @author wuliling Created By 2023-02-28 17:54
 **/
public class SocketRpcRequestTask implements Runnable {
    private Socket client = null;

    public SocketRpcRequestTask(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        SocketRpcRequestHandler.handler(client);
    }
}