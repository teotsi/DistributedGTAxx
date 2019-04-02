import java.util.List;

public class Broker implements Node {

    List<Subscriber> registeredSubscribers;
    List<Publisher> registeredPublishers;

    public Broker(List<Subscriber> subs, List<Publisher> pubs, List<Broker> brokers) {
        this.brokers.addAll(brokers);
        this.registeredSubscribers = subs;
        this.registeredPublishers = pubs;
    }

    public void calculateKeys() {
    }

    public Publisher acceptConnection(Publisher p) {
        return p;
    }

    public Subscriber acceptConnection(Subscriber s) {
        return s;
    }

    public void notifyPublisher(String message) {
    }

    public void pull(Topic t) {
    }

    @Override
    public void init(int port) {

    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void updateNodes() {

    }
}