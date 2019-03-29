import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;

public class Publisher extends Node {
    ServerSocket server;
    Socket connectionSocket;
    public Publisher(List<Broker> brokers) {
        super(brokers);

    }

    @Override
    public void init(int port){
        try {
            server = new ServerSocket(port); //initialising server
            while(true){
                connectionSocket = server.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void push(Topic t, Value v) {
    }

    public void notifyFailure(Broker b) {
    }
}