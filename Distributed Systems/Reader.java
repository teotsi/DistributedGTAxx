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

    public static String getBus() {
        StringTokenizer st;
            st = new StringTokenizer(busLines.get(busCount++), ",");
//        System.out.println("after busCount++");
        st.nextToken();
        return st.nextToken();
    }

    public static int getLineCode(){
        StringTokenizer st;
        st = new StringTokenizer(busLines.get(busCount-1), ",");
        return Integer.parseInt(st.nextToken());
    }

    public static void createBusesMap(){
        for (int i = 0; i < positionLines.size(); i++) {
            StringTokenizer st;
            st = new StringTokenizer(positionLines.get(i), ",");
            String current= st.nextToken();
            st.nextToken();
            LinesNbuses.add(new AbstractMap.SimpleEntry<>(Integer.parseInt(current),Integer.parseInt(st.nextToken().trim())));
        }
    }

    public static int getNumberOfBuses(int LineCode){
        int c=0;
        for(Map.Entry<Integer, Integer> e:LinesNbuses){
            if(e.getKey()==LineCode){
                c++;
            }
        }
        return c;
    }

    public static int getRouteCode(int LineCode){
        for (int i = 0; i < positionLines.size(); i++) {
            if(positionLines.get(i).startsWith(String.valueOf(LineCode))){

            }
        }
        return 0;
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
        createBusesMap();
        System.out.println(getNumberOfBuses(1151));
    }

}
