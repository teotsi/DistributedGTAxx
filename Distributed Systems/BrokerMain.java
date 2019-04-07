import java.io.IOException;
import java.net.InetAddress;

public class BrokerMain {
    public static void main(String[] args) {
        try {
            Broker currentServer = new Broker(Reader.getBrokerList("brokerIPs.txt"), InetAddress.getLocalHost());
            currentServer.init(4321);
            while (true) {
                currentServer.connect();
                new Thread(currentServer).start();
            }
        } catch (IOException e) {
            System.out.println("Error with server. Is the port available?");
            System.exit(1);
        }
    }
}
