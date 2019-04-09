import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BrokerRequest implements Runnable{
    private int port;
    private Socket connectionSocket;
    public BrokerRequest(Socket socket){
        this.connectionSocket= socket;
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
            System.out.println("Broker no" + Thread.currentThread().getId() + " read");
            System.out.println(vr.getLatitude()+" "+ vr.getLongitude());
        } catch (IOException e) {
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
        boolean bool;
        do{
           bool=pull(new Topic("021"), in);
        }while(bool);
        in.close();
        out.close();
    }catch(IOException e){
            e.printStackTrace();
        }
    }
}
