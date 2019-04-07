import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


public class Broker implements Node, Runnable {

    private static int c = 4321;
    private List<Subscriber> registeredSubscribers;
    private List<Publisher> registeredPublishers;
    private List<Value> Values;

    private InetAddress ipAddress;
    private ServerSocket providerSocket;
    private Socket connection;

    public Broker(List<Broker> brokers, InetAddress ipAddress,boolean flag) {
        this.brokers.addAll(brokers);
        this.registeredSubscribers = new ArrayList<Subscriber>();
        this.registeredPublishers = new ArrayList<Publisher>();
        this.Values=new ArrayList<Value>();
        this.ipAddress = ipAddress;
        if(flag){
            init(4321);
            while(true){
                this.connection = null;
                connect();
            }
        }
    }

    public void calculateKeys() {

    }

    public Publisher acceptConnection(Publisher p) {
        return p;
    }

    public Subscriber acceptConnection(Subscriber s) {
        return s;
    }

    public void notifyPublisher(String message) {
    }

    public void pull(Topic t, ObjectInputStream in) {
        try {
            Topic tr= (Topic) in.readObject();
            Value vr = (Value) in.readObject();
            in.close();
            if(!t.getBusLine().equals(tr.getBusLine())){
                System.out.println("Different topic");
                return;
            }
            System.out.println("Broker no" + Thread.currentThread().getId() + " read");

            System.out.println(vr.getLatitude()+" "+ vr.getLongitude());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    @Override
    public void init(int port) {
        try {
            providerSocket = new ServerSocket(port);
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); //we need to find our local IP
            // and linux systems do not share theirs directly
            // via ServerSocket.getInetAddress,
            // so the following thing is necessary
            boolean flag = false;
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                Enumeration<InetAddress> IPs = ni.getInetAddresses();
                while (IPs.hasMoreElements()) {
                    InetAddress currentIP = IPs.nextElement();
                    if (!currentIP.isLoopbackAddress() && currentIP instanceof Inet4Address) {
                        this.ipAddress = currentIP;
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }
            Broker brokerToRemove = null;
            for (Broker b : brokers) {
                if (b.getIpAddress().equals(this.getIpAddress())) {
                    brokerToRemove = b;
                    break;
                }
            }
            brokers.remove(brokerToRemove);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() {
        try {
            connection = providerSocket.accept();
            new Thread(new BrokerRequest(connection)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void disconnect() {
//        try {
//            in.close();
//            out.close();
//            connection.close();
//            providerSocket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void updateNodes() {

    }

    public void run() {
        System.out.println("New Broker Thread");
        try {
            ObjectOutputStream out = new ObjectOutputStream(connection.getOutputStream());
            System.out.println("after out");
            ObjectInputStream in = new ObjectInputStream(connection.getInputStream());
            pull(new Topic("021"), in);
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}