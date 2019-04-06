import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class example1_Client {

    public static void main(String[] args) {
        new example1_Client().startClient();
    }

    public void startClient() {
        Socket requestSocket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        String message;
        try {
            requestSocket = new Socket(InetAddress.getByName("127.0.0.1"),4321);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            try{
                message = (String) in.readObject();
                System.out.println("Server>" + message);

                out.writeObject("Hi!");
                out.flush();

                out.writeObject("Just Testing..");
                out.flush();

                out.writeObject("bye");
                out.flush();
            } catch (ClassNotFoundException classNot){
                System.err.println("Data recieved in unknown format");
            }
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
