import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Package:PACKAGE_NAME
 * Description:
 *
 * @author:EnochStar
 */
public class NioClientHandler implements Runnable{
    private Selector selector;
    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        try{
            for (;;) {
                int ready = selector.select();
                if (ready == 0) continue;

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey,selector);
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
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
            System.out.println(request);
        }
    }
}
