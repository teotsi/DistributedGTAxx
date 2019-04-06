import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Reader {
    private static List<String> busLines;
    private static int busCount = 0;
    private static int totalBusLines;
    private static int ipCount = 0;
    private static List<String> ipLines;
    private static List<String> positionLines;
    private static List<String> routeLines;
    private static int totalIpLines;
    private static Set<Map.Entry> LinesNbuses =new HashSet<>();
    private static Set<Map.Entry> BusesNroutes =new HashSet<>();
    private static Set<Map.Entry> RoutesNinfo =new HashSet<>();

    public Reader(String busLinesfileName, String ipFileName, String busPositionsFileName, String RouteCodesFileName) {
        Scanner busInput, ipInput, positionInput, routeInput;
        File busFile, ipFile, busPositions, RouteCodes;
        try {
            busFile = new File(busLinesfileName);
            busInput = new Scanner(busFile);
            ipFile = new File(ipFileName);
            ipInput = new Scanner(ipFile);
            busPositions = new File(busPositionsFileName);
            positionInput = new Scanner(busPositions);
            RouteCodes = new File(RouteCodesFileName);
            routeInput = new Scanner(RouteCodes);
        } catch (FileNotFoundException e) {
            System.out.println("No such file!");
            return;
        }
        busLines = new ArrayList<String>();
        while (busInput.hasNext()) {
            busLines.add(busInput.nextLine());
        }
        totalBusLines = busLines.size();
        ipLines= new ArrayList<String>();
        while(ipInput.hasNext()){
            ipLines.add(ipInput.nextLine());
        }
        totalIpLines = ipLines.size();
        positionLines = new ArrayList<String>();
        while (positionInput.hasNext()){
            positionLines.add(positionInput.nextLine());
        }
        routeLines = new ArrayList<String>();
        while (routeInput.hasNext()){
            routeLines.add(routeInput.nextLine());
        }
    }

    public static String[] getBus() {
        StringTokenizer st;
        st = new StringTokenizer(busLines.get(busCount++), ",");
        String[] busLineInfo={st.nextToken(),st.nextToken(),st.nextToken()};
        return busLineInfo;
    }

    public static String[] getVehicles(String LineCode, int count){
        String[] Vehicles= new String[count];
        int i=0;
        for(Map.Entry<String, String> e:LinesNbuses){
            if(e.getKey().equals(LineCode)){
                Vehicles[i]=e.getValue();
                i++;
            }
        }
        return Vehicles;

    }

    public static void createRoutesNinfo(){
        for (int i = 0; i < routeLines.size(); i++) {
            StringTokenizer st;
            st = new StringTokenizer(routeLines.get(i), ",");
            String code=st.nextToken();
            st.nextToken();
            st.nextToken();
            String info=st.nextToken();
            RoutesNinfo.add(new AbstractMap.SimpleEntry<>(code,info.trim()));
        }
    }

    public static String getInfo(String RouteCode){
        for(Map.Entry<String, String> e:RoutesNinfo){
            if(e.getKey().equals(RouteCode)){
                return e.getValue();
            }
        }
        return "error";
    }

    public static void createBusesMap(){
        for (int i = 0; i < positionLines.size(); i++) {
            StringTokenizer st;
            st = new StringTokenizer(positionLines.get(i), ",");
            String code= st.nextToken();
            String route=st.nextToken();
            String vehicleId= st.nextToken();
            BusesNroutes.add(new AbstractMap.SimpleEntry<>(vehicleId.trim(),route.trim()));
            LinesNbuses.add(new AbstractMap.SimpleEntry<>(code,vehicleId.trim()));
        }
    }

    public static int getNumberOfBuses(String LineCode){
        int c=0;
        for(Map.Entry<String, String> e:LinesNbuses){
            if(e.getKey().equals(LineCode)){
                c++;
            }
        }
        return c;
    }

    public static String getRouteCode(String Vehicle){
        for(Map.Entry<String, String> e:BusesNroutes){
            if(e.getKey().equals(Vehicle)){
               return e.getValue();
            }
        }
        return "errror";
    }

    public static boolean moreBuses() {
//        System.out.println(busCount);
        return busCount < totalBusLines;
    }
    
    public static String getIP(){
        return ipLines.get(ipCount++);
    }

    public static void main(String[] args) {
        new Reader("../dataset/busLinesNew.txt", "../dataset/brokerIPs.txt", "../dataset/busPositionsNew.txt", "../dataset/RouteCodesNew.txt");
        new Publisher(new ArrayList<>()).init(4321);
    }


}
