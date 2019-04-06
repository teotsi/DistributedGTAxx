import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;
import java.util.Scanner;

public class SubThreads {
    public static void main(String[] args) {
        try {
            ServerSocket g = new ServerSocket(4321, 5, InetAddress.getByName("127.255.255."+new Random().nextInt(255)));
            while(true){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
