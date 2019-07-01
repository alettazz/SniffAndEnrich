package src.helper;

import src.models.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HistoryHolder {
    private static HistoryHolder ourInstance = new HistoryHolder();
    private static HashMap<String, ArrayList<LatLng>> history;
    private boolean dont;


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
                System.out.println("same as previous");
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
        cleanHistoryFromDuplicateEntries();
        return history;
    }

    public HashMap<String, ArrayList<LatLng>> getHistoryMAP() {
        return history;
    }

    public void cleanHistoryFromDuplicateEntries() {
        for (ArrayList<LatLng> value : history.values()) {
            for (int i = 0; i < value.size(); i++) {
                Date date = value.get(i).getDate();
                for (int i1 = 1; i1 < value.size(); i1++) {
                    if (value.get(i1).getDate().equals(date)) {
                        value.remove(i1);
                        dont = true;
                    }
                }
            }
        }
    }
}
