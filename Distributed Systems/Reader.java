import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Reader {
    private static List<String> busLines;
    private static int busCount = 0;
    private static int totalBusLines;
    private static int ipCount = 0;
    private static List<String> ipLines;
    private static int totalIpLines;
    public Reader(String busLinesfileName, String ipFileName) {
        Scanner busInput, ipInput;
        File busFile, ipFile;
        try {
            busFile = new File(busLinesfileName);
            busInput = new Scanner(busFile);
            ipFile = new File(ipFileName);
            ipInput = new Scanner(ipFile);
        } catch (FileNotFoundException e) {
            System.out.println("No such file!");
            return;
        }
        busLines = new ArrayList<String>();
        while (busInput.hasNext()) {
            busLines.add(busInput.nextLine());
        }
        totalBusLines = busLines.size();
        while(ipInput.hasNext()){
            ipLines.add(ipInput.nextLine());
        }
        totalIpLines = ipLines.size();
    }

    public static String getBus() {
        StringTokenizer st;
            st = new StringTokenizer(busLines.get(busCount++), ",");
//        System.out.println("after busCount++");
        st.nextToken();
        return st.nextToken();
    }

    public static boolean moreBuses() {
//        System.out.println(busCount);
        return busCount < totalBusLines;
    }
    
    public static String getIP(){
        return ipLines.get(ipCount++);
    }
    public static void main(String[] args) {
        new Reader("..\\dataset\\busLinesNew.txt", null);
        System.out.println(Reader.getBus());
        Reader.getBus();
        System.out.println(Reader.getBus());
    }
}
