public class LatLng implements Comparable<LatLng> {

    public double latitude;
    public double longitude;

    public LatLng(double triptx, double tripty) {
        this.latitude = triptx;
        this.longitude = tripty;
    }

    @Override
    public int compareTo(LatLng o) {
        return (int) o.latitude;
    }
}
