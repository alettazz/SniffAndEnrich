import java.util.ArrayList;

public class Record {
    private String macID;
    private String vendor;
    private ArrayList<Capture> requests = new ArrayList<>();
    private ArrayList<Capture> responses = new ArrayList<>();
    private LatLng latLng = new LatLng(0, 0);

    public Record(String sourceMAC) {
        this.macID = sourceMAC;
        this.vendor = "";

       /* MacAddress mac = new MacAddress(s); // can also create from byte[] or NetworkInterface
        Oui oui = reg.getOui(mac);
        System.out.println("   MAC Address:  " + mac);
        System.out.println("   isMulticast:  " + mac.isMulticast());
        System.out.println("       isLocal:  " + mac.isLocal());
        System.out.println("  Manufacturer:  " + (oui == null ? "Unknown" : oui.getManufacturer()));*/

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
