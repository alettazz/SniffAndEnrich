package src;

import java.util.Comparator;

public class Probe {
    private String mac_address;
    private String vendor;
    private String ssid;
    private String date;
    private String rssi;

    public Probe(String mac_address, String vendor, String ssid, String date, String rssi, String dump) {
        this.mac_address = mac_address;
        this.vendor = vendor;
        this.ssid = ssid;
        this.date = date;
        this.rssi = rssi;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "src.Probe{" +
                "mac_address='" + mac_address + '\'' +
                ", vendor='" + vendor + '\'' +
                ", ssid='" + ssid + '\'' +
                ", date='" + date + '\'' +
                ", rssi='" + rssi + '\'' +
                '}';
    }

    public static class ProbeComparator implements Comparator<Probe> {

        @Override
        public int compare(Probe p1, Probe p2) {
            return p1.getMac_address().compareTo(p2.getMac_address());
        }
    }
}
