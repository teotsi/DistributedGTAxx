import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

public class Subscriber implements Node {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    public Subscriber( List<Broker> brokers) {
        this.brokers.addAll(brokers);
    }

    public void register(Broker b, Topic t){}
    public void disconnect(Broker b, Topic t){}
    public void visualiseData(Topic t, Value v) throws IOException, ClassNotFoundException {
        socket = new Socket(InetAddress.getByName("127.0.0.1"),4321);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        Scanner s = new Scanner(System.in);
        String i = s.next();
        System.out.println(i);
        out.writeObject(i);
        out.flush();
        String response = (String) in.readObject();
        System.out.println("From Sub: item "+i+" is "+ response);
        socket.close();
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