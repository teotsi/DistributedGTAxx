package com.example.buslocationapp;


import java.io.InputStream;
import java.util.*;

import Service.Reader;

public class RouteReader {

    public static Routes busRoutes;
    public static List<String> filelines = new ArrayList<>();
    public static List<String> routeList = new ArrayList<>();
    public static List<String> officialRouteList = new ArrayList<>();
    private static List<Map.Entry<String, String>> routesAndIds = new ArrayList<>();
    private static ArrayList<Map.Entry> linesAndRoutes = new ArrayList<>();
    public final static String FILENAME = "RouteCodesNew.txt";

    public static int counter = 0;
    public static boolean flag = true;

    public static List<Routes> bRoutes = new ArrayList<>();

    public List<Routes> getRoutes(InputStream routesStream, InputStream linesStream) {
        Scanner input;
        try {
            input = new Scanner(routesStream);
            while (input.hasNext()) {
                filelines.add(input.nextLine());
            }

            for(String f_line: filelines){
                StringTokenizer st;
                st = new StringTokenizer(f_line,",");
                String routeCode = st.nextToken();
                routesAndIds.add(new AbstractMap.SimpleEntry<String, String>(routeCode,st.nextToken()));
                st.nextToken();
                routeList.add(routeCode+" "+st.nextToken());
            }
            List<String> busLines = Reader.getFileLines(linesStream);
            for(String line: busLines){
                StringTokenizer st = new StringTokenizer(line,",");
                linesAndRoutes.add(new AbstractMap.SimpleEntry<String, String>(st.nextToken(),st.nextToken()));
            }
            for(Map.Entry entry: routesAndIds){
                for(Map.Entry entry1:linesAndRoutes){
                    if (entry1.getKey().equals(entry.getValue())){
                        entry.setValue(entry1.getValue());
                    }
                }
            }
            for (String route: routeList){
                for(Map.Entry entry: routesAndIds){
                    if(entry.getKey().equals(route.substring(0,4))){
                        officialRouteList.add(entry.getKey()+" "+route.substring(4));
                    }
                }
            }
            filelines = new ArrayList<>();
            for(String route : officialRouteList){
                if(counter == 0 ){
                    filelines.add(route);
                    flag = false;
                }else if (counter < routeList.size()  && ((routeList.get(counter -1)).contains(route.substring(5, 10)))) {
                    filelines.add(route);
                    flag = false;
                }else{
                    flag = true;
                    bRoutes.add(new Routes(filelines));

                    filelines = new ArrayList<>();
                    filelines.add(route);
                }
                counter++;
            }

            for(int i = 0; i < bRoutes.size(); i++){
                System.out.println(bRoutes.get(i).toString());
            }

            input.close();
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }

        return bRoutes;
    }

    public static List<Map.Entry<String, String>> getLinesAndRoutes() {
        return routesAndIds;
    }
}



