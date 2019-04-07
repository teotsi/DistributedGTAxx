import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class PubThreads {
    public static void main(String[] args) {
        new Reader("busLinesNew.txt", "brokerIPs.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        Publisher PubThread = new Publisher(new ArrayList<Broker>());
        try {
            sleep((new Random().nextInt(2)+1)*5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        PubThread.init(4321);
        PubThread.connect();
        PubThread.push(new Topic(PubThread.getBusLine()), null);
    }
}