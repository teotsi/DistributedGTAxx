import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class BrokerRequest implements Runnable{
    private int port;
    private Socket connectionSocket;
    private List<String> Keys;
    public BrokerRequest(Socket socket, List<String> Keys){
        this.connectionSocket= socket;
        this.Keys=Keys;
    }

    public synchronized boolean pull(Topic t, ObjectInputStream in) {
        try {
            Topic tr= (Topic) in.readObject();
            Value vr = (Value) in.readObject();
            if(vr==null){
                System.out.println("found null");
                return false;
            }
            if(!t.getBusLine().equals(tr.getBusLine())){
                System.out.println("Different topic");
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
        if (message.contains("p")){
            if(Keys.contains(message.substring(message.length()-1))){

            }
        }else{

        }
        boolean bool;
        do{
           bool=pull(new Topic("021"), in);
        }while(bool);
        in.close();
        out.close();
    }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
