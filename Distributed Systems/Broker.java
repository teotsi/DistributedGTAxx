import java.io.*;
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
    private List<Map.Entry<String,List<String>>> OtherKeys=new ArrayList<>();//contains the other keys

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

    public void distributeKeys(){
        final int  MOD = 10; //modulo
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
        for (int i = 0; i < 20; i++) { //applying mod operation to MD5 Hashes
            busLinesHash[i][1] =new BigInteger(busLinesHash[i][1]).mod(BigInteger.valueOf(MOD)).toString();
        }
        for (int i = 0; i < 3; i++) { //same
            ipHashes[i][0] = new BigInteger(ipHashes[i][0]).mod(BigInteger.valueOf(MOD)).toString();
        }

        Arrays.sort(ipHashes, (entry1, entry2) -> { //lambda expression for sorting 2d arrays
            final int hash1 = Integer.parseInt(entry1[0]);
            final int hash2 = Integer.parseInt(entry2[0]);
            return hash1-hash2;
        });
        Arrays.sort(busLinesHash, (entry1, entry2) -> { //same
            final int hash1 = Integer.parseInt(entry1[1]);
            final int hash2 = Integer.parseInt(entry2[1]);
            return hash1 -hash2;
        });
//        for (int i = 0; i < 20; i++) {
//            System.out.println("line " + busLinesHash[i][0] + ", hash=" + busLinesHash[i][1]);
//        }
        System.out.println("ip hashes");
        for (int i = 0; i < 3 ; i++) {
            System.out.println(ipHashes[i][2] + ": " + ipHashes[i][0]);

        }
        int maxIndex; //last element we added. We don't wanna iterate over it again
        for (maxIndex = 0; maxIndex < 20; maxIndex++) { //adding elements to lowest broker
            if(Integer.parseInt(busLinesHash[maxIndex][1])<Integer.parseInt(ipHashes[0][0])){
                ipHashes[0][1]+=busLinesHash[maxIndex][0]+",";
            }else{
                break;
            }
        }
        for (int i = 1; i < 3; i++) {
            for (int j = maxIndex; j < 20; j++) {
                if(Integer.parseInt(busLinesHash[j][1])>=Integer.parseInt(ipHashes[i-1][0])&&
                        Integer.parseInt(busLinesHash[j][1])<Integer.parseInt(ipHashes[i][0])){
                    ipHashes[i][1]+=busLinesHash[j][0]+",";
                }else{
                    if(i==2){
                        ipHashes[0][1]+=busLinesHash[j][0]+",";
                    }else{
                        maxIndex=j;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < 3 ; i++) {
            System.out.println(ipHashes[i][2] + ": " + ipHashes[i][1]);

        }
        for (int i = 0; i <3; i++) {
            StringTokenizer st=new StringTokenizer(ipHashes[i][1]);
            System.out.println(ipAddress.getHostAddress());
            if (ipHashes[i][2].equals(ipAddress.getHostAddress())){
                while(st.hasMoreTokens()){
                    Keys.add(st.nextToken());
                }
            }else{
                Map.Entry<String, List<String>> currentEntry= new AbstractMap.SimpleEntry<String,List<String>>(ipHashes[i][2],new ArrayList<>());
                while(st.hasMoreTokens()){
                    currentEntry.getValue().add(st.nextToken());
                }
                OtherKeys.add(currentEntry);
            }
        }
        System.out.println(Keys);
        System.out.println(OtherKeys.get(0).getKey()+" : "+OtherKeys.get(0).getValue());
        System.out.println(OtherKeys.get(1).getKey()+" : "+OtherKeys.get(1).getValue());
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
        String Hash=big.toString();
//        while(Hash.length()<32){
//            Hash+="0";
//        }
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
            // and linux systems do not share theirs directly
            // via ServerSocket.getInetAddress,
            // so the following thing is necessary
            final DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            System.out.println(socket.getLocalAddress().getHostAddress());
            this.ipAddress=socket.getLocalAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }
        distributeKeys();
    }

    @Override
    public void connect() {
        try {
            connection = providerSocket.accept(); 
            new Thread(new BrokerRequest(connection, Keys)).start();//handling connection into a new thread
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