import java.util.ArrayList;

public class PubThreads {
    public static void main(String[] args) {
        new Reader("busLinesNew.txt", "brokerIPs.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        Reader.createPositionTable();
        for (int i = 0; i <5 ; i++) {
            new Thread(new Publisher(new ArrayList<Broker>())).start();
        }
    }
}