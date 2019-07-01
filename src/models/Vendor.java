package src.models;

public class Vendor {
    private String macPreffix;
    private String vendor;

    public Vendor() {
    }

    public Vendor(String macPreffix, String vendor) {
        this.macPreffix = macPreffix;
        this.vendor = vendor;
    }

    public String getMacPreffix() {
        return macPreffix;
    }

    public void setMacPreffix(String macPreffix) {
        this.macPreffix = macPreffix;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
