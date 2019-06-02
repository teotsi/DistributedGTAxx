package Service;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

import static java.lang.Thread.sleep;

public class Publisher implements Node, Runnable, Serializable {
    boolean flag = false;
    private Socket connectionSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Topic busLine;
    private String[] busLineInfo;
    private List<Bus> ListOfBuses = new ArrayList<>();
    private String[] Vehicles;
    private InetAddress currentAddress;
    private List<Value> Values = new ArrayList<Value>();
    private List<Map.Entry<String, List<String>>> Keys = new ArrayList<>();// contains all the ips and their keys

    public Publisher(List<Broker> brokers) {
        this.brokers.addAll(Reader.getBrokerList(PATH + "brokerIPs.txt"));
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
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        if (this.Vehicles == null || this.ListOfBuses.isEmpty() || this.Values.isEmpty() || this.busLineInfo == null) {
//            System.out.println("We are sorry, our sensor is down...");
//            notifyFailure(port);
//            System.exit(1);
//        }
        System.out.println("sync done");
        try {
            int randomBroker = new Random().nextInt(3);
            connectionSocket = new Socket(brokers.get(randomBroker).getIpAddress(), port); //connecting to get key info
            this.currentAddress=brokers.get(randomBroker).getIpAddress();
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
        Values.add(new Value(null, 10.0, 10.0));
    }


    @Override
    public void connect() {
        try {
            System.out.println("before finish connect");
            in = new ObjectInputStream(connectionSocket.getInputStream());
            out = new ObjectOutputStream(connectionSocket.getOutputStream());
            System.out.println("finish connect");
        } catch (IOException e) {

        }
    }

    @Override
    public void disconnect() {
        try {
            in.close();
            out.close();
            connectionSocket.close();
        } catch (IOException e) {

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

    private void notifyBrokers() throws UnknownHostException {
        List<String> orphanKeys = null;
        Iterator<Map.Entry<String,List<String>>> it=Keys.iterator();
        while(it.hasNext()){
            Map.Entry<String,List<String>> entry=it.next();
            if(entry.getKey().equals(currentAddress.getHostAddress())){
                orphanKeys=entry.getValue();
                it.remove();
            }
        }
        Keys.get(0).getValue().addAll(orphanKeys);
        System.out.println(Keys.get(0));
        currentAddress=InetAddress.getByName(Keys.get(0).getKey());
        for (Map.Entry<String, List<String>> e : Keys) {
            try {
                System.out.println(e.getKey());
                Socket emergencySocket = new Socket(InetAddress.getByName(e.getKey()), 4321);
                ObjectOutputStream out = new ObjectOutputStream(emergencySocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(emergencySocket.getInputStream());
                out.writeObject(busLine.getBusLine() + "x");
                out.flush();
                in.readObject();
                System.out.println(Keys);
                out.writeObject(Keys);
                out.flush();
                if(e.getKey().equals(currentAddress.getHostAddress())){
                    out.writeObject(true);
                    out.flush();
                    out.writeObject(orphanKeys);
                    out.flush();
                }else{
                    out.writeObject(false);
                    out.flush();
                }
                try {
                    in.close();
                    out.close();
                    emergencySocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

    }


    public void notifyFailure(int port) {
        try {
            int randomBroker = new Random().nextInt(3);
            connectionSocket = new Socket(brokers.get(randomBroker).getIpAddress(), port); //connecting to get key info
            System.out.println("after connection Socket");
            connect();
            out.writeObject(busLine.getBusLine() + "f");
            out.flush();
            in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        init(4321);
        while (true) {
            boolean wrongBroker = true;
            do {
                boolean rightBroker = false;
                boolean notify = false;
                try {
                    connect();
                    out.writeObject(busLine.getBusLine() + "p");
                    out.flush();
                    this.Keys = (List<Map.Entry<String, List<String>>>) in.readObject();
                    rightBroker = (boolean) in.readObject();//reading the message of the broker saying if his is the correct one
                } catch (IOException | ClassNotFoundException e){
                    if(e instanceof EOFException){
                        continue;
                    }

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
                                    try {
                                        notifyBrokers();
                                    } catch (UnknownHostException ex) {
                                        ex.printStackTrace();
                                    }
                                    try {
                                        connectionSocket = new Socket(currentAddress, 4321);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    notify = true;
                                    wrongBroker = true;
                                }
                                break;
                            }
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!notify) {
                        System.out.println("right broker");
                        wrongBroker = false;
                    }
                } else {// if not
                    for (int i = 0; i < 3; i++) {
                        if (Keys.get(i).getValue().contains(busLine.getBusLine())) {
                            try {
                                disconnect();
                                this.currentAddress = InetAddress.getByName(Keys.get(i).getKey());
                                connectionSocket = new Socket(currentAddress, 4321);
                                wrongBroker = true;
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } while (wrongBroker);
            while (true) {
                try {
                    sleep(2000);
                    connectionSocket = new Socket(currentAddress, 4321);

                } catch (IOException e) {
                    try {
                        notifyBrokers();
                        break;
                    } catch (UnknownHostException ex) {
                        ex.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}