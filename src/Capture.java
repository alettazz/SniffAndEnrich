package src;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Capture {
    private String id;
    private Date time;
    private String sourceMAC;
    private String destinationMAC;
    private String protocol;
    private String rssi;
    private String deltaTime;
    private String captureType;
    private String sourceManufacturer;
    private String sourceModel;
    private String sourceType;
    private String wlanSSID;
    private String dataRate;
    private String deviceName; //source
    private String deviceTypeCategory; //smartphone
    private boolean used;
    private int minute;
    private int hour;

    public Capture(String id, String time, String sourceMAC, String destinationMAC, String protocol, String rssi, String deltaTime, String captureType, String sourceManufacturer, String sourceModel, String sourceType, String wlanSSID, String dataRate, String deviceName, String deviceTypeCategory) {
        this.id = id.substring(1, id.length() - 1);
        this.time = convertTimeToDate(time.substring(1, time.length() - 1));
        this.sourceMAC = sourceMAC.substring(1, sourceMAC.length() - 1);
        this.destinationMAC = destinationMAC.substring(1, destinationMAC.length() - 1);
        this.protocol = protocol.substring(1, protocol.length() - 1);
        this.rssi = rssi.substring(1, rssi.length() - 1);
        this.deltaTime = deltaTime.substring(1, deltaTime.length() - 1);
        this.captureType = captureType.substring(1, captureType.length() - 1);
        this.sourceManufacturer = sourceManufacturer.substring(1, sourceManufacturer.length() - 1);
        this.sourceModel = sourceModel.substring(1, sourceModel.length() - 1);
        this.sourceType = sourceType.substring(1, sourceType.length() - 1);
        this.wlanSSID = wlanSSID.substring(1, wlanSSID.length() - 1);
        this.dataRate = dataRate.substring(1, dataRate.length() - 1);
        this.deviceName = deviceName.substring(1, deviceName.length() - 1);
        this.deviceTypeCategory = deviceTypeCategory.substring(1, deviceTypeCategory.length() - 1);
    }

    private Date convertTimeToDate(String substring) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Date date = null;
        if (!substring.equals("Time")) {
            try {
                date = sdf.parse(substring);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

    public String getSourceManufacturer() {
        return sourceManufacturer;
    }

    public void setSourceManufacturer(String sourceManufacturer) {
        this.sourceManufacturer = sourceManufacturer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public int getMinute() {
        return minute;
    }

    public int getHour() {
        return hour;
    }

    public String getSourceMAC() {
        return sourceMAC;
    }

    public void setSourceMAC(String sourceMAC) {
        this.sourceMAC = sourceMAC;
    }

    public String getDestinationMAC() {
        return destinationMAC;
    }

    public void setDestinationMAC(String destinationMAC) {
        this.destinationMAC = destinationMAC;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(String deltaTime) {
        this.deltaTime = deltaTime;
    }


    public String getSourceModel() {
        return sourceModel;
    }

    public void setSourceModel(String sourceModel) {
        this.sourceModel = sourceModel;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getWlanSSID() {
        return wlanSSID;
    }

    public void setWlanSSID(String wlanSSID) {
        this.wlanSSID = wlanSSID;
    }

    public String getDataRate() {
        return dataRate;
    }

    public void setDataRate(String dataRate) {
        this.dataRate = dataRate;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceTypeCategory() {
        return deviceTypeCategory;
    }

    public void setDeviceTypeCategory(String deviceTypeCategory) {
        this.deviceTypeCategory = deviceTypeCategory;
    }

    public String getCaptureType() {
        return captureType;
    }

    public void setCaptureType(String captureType) {
        this.captureType = captureType;
    }

    @Override
    public String toString() {
        return "src.Capture{" +
                "id='" + id + '\'' +
                ", time='" + time + '\'' +
                ", sourceMAC='" + sourceMAC + '\'' +
                ", destinationMAC='" + destinationMAC + '\'' +
                ", protocol='" + protocol + '\'' +
                ", rssi='" + rssi + '\'' +
                ", deltaTime='" + deltaTime + '\'' +
                ", captureType='" + captureType + '\'' +
                ", sourceManufacturer='" + sourceManufacturer + '\'' +
                ", sourceModel='" + sourceModel + '\'' +
                ", sourceType='" + sourceType + '\'' +
                ", wlanSSID='" + wlanSSID + '\'' +
                ", dataRate='" + dataRate + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceTypeCategory='" + deviceTypeCategory + '\'' +
                '}';
    }

    public boolean isUsed() {
        return this.used;
    }

    public void setUsed(boolean b) {
        used = b;
    }


    public static class CaptureComparator implements Comparator<Capture> {

        @Override
        public int compare(Capture o1, Capture o2) {
            return o1.getSourceMAC().compareTo(o2.getSourceMAC());
        }
    }

    public static class CaptureComparatorTime implements Comparator<Capture> {

        @Override
        public int compare(Capture o1, Capture o2) {
            return o1.getTime().compareTo(o2.getTime());
        }
    }
}
