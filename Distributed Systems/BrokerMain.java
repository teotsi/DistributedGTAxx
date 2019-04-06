import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class BrokerMain {
    public static void main(String[] args) {
        ExecutorService requestPool = newCachedThreadPool();
        try {
            Broker currentServer = new Broker(Reader.getBrokerList("brokerIPs.txt"), InetAddress.getLocalHost());
            System.out.println(currentServer.ipAddress.toString());
        } catch (IOException e) {
            System.out.println("Error with server. Is the port available?");
            System.exit(1);
        }
    }
}
