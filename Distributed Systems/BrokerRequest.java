import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class BrokerRequest implements Runnable{
    private int port;
    private Socket connectionSocket;
    private List<String> Keys;
    List<Map.Entry<String,List<String>>> AllKeys;
    public BrokerRequest(Socket socket, List<String> Keys, List<Map.Entry<String,List<String>>> AllKeys){
        this.connectionSocket= socket;
        this.Keys=Keys;
        this.AllKeys=AllKeys;
    }

    public synchronized boolean pull( ObjectInputStream in) {
        try {
            Topic tr= (Topic) in.readObject();
            Value vr = (Value) in.readObject();
            if(vr==null){
                System.out.println("found null");
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
            out.writeObject(AllKeys);
            out.flush();
            System.out.println(message.substring(message.length()-1));
            if(Keys.contains(message.substring(message.length()-1))){
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
            System.out.println("not p");


        }

        in.close();
        out.close();
    }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
