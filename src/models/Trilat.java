package src.models;

import src.parser.RequestResponseParser;

public class Trilat {
    private LatLng latLng;
    private double distance;
    private double rssi;

    public Trilat(LatLng latLng, double distance, double rssi) {
        this.latLng = latLng;
        this.distance = distance;
        this.rssi = rssi;

    }

    public Trilat() {

    }

    public Trilat(String lat, String lon, String rssi) {
        this.latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        this.distance = RequestResponseParser.calculateDistance(Double.parseDouble(rssi));
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getRssi() {
        return rssi;
    }
}
