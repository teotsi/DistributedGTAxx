import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;



public class Broker implements Node {

    List<Subscriber> registeredSubscribers;
    List<Publisher> registeredPublishers;
    ServerSocket providerSocket;
    Socket connection;
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
        try {
            Topic t2 = (Topic) in.readObject();
            System.out.println("read");
            System.out.println(t2.getBusLine());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(int port) {
        try {
            providerSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            connection = providerSocket.accept();
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disconnect() {
        try {
            in.close();
            out.close();
            connection.close();
            providerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNodes() {

    }
}