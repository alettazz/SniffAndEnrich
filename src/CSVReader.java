import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

        orderProcessedActivitiesByDate();

        createActivities(5);

    }

    private static void createActivities(int calibratedTime) {
        for (ProbeActivity activity : activities) {
            for (int i = 1; i < activity.getProbes().size(); i++) {
                try {
                    Date firstDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .parse(activity.getProbes().get(i - 1).getDate());
                    Date secondDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .parse(activity.getProbes().get(i).getDate());
                    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                    long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    if (diff > calibratedTime) {
                        System.out.println(firstDate + " , " + secondDate);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static void orderProcessedActivitiesByDate() {
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