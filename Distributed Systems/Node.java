import java.util.ArrayList;
import java.util.List;

public interface Node {
    List<Broker> brokers = new ArrayList<Broker>();

    void init(int port);

    void connect();

    void disconnect();

    void updateNodes();
}
