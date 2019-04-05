import java.util.ArrayList;

public class PubThreads {
    public static void main(String[] args) {
//        new Reader("..\\dataset\\busLinesNew.txt");
        Publisher PubThread = new Publisher(new ArrayList<Broker>());
        PubThread.init(4321);
        PubThread.connect();
        //PubThread.push(new Topic(PubThread.busCode), null);
    }
}