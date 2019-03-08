package src;

public class LatLng implements Comparable<LatLng> {

    private double latitude;
    private double longitude;

    public LatLng(double triptx, double tripty) {
        this.latitude = triptx;
        this.longitude = tripty;
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
