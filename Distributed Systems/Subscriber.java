import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Subscriber implements Node {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    String currentLine;
    private List<Map.Entry<String, List<String>>> AllKeys;// contains all the ips and their keys
    private InetAddress currentAddress;
    public Subscriber(List<Broker> brokers) {
        this.brokers.addAll(Reader.getBrokerList(PATH + "brokerIPs.txt"));
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("Enter bus line:");
            String input = in.next().trim();
            while (input.length() != 3) {
                System.out.print("\nInvalid bus! Retry: ");
                input = in.next();
            }
            this.currentLine = input;
            init(4321);
            connect();
        }
    }

    public synchronized boolean pull(ObjectInputStream in) {
        try {
            Value vr = (Value) in.readObject();
            System.out.println(vr);
            if (vr.getLongitude()==10.0) {
                System.out.println("found null");
                return false;
            }
            visualiseData(vr);
        } catch (EOFException e){
            try {
                System.out.println("sleep");
                sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            if(e.getMessage().contains("Connection reset")){
                System.out.println("Connection reset. Broker may be down.");
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

    public void visualiseData( Value v) {
        System.out.println("New position! " + currentLine + " is at " + v.getLatitude() + ", " + v.getLongitude());
    }

    @Override
    public void init(int port) {
        try {
            int randomBroker = new Random().nextInt(3);
            socket = new Socket(brokers.get(randomBroker).getIpAddress(), port); //connecting to get key info
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        boolean wrongBroker=true;
        do {
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(this.currentLine); //asking broker for list + if he's responsible for this key
                out.flush();
                AllKeys = (List<Map.Entry<String, List<String>>>) in.readObject();//reading the message of the broker saying if his is the correct one
                int hasKey = (int) in.readObject();
                if (hasKey==0) {
                    boolean bool;
                    do {
                        bool = pull(in);
                    } while (bool);
                    System.out.println("No more location data for this bus!");
                    disconnect();
                    wrongBroker=false;
                } else if(hasKey==1) {
                    System.out.println("other broker");
                    for (int i = 0; i < 3; i++) {
                        if (AllKeys.get(i).getValue().contains(currentLine)) {
                            try {
                                disconnect();
                                currentAddress = InetAddress.getByName(AllKeys.get(i).getKey());
                                socket = new Socket(currentAddress, 4321);
                                wrongBroker = true;
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    System.out.println("Bus line "+ currentLine + " is down...");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }while(wrongBroker);
    }

    @Override
    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateNodes() {

    }
}