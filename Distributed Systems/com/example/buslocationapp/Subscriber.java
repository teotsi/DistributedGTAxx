package com.example.buslocationapp;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
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
    private int count=0;
    private int direction;

    public Subscriber(List<Broker> brokers) {
        this.brokers.addAll(Reader.getBrokerList(PATH + "brokerIPs.txt"));
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.print("Enter bus line:");
            String input = in.next().trim();
            while (input.length() > 3) {
                System.out.print("\nInvalid bus! Retry: ");
                input = in.next();
            }
            this.currentLine = input;
            this.direction = 1804;
            init(4321);
            connect();
            count=0;
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
                count++;
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
        System.out.println("New position! " + currentLine + " is at " + v.getLatitude() + ", " + v.getLongitude()+"Rouuute:"+v.getBus().getRouteCode());
    }

    @Override
    public void init(int port) {
        while(true) {
            try {
                int randomBroker = new Random().nextInt(3);
                socket = new Socket(brokers.get(randomBroker).getIpAddress(), port); //connecting to get key info
                break;
            } catch (IOException e) {
                System.out.println("Subscriber tried again");
            }
        }
    }

    @Override
    public void connect() {
        boolean wrongBroker=true;
        do {
            try {
                System.out.println("before in out");
                sleep(1000);
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(this.currentLine+","+direction); //asking broker for list + if he's responsible for this key
                out.flush();
                AllKeys = (List<Map.Entry<String, List<String>>>) in.readObject();//reading the message of the broker saying if his is the correct one
                int hasKey = (int) in.readObject();
                if (hasKey==0) {
                    boolean bool;
                    do {
                        bool = pull(in);
                        if(count>5){
                            System.out.println("Broker is down, please insert line again.");
                            count=0;
                            return;
                        }
                    } while (bool);
                    System.out.println("No more location data for this bus!");
                    disconnect();
                    wrongBroker=false;
                } else if(hasKey==1) {
                    System.out.println("other broker");
                    System.out.println(AllKeys);
                    for (int i = 0; i < AllKeys.size(); i++) {
                        if (AllKeys.get(i).getValue().contains(currentLine)) {
                            try {
                                System.out.println("Socket before Made!");
                                sleep(1000);
                                disconnect();
                                currentAddress = InetAddress.getByName(AllKeys.get(i).getKey());
                                socket = new Socket(currentAddress, 4321);
                                System.out.println("Socket after Made!");
                                sleep(1000);
                                wrongBroker = true;
                                break;
                            } catch (ConnectException e){
                                currentAddress = InetAddress.getByName(AllKeys.get(i).getKey());
                                System.out.println(currentAddress);
                                socket = new Socket(currentAddress, 4321);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    System.out.println("Bus line "+ currentLine + " is down...");
                }
            } catch (IOException e) {
                System.out.println("Broker down...");
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
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