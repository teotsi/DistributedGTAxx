package Service;

import java.io.Serializable;

public class Bus implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;
    private String lineNumber;
    private String routeCode;
    private String vehicleId;
    private String lineName;
    private String buslineId;
    private String info;


    public Bus(String lineNumber, String routeCode, String vehicleId, String lineName, String buslineId, String info) {
        this.lineNumber = lineNumber;
        this.routeCode = routeCode;
        this.vehicleId = vehicleId;
        this.lineName = lineName;
        this.buslineId = buslineId;
        this.info = info;
    }


    //Getters
    public String getLineNumber() {
        return lineNumber;
    }

    //Setters
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getBuslineId() {
        return buslineId;
    }

    public void setBuslineId(String buslineId) {
        this.buslineId = buslineId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}