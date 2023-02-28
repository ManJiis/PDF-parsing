package io.github.wulilh.example.rpcdemo.center;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @author wuliling Created By 2023-02-28 21:22
 **/
public class SocketRpcRequestHandler {

    public static void handler(Socket client) {
        ObjectInputStream input = null;
        ObjectOutputStream output = null;
        try {
            // 2.将客户端发送的码流反序列化成对象，反射调用服务实现者，获取执行结果
            input = new ObjectInputStream(client.getInputStream());
            String serviceName = input.readUTF();
            String methodName = input.readUTF();
            Class<?>[] parameterTypes = (Class<?>[]) input.readObject();
            Object[] arguments = (Object[]) input.readObject();
            Class<?> serviceClass = SocketServiceCenter.getService(serviceName);
            if (serviceClass == null) {
                throw new ClassNotFoundException(serviceName + " not found");
            }
            Method method = serviceClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceClass.newInstance(), arguments);

            // 3.将执行结果反序列化，通过socket发送给客户端
            output = new ObjectOutputStream(client.getOutputStream());
            output.writeObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
            IOUtils.closeQuietly(input);

            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
