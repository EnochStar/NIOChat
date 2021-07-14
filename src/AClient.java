import java.io.IOException;

/**
 * Package:PACKAGE_NAME
 * Description:
 *
 * @author:EnochStar
 */
public class AClient {
    public static void main(String[] args) throws IOException {
        new NIOClient().start("AClient");
    }
}
