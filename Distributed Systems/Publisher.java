import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Thread.sleep;

public class Publisher implements Node, Runnable, Serializable {
    private Socket connectionSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Topic busLine;
    private String[] busLineInfo;
    private List<Bus> ListOfBuses = new ArrayList<>();
    private String[] Vehicles;
    private List<Value> Values = new ArrayList<Value>();
    private List<Map.Entry<String,List<String>>> Keys = new ArrayList<>();// contains all the ips and their keys

    public Publisher(List<Broker> brokers) {
        this.brokers.addAll(Reader.getBrokerList(PATH+"brokerIPs.txt"));
    }

    @Override
    public void init(int port) {
        System.out.println("sync starts");
        this.busLineInfo = Reader.getBus();
        this.busLine = new Topic(this.busLineInfo[1]);
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

    private void createValues() {
        List<String[]> table = Reader.getPositionTable();
        for (String[] line : table) {
            for (Bus b : ListOfBuses) {
                if (line[0].trim().equals(b.getLineNumber()) && line[2].trim().equals(b.getVehicleId())) {
                    Values.add(new Value(b, Double.parseDouble(line[3]), Double.parseDouble(line[4])));
                }

            }
        }
        Values.add(new Value(null,10.0,10.0));
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


    private void push(Topic t, Value v) throws IOException {
        out.writeObject(t);
        out.flush();
        out.writeObject(v);
        out.flush();
        System.out.println("Publisher no" + Thread.currentThread().getId() + " pushed");

    }

    private void notifyBrokers() {
        List<String> orphanKeys = null;
        for(Map.Entry<String, List<String>> e: Keys){
            if(e.getKey().equals(connectionSocket.getLocalAddress().getHostAddress())){
                orphanKeys = e.getValue();
                continue;
            }
            try {
                Socket emergencySocket = new Socket(InetAddress.getByName(e.getKey()), 4322);
                ObjectOutputStream out = new ObjectOutputStream(emergencySocket.getOutputStream());
                out.writeObject(orphanKeys);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
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
                this.Keys = (List<Map.Entry<String, List<String>>>) in.readObject();
                rightBroker = (boolean) in.readObject();//reading the message of the broker saying if his is the correct one
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("before push");
            if (rightBroker) {//if he is in the right broker
                for (Value v : Values) {
                    try {
                        push(busLine, v);
                        sleep(50);
                    } catch (IOException e) {
                        if (e instanceof SocketException) {
                            if (e.getMessage().contains("Connection reset")) {
                                System.out.println("Connection reset, broker may be down.");
                                notifyBrokers();
                            }
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