package Service;

import java.util.ArrayList;

public class PubThreads {
    public static void main(String[] args) {
        new Reader("busLinesNew.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        Reader.createPositionTable();
        Reader.createBusesMap();
        Reader.createRoutesNinfo();
        for (int i = 0; i <13 ; i++) {
            new Thread(new Publisher(new ArrayList<>())).start();
        }
    }
}