import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Broker implements Node, Runnable {

    private static int c = 4321;
    List<Subscriber> registeredSubscribers;
    List<Publisher> registeredPublishers;
    InetAddress ipAddress;
    ServerSocket providerSocket;
    Socket connection;
    ObjectOutputStream out;
    ObjectInputStream in;

    public Broker(List<Broker> brokers, InetAddress ipAddress) {
        this.brokers.addAll(brokers);
        this.registeredSubscribers = new ArrayList<Subscriber>();
        this.registeredPublishers = new ArrayList<Publisher>();
        this.brokers.add(this);
        this.ipAddress = ipAddress;
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
            System.out.println("Broker no" + Thread.currentThread().getId() + "read");

            System.out.println(t2.getBusLine());
            this.registeredPublishers.add((Publisher) in.readObject());
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
            this.ipAddress = providerSocket.getInetAddress();
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

    public void run() {
        System.out.println("entered broker");
            init(c++);
        while (true) {
            System.out.println("brooo");
            connect();

            pull(null);
        }
    }

}