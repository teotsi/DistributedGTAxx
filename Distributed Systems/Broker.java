import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class Broker implements Node, Runnable {

    private static int p = 4321; //port
    private List<Subscriber> registeredSubscribers;
    private List<Publisher> registeredPublishers;
    private static List<Map.Entry<Topic,List<Value>>> Buffer=new ArrayList<Map.Entry<Topic, List<Value>>>(); //contains all the buses from busPositionNew.txt that belongs to the broker.
    private static String[][] Hashes=new String[3][2]; //contains all broker IPs and their md5 hashes 
    private static String[][] IDHashes;
    private static List<String> Keys=new ArrayList<String>(); //contains all keys current broker is responsible for

    private InetAddress ipAddress;
    private ServerSocket providerSocket;
    private Socket connection;
    private String busLinesFileName;

    public Broker(List<Broker> brokers,String busLinesFileName, InetAddress ipAddress,boolean flag) {
        this.brokers.addAll(brokers);
        this.registeredSubscribers = new ArrayList<Subscriber>();
        this.registeredPublishers = new ArrayList<Publisher>();
        this.ipAddress = ipAddress;
        if(flag){
            this.busLinesFileName=busLinesFileName;
            init(p);
            for (int i = 0; i <3 ; i++) {
                Hashes[i][0]=Reader.getIPs().get(i); //storing broker IPs
            }
            for (int i = 0; i <2 ; i++) {
                Hashes[i][1]= calculateHash(Hashes[i][0]+p); //hashing ip+port of each broker and storing it
            }
            IDHashes=calculateKeys();
            System.out.println(IDHashes[3][1]);
            System.out.println(Hashes[0][1]);
            while(true){
                this.connection = null;
                connect();
            }
        }

    }

    public String calculateHash(String message) {//hashing function
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5"); //using MD5 algorithm
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update((message).getBytes());
        byte[] md=md5.digest();
        BigInteger big= new BigInteger(1,md);
        String Hash=big.toString(16);
        while(Hash.length()<32){
            Hash+="0";
        }
        return Hash;
    }

    public String[][] calculateKeys(){
        String[][] idhases=new String[Reader.IDs(busLinesFileName).size()][2];
        for (int i = 0; i <Reader.IDs(busLinesFileName).size() ; i++) {
            idhases[i][0]=Reader.IDs(busLinesFileName).get(i);
            idhases[i][1]=calculateHash(idhases[i][0]);
        }
        return idhases;
    }

    public static void addToBuffer(Topic t, Value v){
        boolean flag=true;
        for(Map.Entry<Topic,List<Value>> e: Buffer){
            if(e.getKey().equals(t)) {
                e.getValue().add(v);
                flag=false;
            }
        }
        if(flag){
            Map.Entry<Topic,List<Value>> entry=new AbstractMap.SimpleEntry<Topic, List<Value>>(t,new ArrayList<Value>());
            entry.getValue().add(v);
            Buffer.add(entry);
        }

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
            Topic tr= (Topic) in.readObject();   //reading Topic from publisher's push 
            Value vr = (Value) in.readObject(); //reading Value from publisher's push
            in.close(); //closing stream
            if(!t.equals(tr)){  //in case we receive the wrong Topic
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

    public InetAddress getIpAddress() { //returns current broker's IP
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
            while (e.hasMoreElements()) { //scanning all network Interface
                NetworkInterface ni = e.nextElement(); 
                Enumeration<InetAddress> IPs = ni.getInetAddresses();
                while (IPs.hasMoreElements()) {
                    InetAddress currentIP = IPs.nextElement();
                    if (!currentIP.isLoopbackAddress() && currentIP instanceof Inet4Address) { //the first non-loopback IPv4 address is the one we need
                        this.ipAddress = currentIP;
                        flag = true;
                        break;
                    }
                }
                if (flag) break;
            }
            Broker brokerToRemove = null; //broker removes itself from his list
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
            new Thread(new BrokerRequest(connection)).start();//handling connection into a new thread
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