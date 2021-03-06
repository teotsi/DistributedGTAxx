package Service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Thread.sleep;

public class BrokerRequest extends Broker implements Runnable {
    private static List<Map.Entry<Topic, CopyOnWriteArrayList<Value>>> Buffer;
    private static List<String> BrokenKeys = new ArrayList<>();
    private int port;
    private Socket connectionSocket;
    private List<String> Keys;

    public BrokerRequest(Socket socket, List<String> Keys, List<Map.Entry<String, List<String>>> AllKeys, List<Map.Entry<Topic, CopyOnWriteArrayList<Value>>> Buffer) {
        this.connectionSocket = socket;
        this.Keys = Keys;
        super.AllKeys = AllKeys;
        BrokerRequest.Buffer = Buffer;
    }

    public synchronized boolean pull(ObjectInputStream in) {
        try {
            Topic tr = (Topic) in.readObject();
            Value vr = (Value) in.readObject();
            if (vr.getLongitude() == 10.0) {
                System.out.println("found null");
                Broker.addToBuffer(tr, vr);
                return false;
            }
            Broker.addToBuffer(tr, vr);
            System.out.println("Broker no" + Thread.currentThread().getId() + " read");
            System.out.println(vr.getLatitude() + " " + vr.getLongitude());
        } catch (IOException e) {
            if (e.getMessage().contains("Connection reset")) {
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
        try {
            System.out.println("New Request THREADDDD");
            ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());
            System.out.println("after out");
            ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
            String message = (String) in.readObject();
            out.writeObject(AllKeys);
            out.flush();
            if (message.contains("p")) {
                System.out.println(message.substring(0, message.length() - 1));
                if (Keys.contains(message.substring(0, message.length() - 1))) {
                    out.writeObject(true);
                    out.flush();
                    boolean bool;
                    do {
                        bool = pull(in);
                    } while (bool);
                } else {
                    out.writeObject(false);
                    out.flush();
                }
            } else if (message.contains("f")) {//sensor failure
                System.out.println("Bus line " + message.substring(0, message.length() - 1) + " is down...");
                BrokenKeys.add(message.substring(0, message.length() - 1));
            } else if (message.contains("x")) {//broker failure
                AllKeys= (List<Map.Entry<String, List<String>>>) in.readObject();
                System.out.println(AllKeys);
                System.out.println(Keys);
                if((boolean)in.readObject()){
                    Keys.addAll((List<String>)in.readObject());
                }
                System.out.println(Keys);
            } else {//push to sub
                System.out.println("sending to sub");
                StringTokenizer tk = new StringTokenizer(message,",");
                String key = tk.nextToken();
                Topic topic = new Topic(key);
                String route = tk.nextToken();
                if (!BrokenKeys.contains(key)) {
                    if (Keys.contains(key)) {
                        out.writeObject(0);
                        out.flush();
                        for (Map.Entry<Topic, CopyOnWriteArrayList<Value>> e : Buffer) {
                            if (e.getKey().equals(topic)) {
                                int i = 0;
                                List<Value> v1 = e.getValue();
                                while (true) {
                                    try {
                                        Value currentValue = v1.get(i);
                                        if(currentValue.getBus().getRouteCode().equals(route)) {
                                            System.out.println("Sent to customer!");
                                            out.writeObject(currentValue);
                                            out.flush();
                                        }
                                        if (currentValue.getLongitude() == 10.0) {
                                            System.out.println("found null");
                                            break;
                                        }
                                        sleep(50);
                                        i++;
                                    } catch (IndexOutOfBoundsException e1) {
                                        System.out.println("exception e1");
                                        sleep(1000);
                                    } catch (SocketException e2) {
                                        System.out.println("Subscriber is down!");
                                        break;
                                    }
                                }
                            }

                        }
                    } else {
                        out.writeObject(1);
                        out.flush();
                    }
                } else {
                    out.writeObject(2);
                }


            }

            in.close();
            out.close();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            if (e instanceof SocketException) {
                System.out.println("Socket closed");
            } else {
                e.printStackTrace();
            }
        }
    }
}
