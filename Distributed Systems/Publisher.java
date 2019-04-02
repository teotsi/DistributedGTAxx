import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;

public class Publisher implements Node {
    ServerSocket server;
    Socket connectionSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    int[] array = {81, 52, 34, 42, 54, 67, 57, 18, 29};
    public Publisher(List<Broker> brokers) {
        this.brokers.addAll(brokers);
    }

    @Override
    public void init(int port){
        try {
            server = new ServerSocket(4321); //initialising server
        } catch (IOException e) {
            e.printStackTrace();
        }
        connect();
    }

    @Override
    public void connect(){
        while(true){
            try {
                connectionSocket = server.accept();

            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            in = new ObjectInputStream(connectionSocket.getInputStream());
            int request = Integer.parseInt( (String) in.readObject());
            int response = array[request];
            push(new Topic((String.valueOf(response))),null);
            connectionSocket.close();
            server.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void disconnect() {

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
}