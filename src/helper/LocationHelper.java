package src.helper;

import src.models.LatLng;

public class LocationHelper {
    //since input data has been bettered with heatmap and trilateration modul this was used only at the beginning
    public static LatLng calculateIntersection(int x0, int y0, int x1, int y1, int r0, int r1) {
        double d;
        d = Math.sqrt(((x1 - x0) ^ 2 + (y1 - y0) ^ 2));
        double a;
        a = (r0 * r0 - r1 * r1 + d * d) / (2 * d);
        double h;
        h = Math.sqrt(r0 * r0 - a * a);
        double x2 = x0 + a * (x1 - x0) / d;
        double y2 = y0 + a * (y1 - y0) / d;
        double x3 = x2 + h * (y1 - y0) / d;      // also x3=x2-h*(y1-y0)/d
        double y3 = y2 - h * (x1 - x0) / d;
        return new LatLng(x3, y3);
    }
}
