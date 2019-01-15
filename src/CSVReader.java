import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class CSVReader {

    private static ArrayList<Probe> initialDataSet;
    private static boolean last;
    private static ArrayList<ProbeActivity> activities = new ArrayList<>();

    public static void main(String[] args) {
        String csvFile = "/Users/Aletta/Desktop/sniffngo/probeDB_FILTERED_ssid.csv";
        String line = "";
        String cvsSplitBy = ";";
        initialDataSet = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] split = line.split(cvsSplitBy);

                Probe probe = new Probe(split[0], split[1], split[2], split[3], split[4]);
                initialDataSet.add(probe);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        processData();
        for (ProbeActivity activity : activities) {
            activity.orderActivitiesByDate();
            System.out.println("Activity + " + activity.toString());

            /*for (Probe probe : activity.getProbes()) {
                System.out.println("Activity + " + probe.toString());
            }*/
        }

    }

    private static void processData() {
        ProbeActivity probeActivity = new ProbeActivity();
        Collections.sort(initialDataSet, new Probe.ProbeComparator());
        for (int i = 1; i < initialDataSet.size(); i++) {

            if (initialDataSet.get(i).getMac_address().equals(initialDataSet.get(i - 1).getMac_address())) {

                //  System.out.println(initialDataSet.get(i - 1).toString());
                probeActivity.addProbe(initialDataSet.get(i - 1));
                last = true;
            } else {
                if (last) {

                    probeActivity.addProbe(initialDataSet.get(i-1));
                    activities.add(probeActivity);
                }
                last = false;
                probeActivity = new ProbeActivity();

            }
        }
    }

}