import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.sleep;

public class BrokerRequest implements Runnable{
    private int port;
    private Socket connectionSocket;
    private List<String> Keys;
    private List<Map.Entry<String,List<String>>> AllKeys;
    private static List<Map.Entry<Topic, CopyOnWriteArrayList<Value>>> Buffer;

    public BrokerRequest(Socket socket, List<String> Keys, List<Map.Entry<String,List<String>>> AllKeys, List<Map.Entry<Topic, CopyOnWriteArrayList<Value>>> Buffer){
        this.connectionSocket= socket;
        this.Keys=Keys;
        this.AllKeys=AllKeys;
        this.Buffer=Buffer;
    }

    public synchronized boolean pull( ObjectInputStream in) {
        try {
            Topic tr= (Topic) in.readObject();
            Value vr = (Value) in.readObject();
            if(vr.getLongitude()==10.0){
                System.out.println("found null");
                Broker.addToBuffer(tr, vr);
                return false;
            }
            Broker.addToBuffer(tr,vr);
            System.out.println("Broker no" + Thread.currentThread().getId() + " read");
            System.out.println(vr.getLatitude()+" "+ vr.getLongitude());
        } catch (IOException e) {
           if(e.getMessage().contains("Connection reset")){
               System.out.println("Connection reset. Publisher may be down.");
               return false;
           }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }
    @Override
    public void run() {
        try{
            System.out.println("New Request THREADDDD");
        ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
        System.out.println("after out");
        ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
        String message=(String)in.readObject();
        out.writeObject(AllKeys);
        out.flush();
        if (message.contains("p")){
            System.out.println(message.substring(0,message.length()-1));
            if(Keys.contains(message.substring(0,message.length()-1))){
                out.writeObject(true);
                out.flush();
                boolean bool;
                do{
                    bool=pull(in);
                }while(bool);
            }else {
                out.writeObject(false);
                out.flush();
            }
        }else{
            System.out.println("sending to sub");
            Topic topic=new Topic(message);
            if(Keys.contains(message)){
                out.writeObject(true);
                out.flush();
                    for (Map.Entry<Topic, CopyOnWriteArrayList<Value>> e : Buffer) {
                        if (e.getKey().equals(topic)) {
                            for (Value v1 : e.getValue()) {
                                out.writeObject(v1);
                                out.flush();
                                if (v1.getLongitude()==10.0) {
                                    System.out.println("found null");
                                }
//                            if(v==null){
//                                System.out.println("v is null");
//                            }
                                sleep(100);
                            }
                        }

                    }
            }else{
                out.writeObject(false);
                out.flush();
            }



        }

        in.close();
        out.close();
    }catch(IOException | ClassNotFoundException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
