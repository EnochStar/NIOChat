import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Package:PACKAGE_NAME
 * Description:
 *
 * @author:EnochStar
 */
public class NIOClient {
    public void start(String name) throws IOException {
        // 连接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8000));
        // 接受服务器响应
        socketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        socketChannel.register(selector,SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        // 向服务器发送数据
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String request = sc.nextLine();
            if (request != null && request.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(name + " :" +request));
            }
        }
    }
}
