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

import static java.lang.Thread.sleep;

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
        for (int i = 0; i < numofbuses; i++) {
            System.out.println(ListOfBuses.get(i).getVehicleId()+" "+ ListOfBuses.get(i).getRouteCode());
        }
        System.out.println("sync done");
        try {
            connectionSocket = new Socket(InetAddress.getByName("127.0.0.1"), port); //initialising client
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            in = new ObjectInputStream(connectionSocket.getInputStream());
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
        return new Broker(null,null);
    }

    public void push(Topic t, Value v) {
        try {
            out.writeObject(t);
            out.flush();
//            out.writeObject(new Publisher(this.brokers));
//            out.flush();
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
           try {
               sleep(50);
               connect();

               push(new Topic(busLine), null);
               while (true) {
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
    }
}