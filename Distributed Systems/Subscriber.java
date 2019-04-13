import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Subscriber implements Node {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String currentLine;
    private List<Map.Entry<String,List<String>>> OtherKeys;

    public Subscriber(List<Broker> brokers) {
        this.brokers.addAll(Reader.getBrokerList(PATH+"brokerIPs.txt"));
        Scanner in = new Scanner(System.in);
        System.out.print("Enter bus line:");
        String input = in.next();
        while(input.length()!=3){
            System.out.print("\nInvalid bus! Retry: ");
            input= in.next();
        }
        this.currentLine = input;
        init(4321);
        connect();
    }
    public boolean pull(ObjectInputStream in) {
        try {
            Topic tr= (Topic) in.readObject();
            Value vr = (Value) in.readObject();
            if(vr==null){
                System.out.println("found null");
                return false;
            }
            visualiseData(tr, vr);
        } catch (IOException e) {
            if(e.getMessage().contains("Connection reset")){
                System.out.println("Connection reset. Subscriber may be down.");
                return false;
            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
    public void register(Broker b, Topic t) {

    }

    public void disconnect(Broker b, Topic t) {
    }

    public void visualiseData(Topic t, Value v) throws IOException, ClassNotFoundException {
        System.out.println();
    }

    @Override
    public void init(int port) {
        try {
            int randomBroker = new Random().nextInt(3);
            socket = new Socket(brokers.get(randomBroker).getIpAddress(),port); //connecting to get key info
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(this.currentLine); //asking broker for list + if he's responsible for this key
            out.flush();
            OtherKeys = (List<Map.Entry<String, List<String>>>) in.readObject();
            boolean hasKey = (boolean) in.readObject();
            if(hasKey){
                boolean bool;
                do{
                    bool=pull(in);
                }while(bool);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
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