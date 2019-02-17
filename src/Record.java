import java.util.ArrayList;

public class Record {
    private String macID;
    private ArrayList<Capture> requests = new ArrayList<>();
    private ArrayList<Capture> responses = new ArrayList<>();

    public Record(String sourceMAC) {
        this.macID = sourceMAC;
    }

    public void addToRequestList(Capture capture) {
        requests.add(capture);
    }

    public void addToResponseList(Capture capture) {
        responses.add(capture);
    }

    public String getMacID() {
        return macID;
    }

    public ArrayList<Capture> getRequests() {
        return requests;
    }

    public ArrayList<Capture> getResponses() {
        return responses;
    }
}
