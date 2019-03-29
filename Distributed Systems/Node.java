import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Node {

    List<Broker> brokers;
    ServerSocket server;
    Socket connectionSocket;

    public Node(List<Broker> list) {
        this.brokers = list;
    }


    public void init(int port) {
        try {
            server = new ServerSocket(port); //initialising server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        while(true){
            try {
                connectionSocket = server.accept(); //accepting connection
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
    }

    public void updateNodes() {
    }
}