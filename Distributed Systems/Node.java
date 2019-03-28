import java.util.List;

public class Node {

    List<Broker> brokers;

    public Node(List<Broker> list){
        this.brokers = list;
    }


    public void init(int value){}
    public void connect(){}
    public void disconnect(){}
    public void updateNodes(){}
}