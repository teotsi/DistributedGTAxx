import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

public class PubThreads {
    public static void main(String[] args) {
        new Reader("busLinesNew.txt", "brokerIPs.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        Reader.createPositionTable();
        for (int i = 0; i <3 ; i++) {
            new Thread(new Publisher(new ArrayList<Broker>())).start();
        }
    }
}