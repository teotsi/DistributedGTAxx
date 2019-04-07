import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Publisher implements Node, Runnable, Serializable {
    Socket connectionSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String busLine;
    String[] busLineInfo;
    List<Bus> ListOfBuses=new ArrayList<Bus>();
    String[] Vehicles;
    List<Value> Values=new ArrayList<Value>();

    public Publisher(List<Broker> brokers) {
        this.brokers.addAll(brokers);
    }

    @Override
    public void init(int port) {
        System.out.println("sync starts");
        this.busLineInfo=Reader.getBus();
        this.busLine = this.busLineInfo[1];
        Reader.createBusesMap();//na mpei sthn main
        Reader.createRoutesNinfo();//na mpei sthn main
        int numofbuses=Reader.getNumberOfBuses(busLineInfo[0]);
        this.Vehicles=Reader.getVehicles(busLineInfo[0],numofbuses);
        for (int i = 0; i < numofbuses ; i++) {
            ListOfBuses.add(new Bus(busLineInfo[0],Reader.getRouteCode(this.Vehicles[i]),this.Vehicles[i],busLineInfo[2],busLineInfo[1],Reader.getInfo(Reader.getRouteCode(this.Vehicles[i]))));
        }
        createValues();
        System.out.println("sync done");
        try {
            connectionSocket = new Socket(InetAddress.getByName("127.0.0.1"), port); //initialising client
            System.out.println("after connection Socket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createValues(){ //na to trexei ka8e publisher
        List<String[]> table= Reader.getPositionTable();
        for(String[] line: table){
            for(Bus b: ListOfBuses){
                if(line[0].trim().equals(b.getLineNumber())&& line[2].trim().equals(b.getVehicleId())){
                    Values.add(new Value(b, Double.parseDouble(line[3]), Double.parseDouble(line[4])));
                }

            }
        }
    }

    @Override
    public void connect() {
        try {
            System.out.println("before finish connect");
            in = new ObjectInputStream(connectionSocket.getInputStream());
            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            System.out.println("finish connect");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return new Broker(null,null,true);
    }

    public void push(Topic t, Value v) {
        try {
            out.writeObject(t);
            out.flush();
            out.writeObject(v);
            out.flush();
            System.out.println("Publisher no" + Thread.currentThread().getId() + " pushed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void notifyFailure(Broker b) {
    }

    @Override
    public void run() {
        init(4321);
        connect();
        System.out.println("before push");
        push(new Topic(busLine), Values.get(0));
        while (true) {
        }
    }
}