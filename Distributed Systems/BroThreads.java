import java.util.ArrayList;

public class BroThreads {
    public static void main(String[] args) {
        Broker BroThread = new Broker(null, null, new ArrayList<Broker>());
        BroThread.init(4321);
        BroThread.connect();
    }
}
