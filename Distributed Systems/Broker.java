import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.sun.net.ssl.internal.ssl.Provider;

public class Broker implements Node {

    List<Subscriber> registeredSubscribers;
    List<Publisher> registeredPublishers;
    ServerSocket server;
    Socket connectionSocket;
    ObjectOutputStream out;
    ObjectInputStream in;

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
        providerSocket = new ServerSocket(port);
    }

    @Override
    public void connect() {
        connection = providerSocket.accept();
        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
    }

    @Override
    public void disconnect() {
        in.close();
        out.close();
        connection.close();
        providerSocket.close();
        
    }

    @Override
    public void updateNodes() {

    }
}