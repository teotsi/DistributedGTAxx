import java.util.ArrayList;

public class Publisher{

    public Publisher(){}

    public void getBrokerList(){}
    public Broker hashTopic(Topic t){return new Broker(new ArrayList<Subscriber>(),new ArrayList<Publisher>());}
    public void push(Topic t, Value v){}
    public void notifyFailure(Broker b){}
}