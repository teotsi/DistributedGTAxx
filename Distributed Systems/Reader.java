import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Reader {
    private static List<String> busLines;
    private static int busCount = 0;
    private static int totalBusLines;
    private static int ipCount = 0;
    private static List<String> ipLines;
    private static List<String> positionLines;
    private static List<String> routeLines;
    private static Set<Map.Entry<String, String>> LinesNbuses = new HashSet<>();
    private static Set<Map.Entry<String, String>> BusesNroutes = new HashSet<>();
    private static Set<Map.Entry<String, String>> RoutesNinfo = new HashSet<>();
    private static final String PATH = "../dataset/";
    private static List<String[]> PositionTable=new ArrayList<>();

    public Reader(String busLinesFileName, String ipFileName, String busPositionsFileName, String routeCodesFileName) {
        busLines = getFileLines(busLinesFileName);
        totalBusLines = busLines.size();
        positionLines = getFileLines(busPositionsFileName);
        routeLines = getFileLines(routeCodesFileName);

    }

    public static String[] getBus() {
        StringTokenizer st;
        st = new StringTokenizer(busLines.get(busCount++), ",");
        String[] busLineInfo = {st.nextToken(), st.nextToken(), st.nextToken()};
        return busLineInfo;
    }

    public static String[] getVehicles(String LineCode, int count) {
        String[] Vehicles = new String[count];
        int i = 0;
        for (Map.Entry<String, String> e : LinesNbuses) {
            if (e.getKey().equals(LineCode)) {
                Vehicles[i] = e.getValue();
                i++;
            }
        }
        return Vehicles;

    }

    public static List<String[]> getPositionTable(){
        return PositionTable;
    }

    public static void createRoutesNinfo() {
        for (int i = 0; i < routeLines.size(); i++) {
            StringTokenizer st;
            st = new StringTokenizer(routeLines.get(i), ",");
            String code = st.nextToken();
            st.nextToken();
            st.nextToken();
            String info = st.nextToken();
            RoutesNinfo.add(new AbstractMap.SimpleEntry<>(code, info.trim()));
        }
    }

    public static String getInfo(String RouteCode) {
        for (Map.Entry<String, String> e : RoutesNinfo) {
            if (e.getKey().equals(RouteCode)) {
                return e.getValue();
            }
        }
        return "error";
    }

    public static void createBusesMap() {
        for (int i = 0; i < positionLines.size(); i++) {
            StringTokenizer st;
            st = new StringTokenizer(positionLines.get(i), ",");
            String code = st.nextToken();
            String route = st.nextToken();
            String vehicleId = st.nextToken();
            BusesNroutes.add(new AbstractMap.SimpleEntry<>(vehicleId.trim(), route.trim()));
            LinesNbuses.add(new AbstractMap.SimpleEntry<>(code, vehicleId.trim()));

        }
    }

    public static void createPositionTable(){
        for (String line:positionLines) {
            String[] lineinfo= line.split(",");
            PositionTable.add(lineinfo);
        }

    }

    public static int getNumberOfBuses(String LineCode) {
        int c = 0;
        for (Map.Entry<String, String> e : LinesNbuses) {
            if (e.getKey().equals(LineCode)) {
                c++;
            }
        }
        return c;
    }

    public static String getRouteCode(String Vehicle) {
        for (Map.Entry<String, String> e : BusesNroutes) {
            if (e.getKey().equals(Vehicle)) {
                return e.getValue();
            }
        }
        return "error";
    }

    public static boolean moreBuses() {
//        System.out.println(busCount);
        return busCount < totalBusLines;
    }

    public static List<String> getFileLines(String fileName) {
        List<String> fileLines = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(PATH+fileName));
            while (input.hasNext()) {
                fileLines.add(input.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " does not exist! Exiting.");
            System.exit(1);
        }
        return fileLines;
    }

    public static List<Broker> getBrokerList(String ipFileName) {
        List<Broker> brokers = new ArrayList<>();
        ipLines = getFileLines(ipFileName);
        for (String ip : ipLines) {
            try {
                brokers.add(new Broker(new ArrayList<>(), InetAddress.getByName(ip)));
            } catch (UnknownHostException e) {
                System.out.println("Unknown IP address " + ip + ". Check your IP file.");
                e.printStackTrace();
            }
        }
        return brokers;
    }

    public static void main(String[] args) {
        new Reader("busLinesNew.txt", "brokerIPs.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        //new Publisher(new ArrayList<>()).init(4321);

        for (String[] s: Reader.PositionTable) {
            //System.out.println("hey");
            System.out.println(s[0]);

        }
    }


}
