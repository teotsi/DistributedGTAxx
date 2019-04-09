import java.io.ObjectInput;
import java.io.Serializable;

public class Topic implements Serializable {

    private String busLine;

    public Topic(String busLine) {
        this.busLine = busLine;
    }


    public String getBusLine() {
        return busLine;
    }

    public void setBusLine(String busLine) {
        this.busLine = busLine;
    }


    @Override
    public boolean equals(Object t){
        if(t==null){
            return false;
        }
        if(!Topic.class.isAssignableFrom(t.getClass())){
            return false;
        }
        return ((Topic)t).getBusLine().equals(this.busLine);
    }
}