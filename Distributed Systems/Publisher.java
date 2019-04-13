import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Publisher implements Node, Runnable, Serializable {
    Socket connectionSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    Topic busLine;
    String[] busLineInfo;
    List<Bus> ListOfBuses = new ArrayList<Bus>();
    String[] Vehicles;
    List<Value> Values = new ArrayList<Value>();
    List<Map.Entry<String,List<String>>> Keys = new ArrayList<>();// contains all the ips and their keys

    public Publisher(List<Broker> brokers) {
        this.brokers.addAll(Reader.getBrokerList(PATH+"brokerIPs.txt"));
    }

    @Override
    public void init(int port) {
        System.out.println("sync starts");
        this.busLineInfo = Reader.getBus();
        this.busLine = new Topic(this.busLineInfo[1]);
        Reader.createBusesMap();// TODO na mpei sthn main
        Reader.createRoutesNinfo();//TODO na mpei sthn main
        int numofbuses = Reader.getNumberOfBuses(busLineInfo[0]);
        this.Vehicles = Reader.getVehicles(busLineInfo[0], numofbuses);
        for (int i = 0; i < numofbuses; i++) {
            ListOfBuses.add(new Bus(busLineInfo[0], Reader.getRouteCode(this.Vehicles[i]), this.Vehicles[i], busLineInfo[2], busLineInfo[1], Reader.getInfo(Reader.getRouteCode(this.Vehicles[i]))));
        }
        createValues();
        System.out.println("sync done");
        try {
            int randomBroker = new Random().nextInt(3);
            connectionSocket = new Socket(brokers.get(randomBroker).getIpAddress(),port); //connecting to get key info
            System.out.println("after connection Socket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createValues() { //TODO na to trexei ka8e publisher
        List<String[]> table = Reader.getPositionTable();
        for (String[] line : table) {
            for (Bus b : ListOfBuses) {
                if (line[0].trim().equals(b.getLineNumber()) && line[2].trim().equals(b.getVehicleId())) {
                    Values.add(new Value(b, Double.parseDouble(line[3]), Double.parseDouble(line[4])));
                }

            }
        }
        Values.add(null);
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
            in.close();
            out.close();
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
        return new Broker(null,null, null, true);
    }

    public void push(Topic t, Value v) throws IOException {
        out.writeObject(t);
        out.flush();
        out.writeObject(v);
        out.flush();
        System.out.println("Publisher no" + Thread.currentThread().getId() + " pushed");

    }


    public void notifyFailure(Broker b) {
    }

    @Override
    public void run() {
        init(4321);
        boolean wrongBroker=true;
        do {
            connect();
            boolean rightBroker = false;
            try {
                out.writeObject(busLine.getBusLine() + "p");
                out.flush();
                List<Map.Entry<String, List<String>>> AllKeys = (List<Map.Entry<String, List<String>>>) in.readObject();
                this.Keys = AllKeys;
                rightBroker = (boolean) in.readObject();//reading the message of the broker saying if his is the correct one
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("before push");
            if (rightBroker) {//if he is in the right broker
                for (Value v : Values) {
                    try {
                        push(busLine, v);
                        sleep(100);
                    } catch (IOException e) {
                        if (e instanceof SocketException) {
                            if (e.getMessage().contains("Connection reset")) {
                                System.out.println("Connection reset, broker may be down.");
                            }
                            wrongBroker=true;
                            break;
                        }
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                wrongBroker=false;
            } else {// if not
                for (int i = 0; i < 3; i++) {
                    System.out.println("wtf");
                    System.out.println(Keys.get(i));
                    if (Keys.get(i).getValue().contains(busLine.getBusLine())) {
                        try {
                            disconnect();
                            connectionSocket = new Socket(InetAddress.getByName(Keys.get(i).getKey()), 4321);
                            wrongBroker=true;
                            break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }while(wrongBroker);
        while (true) {
        }
    }
}