package com.example.buslocationapp;

import java.util.*;

public class Routes{

    List<String> routeVariants = new ArrayList<>();
    String masterRoute;

    public Routes(List<String> r){
        this.routeVariants = r;
        this.masterRoute = r.get(0);
    }

    public List<String> getRouteVariants(){
        return this.routeVariants;
    }

    public String getMasterRoute(){
        return this.masterRoute;
    }

    public void addRouteVariant(String var){
        this.routeVariants.add(var);
    }

    public String toString(){
        String reply = "";
        for(int i = 0; i< this.getRouteVariants().size(); i++){
            reply = reply + getRouteVariants().get(i) + "\n";

        }
        return reply;
    }
}