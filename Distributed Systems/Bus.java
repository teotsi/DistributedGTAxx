public class Bus{

    private String lineNumber;
    private String routeCode;
    private String vehicleId;
    private String lineName;
    private String buslineId;
    private String info;


    public Bus(String lineNumber, String routeCode, String vehicleId, String lineName, String buslineId, String info){
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
    public String getRouteCode() {
        return routeCode;
    }    
    public String getVehicleId() {
        return vehicleId;
    }
    public String getLineName() {
        return lineName;
    }
    public String getBuslineId() {
        return buslineId;
    }
    public String getInfo() {
        return info;
    }

    //Setters
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
    public void setBuslineId(String buslineId) {
        this.buslineId = buslineId;
    }
    public void setInfo(String info) {
        this.info = info;
    }

}