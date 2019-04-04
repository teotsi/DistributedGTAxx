import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newCachedThreadPool;

public class BroThreads {
    public static void main(String[] args) {
        List<Broker> brokerList = new ArrayList<Broker>();
        ExecutorService ThreadPool = newCachedThreadPool();
        for (int i = 0; i < 5; i++) {
            brokerList.add(new Broker(new ArrayList<Broker>()));
        }
        for (int i = 0; i < 5; i++) {
            brokerList.get(i).brokers.addAll(brokerList);
        }
        for (int i = 0; i < 5; i++) {
            ThreadPool.execute(brokerList.get(i));
        }

        new Reader("..\\dataset\\busLinesNew.txt");
        while (Reader.moreBuses()) {
            ThreadPool.execute(new Publisher(brokerList));
            try {
                sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
