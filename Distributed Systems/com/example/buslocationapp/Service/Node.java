package com.example.buslocationapp.Service;

import java.util.ArrayList;
import java.util.List;

public interface Node {
    List<Broker> brokers = new ArrayList<Broker>();
    String PATH = "../dataset/";
    void init(int port);

    void connect();

    void disconnect();

    void updateNodes();
}
