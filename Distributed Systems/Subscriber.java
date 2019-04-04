import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Subscriber implements Node {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;

    public Subscriber(List<Broker> brokers) {
        this.brokers.addAll(brokers);
    }

    public void register(Broker b, Topic t) {

    }

    public void disconnect(Broker b, Topic t) {
    }

    public void visualiseData(Topic t, Value v) throws IOException, ClassNotFoundException {

    }

    @Override
    public void init(int port) {
        try {
            socket = new Socket(InetAddress.getByName("127.0.0.1"), 4321);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {

    }

    @Override
    public void updateNodes() {

    }
}