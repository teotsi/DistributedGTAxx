import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Publisher implements Node, Runnable {
    Socket connectionSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    int busCode;
    int port;

    public Publisher(List<Broker> brokers) {
        this.brokers.addAll(brokers);
    }

    @Override
    public void init(int port) {
        this.busCode = Reader.getBus();
        try {
            connectionSocket = new Socket(InetAddress.getByName("127.0.0.1"), port); //initialising client
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            in = new ObjectInputStream(connectionSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNodes() {

    }

    public void getBrokerList() {
    }

    public Broker hashTopic(Topic t) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5"); //initialising MD5
            md5.update(t.getBusLine().getBytes()); //hashing

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new Broker(new ArrayList<Subscriber>(), new ArrayList<Publisher>(), null);
    }

    public void push(Topic t, Value v) throws IOException {
        out.writeObject(t.getBusLine());
        out.flush();
    }

    public void notifyFailure(Broker b) {
    }

    @Override
    public void run() {
        init(1111);

    }
}