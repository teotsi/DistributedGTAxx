import java.io.Serializable;
public class Message implements Serializable {

    private static final long serialVersionUID = -746107103362571848L;
    int id;
    String data;

    public Message(int id, String data) {
        super();
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String toString() {
        return id + " - " + data;
    }
}

