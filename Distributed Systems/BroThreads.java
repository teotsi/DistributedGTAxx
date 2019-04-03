import java.util.ArrayList;

public class BroThreads {
    public static void main(String[] args) {
        Broker BroThread = new Broker(new ArrayList<Broker>());
        BroThread.init(4321);
        BroThread.acceptConnection();
        BroThread.pull(null);
    }
}
