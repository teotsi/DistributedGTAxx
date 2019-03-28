public class Value{

    private Bus bus;
    private double latitude;
    private double longitude;

    public Value(Bus bus, double latitude, double longitude){
        this.bus = bus;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    //Getters
    public Bus getBus() {
        return bus;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    //Setters
    public void setBus(Bus bus) {
        this.bus = bus;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}