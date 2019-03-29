import java.net.Socket;
import java.util.List;

public class Node {

    List<Broker> brokers;

    public Node(List<Broker> list){
        this.brokers = list;
    }


    public void init(int value){
        Socket socket = new Socket(value);
    }
    public void connect(){}
    public void disconnect(){}
    public void updateNodes(){}
}