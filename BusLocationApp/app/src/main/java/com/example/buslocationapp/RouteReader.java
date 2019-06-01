package com.example.buslocationapp;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RouteReader {

    public static Routes busRoutes;
    public static List<String> filelines = new ArrayList<>();
    public static List<String> routeList = new ArrayList<>();
    public final static String FILENAME = "RouteCodesNew.txt";

    public static int counter = 0;
    public static boolean flag = true;

    public static List<Routes> bRoutes = new ArrayList<>();

    public static void main(String[] args) {
        Scanner input;
        try {
            input = new Scanner(new File(FILENAME));
            while (input.hasNext()) {
                filelines.add(input.nextLine());
            }

            for(String f_line: filelines){
                StringTokenizer st;
                st = new StringTokenizer(f_line,",");
                st.nextToken();
                st.nextToken();
                st.nextToken();
                routeList.add(st.nextToken());
            }

            filelines = new ArrayList<>();
            for(String route : routeList){
                if(counter == 0 ){
                    filelines.add(route);
                    flag = false;
                }else if (counter < routeList.size()  && ((routeList.get(counter -1)).contains(route.substring(0, 5)))) {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }

    }
}



