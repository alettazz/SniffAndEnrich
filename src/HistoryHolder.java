package src;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryHolder {
    private static HistoryHolder ourInstance = new HistoryHolder();
    private static HashMap<String, ArrayList<LatLng>> history;


    private HistoryHolder() {
        history = new HashMap<String, ArrayList<LatLng>>();
    }

    public static HistoryHolder getInstance() {

        if (ourInstance == null) {
            ourInstance = new HistoryHolder();
        }
        return ourInstance;
    }

    public void addHistoryEntryToMac(String destinationMAC, LatLng entry) {

        if (history.get(destinationMAC) != null) {
            int size = history.get(destinationMAC).size();
            if (history.get(destinationMAC).get(size - 1) == (entry)) {
                System.out.println("ugyanaz mint az uccso");
                return;
            }
        }

        if (history.get(destinationMAC) != null) {

            history.get(destinationMAC).add(entry);
        } else {
            ArrayList<LatLng> latLngs = new ArrayList<>();
            latLngs.add(entry);

            history.put(destinationMAC, latLngs);
        }


    }

    public HashMap<String, ArrayList<LatLng>> getHistory() {
        return history;
    }
}
