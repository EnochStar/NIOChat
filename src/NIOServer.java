import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Package:PACKAGE_NAME
 * Description:
 *
 * @author:EnochStar
 */
public class NIOServer {
    public void start() throws IOException {
        // 创建一个连接器
        Selector selector = Selector.open();
        // 创建接受连接的Channel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置为非阻塞的
        serverSocketChannel.configureBlocking(false);
        // 指定端口
        serverSocketChannel.bind(new InetSocketAddress(8000));
        // 接受连接请求
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("server start");
        // 轮询
        for (;;) {
            // 获取连接数量
            int ready = selector.select();
            if (ready == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                if (next.isReadable()) {
                    readHandler(next,selector);
                }else if (next.isAcceptable()) {
                    acceptHandler(serverSocketChannel,selector);
                }
            }
        }
    }

    public void readHandler(SelectionKey selectionKey,Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        socketChannel.configureBlocking(false);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        String request = "";
        while (socketChannel.read(byteBuffer) > 0) {
            byteBuffer.flip();
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }
        if (request.length() > 0) {
            broadCast(selector,socketChannel,request);
        }
    }

    public void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector,SelectionKey.OP_READ);
        socketChannel.write(Charset.forName("UTF-8").encode("您与各位非好友关系，注意言多必失"));
    }

    public void broadCast(Selector selector,SocketChannel socketChannel,String request) throws IOException {
        Set<SelectionKey> keys = selector.keys();
        for (SelectionKey key : keys) {
            Channel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != socketChannel) {
                ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NIOServer nioServer = new NIOServer();
        nioServer.start();
    }
}
