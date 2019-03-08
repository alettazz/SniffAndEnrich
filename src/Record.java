package src;


import java.util.ArrayList;

public class Record {
    private String macID;
    private String vendor;
    private ArrayList<Capture> requests = new ArrayList<>();
    private ArrayList<Capture> responses = new ArrayList<>();
    private LatLng latLng = new LatLng(0, 0);

    @Override
    public String toString() {
        return "Record{" +
                "macID='" + macID + '\'' +
                ", vendor='" + vendor + '\'' +
                ", requests=" + requests +
                ", responses=" + responses +
                ", latLng=" + latLng +
                '}';
    }

    public Record(String sourceMAC) {
        this.macID = sourceMAC;
        this.vendor = "";

    }

    public void addToRequestList(Capture capture) {
        requests.add(capture);
    }

    public void updateWithFileteredResponses(ArrayList<Capture> filtered) {
        this.responses.clear();
        this.responses.addAll(filtered);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
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
