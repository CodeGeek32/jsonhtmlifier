package beautifier;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public String header;
    public String json;
    public List<String> body;
    public int index;

    public Message() {
        header = "";
        json = "";
        body = new ArrayList<>();
    }

    public Message(String header, int index) {
        this.header = header;
        body = new ArrayList<>();
        this.index = index;
        json = "";
    }

    public boolean containsJson(){
        return json != "";
    }
}
