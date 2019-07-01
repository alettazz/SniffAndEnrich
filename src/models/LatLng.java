package src.models;

import java.util.Date;

public class LatLng implements Comparable<LatLng> {

    private Date date;
    private double latitude;
    private double longitude;
    private double timestamp = new Date().getTime();

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng(double triptx, double tripty, Date date) {
        this.latitude = triptx;
        this.longitude = tripty;

        if (date != null) {
            this.timestamp = date.getTime();
            this.date = date;
        }
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "LatLng{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public int compareTo(LatLng o) {
        return (int) o.latitude;
    }
}
