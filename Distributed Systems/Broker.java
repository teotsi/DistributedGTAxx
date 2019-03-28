public class Broker{

    List<Subscriber> registeredSubscribers;
    List<Publisher> registeredPublishers;

    public Broker(List<Subscriber> subs, List<Publisher> pubs){
        this.registeredSubscribers = subs;
        this.registeredPublishers = pubs;
    }

    public void calculateKeys(){}
    public Publisher acceptConnection(Publisher p){}
    public Subscriber acceptConnection(Subscriber s){}
    public void notifyPublisher(String message){}
    public void pull(Topic t){}
}