package Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Reader {
    private static List<String> busLines;
    private static int busCount = 0;
    private static int totalBusLines;

    private static List<String> ipLines;
    private static List<String> positionLines;
    private static List<String> routeLines;
    private static Set<Map.Entry<String, String>> LinesNbuses = new HashSet<>();
    private static Set<Map.Entry<String, String>> BusesNroutes = new HashSet<>();
    private static Set<Map.Entry<String, String>> RoutesNinfo = new HashSet<>();
    private static final String PATH = "../dataset/";
    private static List<String[]> PositionTable=new ArrayList<>();

    public Reader(String busLinesFileName, String busPositionsFileName, String routeCodesFileName) {
        createBusLines(busLinesFileName);
        totalBusLines = busLines.size();
        positionLines = getFileLines(busPositionsFileName);
        routeLines = getFileLines(routeCodesFileName);

    }

    public static List<String> IDs(String busLinesFileName){
        createBusLines(busLinesFileName);
        List<String> Ids= new ArrayList<>();
        for (String busLine : busLines) {
            StringTokenizer st = new StringTokenizer(busLine, ",");
            st.nextToken();
            Ids.add(st.nextToken());
            st.nextToken();
        }
        return Ids;
    }

    private static void createBusLines(String busLinesFileName){
        busLines = getFileLines(busLinesFileName);
    }

    public static String[] getBus() {
        StringTokenizer st;
        st = new StringTokenizer(busLines.get(busCount++), ",");
        return new String[]{st.nextToken(), st.nextToken(), st.nextToken()};
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
        for (String routeLine : routeLines) {
            StringTokenizer st;
            st = new StringTokenizer(routeLine, ",");
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
        for (String positionLine : positionLines) {
            StringTokenizer st;
            st = new StringTokenizer(positionLine, ",");
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

    private static List<String> getFileLines(String fileName) {
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
    private static List<String> getFileLines(InputStream fileName) {
        List<String> fileLines = new ArrayList<>();
        Scanner input = new Scanner(fileName);
        while (input.hasNext()) {
            fileLines.add(input.nextLine());
        }
        return fileLines;
    }

    public static List<String> getIPs(){
        return  ipLines;
    }

    public static List<Broker> getBrokerList(InputStream stream) {
        List<Broker> brokers = new ArrayList<>();
        ipLines = getFileLines(stream);
        for (String ip : ipLines) {
            try {
                brokers.add(new Broker(new ArrayList<>(),null, InetAddress.getByName(ip),false));
            } catch (UnknownHostException e) {
                System.out.println("Unknown IP address " + ip + ". Check your IP file.");
                e.printStackTrace();
            }
        }
        return brokers;
    }

    public static void main(String[] args) {
        new Reader("busLinesNew.txt", "busPositionsNew.txt", "RouteCodesNew.txt");
        //new Service.Publisher(new ArrayList<>()).init(4321);

        for (String[] s: Reader.PositionTable) {
            //System.out.println("hey");
            System.out.println(s[0]);

        }
    }

    public static void sort2D(String[][] array,int column){
        Arrays.sort(array, (entry1, entry2) -> { //lambda expression for sorting 2d arrays
            final int hash1 = Integer.parseInt(entry1[column]);
            final int hash2 = Integer.parseInt(entry2[column]);
            return hash1 - hash2;
        });
    }

}
