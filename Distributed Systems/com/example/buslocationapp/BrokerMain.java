package com.example.buslocationapp;

import java.io.IOException;
import java.net.InetAddress;

public class BrokerMain {
    public static void main(String[] args) {
        try {
            Broker currentServer = new Broker(Reader.getBrokerList("brokerIPs.txt"),"busLinesNew.txt", InetAddress.getLocalHost(), true);
            while(true){
                currentServer.connect();
            }
            //currentServer.init(4321);
        } catch (IOException e) {
            System.out.println("Error with server. Is the port available?");
            System.exit(1);
        }
    }
}
