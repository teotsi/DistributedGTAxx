package Service;



import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Broker implements Node {
    final int MOD = 10; //modulo
    private static int p = 4321; //port
    private List<Subscriber> registeredSubscribers;
    private List<Publisher> registeredPublishers;
    private static CopyOnWriteArrayList<Map.Entry<Topic, CopyOnWriteArrayList<Value>>> Buffer = new CopyOnWriteArrayList<>(); //contains all the buses from busPositionNew.txt that belongs to the broker.
    private static String[][] Hashes = new String[3][2]; //contains all broker IPs and their md5 hashes
    private static String[][] IDHashes;
    private static List<String> Keys = new ArrayList<>(); //contains all keys current broker is responsible for
    protected static List<Map.Entry<String, ArrayList<String>>> AllKeys = new ArrayList<>();//contains all the keys

    private InetAddress ipAddress;
    private ServerSocket providerSocket, emergencyServer;
    private Socket connection;
    private String busLinesFileName;

    public Broker(List<Broker> brokers, String busLinesFileName, InetAddress ipAddress, boolean flag) {
        this.brokers.addAll(brokers);
        this.registeredSubscribers = new ArrayList<Subscriber>();
        this.registeredPublishers = new ArrayList<Publisher>();
        this.ipAddress = ipAddress;
        if (flag) {
            this.busLinesFileName = busLinesFileName;
            init(p);
            for (int i = 0; i < 3; i++) {
                Hashes[i][0] = Reader.getIPs().get(i); //storing broker IPs
            }
            for (int i = 0; i < 2; i++) {
                Hashes[i][1] = calculateHash(Hashes[i][0] + p); //hashing ip+port of each broker and storing it
            }
            IDHashes = calculateKeys();
            System.out.println(IDHashes[3][1]);
            System.out.println(Hashes[0][1]);
            while (true) {
                this.connection = null;
                connect();
            }
        }

    }

    public Broker(){

    }
    private void modMD5(String[][] array, int size, int column){ //applying mod operation to MD5 hashes
        for (int i = 0; i < size; i++) {
            array[i][column] = new BigInteger(array[i][column]).mod(BigInteger.valueOf(MOD)).toString();
        }
    }

    public void distributeKeys() {
        new Reader("busLinesNew.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        String[][] busLinesHash = new String[20][2];
        for (int i = 0; i < 20; i++) {
            busLinesHash[i][0] = Reader.getBus()[1];
            busLinesHash[i][1] = calculateHash(busLinesHash[i][0]);
        }
        Reader.getBrokerList("brokerIPs.txt");
        List<String> ips = Reader.getIPs();
        String[][] ipHashes = new String[3][3];
        for (int j = 0; j < 3; j++) {
            ipHashes[j][0] = calculateHash(ips.get(j) + "4321");
            ipHashes[j][1] = "";
            ipHashes[j][2] = ips.get(j);
        }
        modMD5(busLinesHash, 20, 1); //getting mod value of MD5 hash
        modMD5(ipHashes, 3, 0);
        Reader.sort2D(ipHashes,0); //sorting IPs by hash
        Reader.sort2D(busLinesHash, 1); //sorting bus Lines by hash
        System.out.println("ip hashes");
        int maxIndex; //last element we added. We don't wanna iterate over it again
        for (maxIndex = 0; maxIndex < 20; maxIndex++) { //adding elements to lowest broker
            if (Integer.parseInt(busLinesHash[maxIndex][1]) < Integer.parseInt(ipHashes[0][0])) {
                ipHashes[0][1] += busLinesHash[maxIndex][0] + ",";
            } else {
                break;
            }
        }
        for (int i = 1; i < 3; i++) {
            for (int j = maxIndex; j < 20; j++) {
                if (Integer.parseInt(busLinesHash[j][1]) < Integer.parseInt(ipHashes[i][0])) {
                    ipHashes[i][1] += busLinesHash[j][0] + ",";
                } else {
                    if (i == 2) {
                        ipHashes[0][1] += busLinesHash[j][0] + ",";
                    } else {
                        maxIndex = j;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            System.out.println(ipHashes[i][2] + ": " + ipHashes[i][1]);

        }
        for (int i = 0; i < 3; i++) {
            StringTokenizer st = new StringTokenizer(ipHashes[i][1], ",");
            System.out.println(ipAddress.getHostAddress());
            if (ipHashes[i][2].equals(ipAddress.getHostAddress())) {
                Map.Entry<String, ArrayList<String>> currentEntry = new AbstractMap.SimpleEntry<String, ArrayList<String>>(ipHashes[i][2], new ArrayList<String>());
                while (st.hasMoreTokens()) {
                    String t = st.nextToken();
                    Keys.add(t);
                    currentEntry.getValue().add(t);
                }
                AllKeys.add(currentEntry);
            } else {
                Map.Entry<String, ArrayList<String>> currentEntry = new AbstractMap.SimpleEntry<String, ArrayList<String>>(ipHashes[i][2], new ArrayList<String>());
                while (st.hasMoreTokens()) {
                    currentEntry.getValue().add(st.nextToken());
                }
                AllKeys.add(currentEntry);
            }
        }
        System.out.println(Keys);
        System.out.println(AllKeys.get(0).getKey() + " : " + AllKeys.get(0).getValue());
        System.out.println(AllKeys.get(1).getKey() + " : " + AllKeys.get(1).getValue());
    }


    private String calculateHash(String message) {//hashing function
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5"); //using MD5 algorithm
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md5 != null;
        md5.update((message).getBytes());
        byte[] md = md5.digest();
        BigInteger big = new BigInteger(1, md);
        //        while(Hash.length()<32){
//            Hash+="0";
//        }
        return big.toString();
    }

    private String[][] calculateKeys() {
        String[][] idHashes = new String[Reader.IDs(busLinesFileName).size()][2];
        for (int i = 0; i < Reader.IDs(busLinesFileName).size(); i++) {
            idHashes[i][0] = Reader.IDs(busLinesFileName).get(i);
            idHashes[i][1] = calculateHash(idHashes[i][0]);
        }
        return idHashes;
    }

    static void addToBuffer(Topic t, Value v) {
        boolean flag = true;
        for (Map.Entry<Topic, CopyOnWriteArrayList<Value>> e : Buffer) {
            if (e.getKey().equals(t)) {
                e.getValue().add(v);
                flag = false;
            }
        }
        if (flag) {
            Map.Entry<Topic, CopyOnWriteArrayList<Value>> entry = new AbstractMap.SimpleEntry<>(t, new CopyOnWriteArrayList<Value>());
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

     InetAddress getIpAddress() { //returns current broker's IP
        return ipAddress;
    }

    @Override
    public void init(int port) {
        try {
            providerSocket = new ServerSocket(port);
            emergencyServer = new ServerSocket(4322);
            // and linux systems do not share theirs directly
            // via ServerSocket.getInetAddress,
            // so the following thing is necessary
            final DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            System.out.println(socket.getLocalAddress().getHostAddress());
            this.ipAddress = socket.getLocalAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        distributeKeys();
    }

    @Override
    public void connect() {
        try {
            connection = providerSocket.accept();
            //new Thread(new BrokerRequest(connection, Keys, AllKeys, Buffer)).start();//handling connection into a new thread
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
}