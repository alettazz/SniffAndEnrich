public class Trilat {
    private LatLng latLng;
    private double distance;

    public Trilat(LatLng latLng, double distance) {
        this.latLng = latLng;
        this.distance = distance;

    }

    public Trilat() {

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
}
