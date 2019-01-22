import java.io.*;
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
    private static int id = 1;

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

        System.out.println(calculateDistance(-60, 2.5));
    }

    private static void createActivities(int calibratedTime) {
        StringBuilder sb = null;
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("test_dist.csv"));
            sb = new StringBuilder();
            sb.append("activity_ID");
            sb.append(',');
            sb.append("mac_address");
            sb.append(',');
            sb.append("vendor");
            sb.append(',');
            sb.append("first_Try");
            sb.append(',');
            sb.append("second_try");
            sb.append(',');
            sb.append("third_try");
            sb.append(',');
            sb.append("ssid_1");
            sb.append(',');
            sb.append("rssi_1");
            sb.append(',');
            sb.append("dist_1");
            sb.append(',');
            sb.append("ssid_2");
            sb.append(',');
            sb.append("rssi_2");
            sb.append(',');
            sb.append("dist_2");
            sb.append(',');
            sb.append("ssid_3");
            sb.append(',');
            sb.append("rssi_3");
            sb.append(',');
            sb.append("dist_3");
            sb.append(',');


            pw.write(sb.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        System.out.println(activities.size());
        for (ProbeActivity activity : activities) {
            sb = new StringBuilder();

            for (int i = 1; i < activity.getProbes().size(); i++) {
                try {
                    Date firstDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .parse(activity.getProbes().get(i - 1).getDate());
                    Date secondDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .parse(activity.getProbes().get(i).getDate());
                    Date thirdDate = null;

                    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                    long diff = TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.SECONDS);

                    if (diff < calibratedTime) {
                        if (activity.getProbes().size() > 2 && i + 1 < activity.getProbes().size()) {
                            thirdDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    .parse(activity.getProbes().get(i + 1).getDate());
                        }
                        calculateDistance(Double.parseDouble(activity.getProbes().get(i).getRssi()), 2.5);
                        // System.out.println((activity.getProbes().get(i).getMac_address()+"=>"+firstDate + " , " + secondDate+ " , "+thirdDate));
                        sb.append("\n");
                        sb.append(id++);
                        sb.append(",");
                        sb.append(activity.getProbes().get(i).getMac_address());
                        sb.append(",");
                        sb.append(activity.getProbes().get(i).getVendor());
                        sb.append(",");
                        sb.append(firstDate);
                        sb.append(",");
                        sb.append(secondDate);
                        sb.append(",");
                        sb.append(thirdDate);
                        sb.append(",");
                        sb.append(activity.getProbes().get(i - 1).getSsid());
                        sb.append(",");
                        sb.append(activity.getProbes().get(i - 1).getRssi());
                        sb.append(",");
                        sb.append(calculateDistance(Double.parseDouble(activity.getProbes().get(i - 1).getRssi()), 2.4));
                        sb.append(",");
                        sb.append(activity.getProbes().get(i).getSsid());
                        sb.append(",");
                        sb.append(activity.getProbes().get(i).getRssi());
                        sb.append(",");
                        sb.append(calculateDistance(Double.parseDouble(activity.getProbes().get(i).getRssi()), 2.4));
                        sb.append(",");

                        if (activity.getProbes().size() > 2 && i + 1 < activity.getProbes().size()) {
                            sb.append(activity.getProbes().get(i + 1).getSsid());
                            sb.append(",");
                            sb.append(activity.getProbes().get(i + 1).getRssi());
                            sb.append(",");
                            sb.append(calculateDistance(Double.parseDouble(activity.getProbes().get(i - 1).getRssi()), 2.4));
                            sb.append(",");

                        } else {
                            sb.append(" null ");
                            sb.append(",");
                            sb.append(" null ");
                            sb.append(" 0 ");

                        }
                        sb.append(",");


                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    System.out.println("parseException");
                }

            }
            // sb.append("\n");
            pw.write(sb.toString());

        }

        pw.close();
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
                    probeActivity.addProbe(initialDataSet.get(i - 1));
                    activities.add(probeActivity);
                }
                last = false;
                probeActivity = new ProbeActivity();

            }
        }
    }

    public static double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (-69.44 - (levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    public static LatLng getLocationByTrilateration(
            LatLng location1, double distance1,
            LatLng location2, double distance2,
            LatLng location3, double distance3) {
//DECLARE VARIABLES

        double[] P1 = new double[2];
        double[] P2 = new double[2];
        double[] P3 = new double[2];
        double[] ex = new double[2];
        double[] ey = new double[2];
        double[] p3p1 = new double[2];
        double jval = 0;
        double temp = 0;
        double ival = 0;
        double p3p1i = 0;
        double triptx;
        double tripty;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;
//TRANSALTE POINTS TO VECTORS
//POINT 1
        P1[0] = location1.latitude;
        P1[1] = location1.longitude;
//POINT 2
        P2[0] = location2.latitude;
        P2[1] = location2.longitude;
//POINT 3
        P3[0] = location3.latitude;
        P3[1] = location3.longitude;
//TRANSFORM THE METERS VALUE FOR THE MAP UNIT
//DISTANCE BETWEEN POINT 1 AND MY LOCATION
        distance1 = (distance1 / 100000);
//DISTANCE BETWEEN POINT 2 AND MY LOCATION
        distance2 = (distance2 / 100000);
//DISTANCE BETWEEN POINT 3 AND MY LOCATION
        distance3 = (distance3 / 100000);
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            t = t1 - t2;
            temp += (t * t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            exx = (t1 - t2) / (Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1 * t2);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t = t1 - t2 - t3;
            p3p1i += (t * t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3) / Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1 * t2);
        }
        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2)) / (2 * d);
        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2)) / (2 * jval)) - ((ival / jval) * xval);
        t1 = location1.latitude;
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;
        t1 = location1.longitude;
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        tripty = t1 + t2 + t3;
        return new LatLng(triptx, tripty);
    }

}