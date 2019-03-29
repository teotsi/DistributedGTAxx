import java.util.List;

public class Subscriber extends Node{

    public Subscriber( List<Broker> brokers) {
        super(brokers);
    }

    public void register(Broker b, Topic t){}
    public void disconnect(Broker b, Topic t){}
    public void visualiseData(Topic t, Value v){}
}