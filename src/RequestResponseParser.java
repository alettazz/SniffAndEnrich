package src;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestResponseParser {
    private static final String PROBE_REQUEST = "src.Probe Request";
    private static final String PROBE_RESPONSE = "src.Probe Response";
    private static final String BROADCAST = "ff:ff:ff:ff:ff:ff";
    private static ArrayList<Capture> captures;
    private static int counter = 1;
    private static StringBuilder sb;
    static PrintWriter pw = null;

    private static HashMap<String, Record> records = new HashMap<String, Record>();
    private static ArrayList<String> strangers = new ArrayList<>();
    private static int strangerId = 0;

    public static void main(String[] args) {
        String csvFile = "C:/Users/Aletta/Desktop/sniffngo/MERESEK/febr26/febr26.csv";
        String line = "";
        String cvsSplitBy = ",";
        captures = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] split = line.split(cvsSplitBy);
                Capture capture = new Capture(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9], split[10], split[11], split[12], split[13], split[14]);
                captures.add(capture);
                //  System.out.println(capture);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        captures.remove(0);

        Collections.sort(captures, new Capture.CaptureComparator());

        System.out.print("kezdet ido  ");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(cal.getTime()));

        createRecords();

        System.out.println("vege" + records.size());

        initFile();

        processRecordIntoActivity();

        pw.close();
        System.out.println("First seen");


        Set<String> unique = new HashSet<>(strangers);

        for (String s : unique) {
            // System.out.println("Unique stranger mac: " +s);

        }
        System.out.println(unique.size());
        Calendar cal1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        System.out.println("vege ido" + sdf1.format(cal1.getTime()));

        // createGroups(5);

    }

    private static void createGroups(int min) {
        Collections.sort((List<Record>) records.values(), new Comparator<Record>() {
                    @Override
                    public int compare(Record o1, Record o2) {
                        return o1.getLatLng().compareTo(o2.getLatLng());
                    }
                }
        );
        for (Record value : records.values()) {
            System.out.println("Rec" + value.getLatLng());
        }
    }

    private static void initFile() {
        try {
            pw = new PrintWriter(new File("test_dist_loc_febr26.csv"));
            System.out.println(pw.toString());
            sb = new StringBuilder();
            sb.append("activity_ID"); //counter
            sb.append(',');
            sb.append("mac_address"); //key
            sb.append(',');
            sb.append("vendor");
            sb.append(',');
            sb.append("first_Try"); //objecy.get0
            sb.append(',');
            sb.append("second_try");
            sb.append(',');
            sb.append("third_try");
            sb.append(',');
            sb.append("dist_1");
            sb.append(',');
            sb.append("dist_2");
            sb.append(',');
            sb.append("dist_3");
            sb.append(',');
            sb.append("LAT");
            sb.append(',');
            sb.append("LON");
            sb.append(',');
            sb.append("Vendor");
            sb.append(',');


            pw.write(sb.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static LatLng matchLocationSSID(String SSID) {
        LatLng latlng = new LatLng(0, 0);
        switch (SSID) {
            case "Internet":
                latlng = new LatLng(46.522973, 24.598701);
                break;
            case "Internet5G":
                latlng = new LatLng(46.522973, 24.598701);
                break;
            case "Internet5Ga":
                latlng = new LatLng(46.52271327, 24.59885103);
                break;
            case "Internet5Gb":
                latlng = new LatLng(46.523182, 24.598695);
                break;
            case "SapiPark":
                latlng = new LatLng(46.522764, 24.598590);
                break;
            case "Pince2":
                latlng = new LatLng(46.523044, 24.598436);
                break;
            case "Internet5Gc":
                latlng = new LatLng(46.5059101, 24.6196315);
                break;
        }
        return latlng;

    }

    private static void processRecordIntoActivity() {

        for (Map.Entry<String, Record> entry : records.entrySet()) {
            String key = entry.getKey();
            Record value = entry.getValue();
            Date timeToCheck = new Date();
            Map<String, Double> mapRSSIDistance = new HashMap<>();
            if (entry.getValue().getRequests().size() == 1 && !entry.getValue().getRequests().get(0).getWlanSSID().equals("Internet")) {
                if (entry.getValue().getRequests().get(0).getWlanSSID().equals("")) {
                    strangers.add(entry.getValue().getRequests().get(0).getSourceMAC());

                }
            }
            for (Capture request : value.getRequests()) {

                if (request.getDestinationMAC().equals(BROADCAST)) {
                    timeToCheck = request.getTime();
                    if (request.getWlanSSID().equals("Internet") || request.getWlanSSID().equals("Internet5G") || request.getWlanSSID().equals("Internet5Ga") || request.getWlanSSID().equals("Internet5Gb") || request.getWlanSSID().equals("Internet5Gc")) {
                        //check the broadcasted ssid is one of the known from infrastructure
                        mapRSSIDistance.put(request.getWlanSSID(), calculateDistance(Double.parseDouble(request.getRssi().substring(0, 2)), 2.4));
                    }
                }

            }

            ArrayList<Capture> filteredResponses = new ArrayList<>();
            for (Capture respons : value.getResponses()) {
                if (respons.getTime().equals(timeToCheck)) {//plus min
                    if (respons.getWlanSSID().equals("Internet") || respons.getWlanSSID().equals("Internet5G") || respons.getWlanSSID().equals("Internet5Ga") || respons.getWlanSSID().equals("Internet5Gb") || respons.getWlanSSID().equals("Internet5Gc")) {
                        // || respons.getWlanSSID().equals("Internet5Gc") is optional please check the file that has to be processed
                        filteredResponses.add(respons);
                        mapRSSIDistance.put(respons.getWlanSSID(), calculateDistance(Double.parseDouble(respons.getRssi().substring(0, 2)), 2.4));
                    }

                }

            }
            value.updateWithFileteredResponses(filteredResponses);
            pinPoint(key, timeToCheck, mapRSSIDistance);
            // System.out.println("pinpoint"+key);

        }
    }

    private static void pinPoint(String key, Date timeToCheck, Map<String, Double> mapRSSIDistance) {

        ArrayList<Trilat> trilat = new ArrayList<Trilat>();
        for (Map.Entry<String, Double> stringDoubleEntry : mapRSSIDistance.entrySet()) {
            //latlng
            //distance
            trilat.add(new Trilat(matchLocationSSID(stringDoubleEntry.getKey()), stringDoubleEntry.getValue()));

        }
        if (mapRSSIDistance.entrySet().size() >= 3) {
            System.out.println("trilat");
            mapRSSIDistance = sortbykey(mapRSSIDistance);
            LatLng locationByTrilateration = CSVReader.getLocationByTrilateration(trilat);
            System.out.println(counter + " MAC address: " + key + " at time of: " + timeToCheck + " was at:   " + locationByTrilateration.latitude + " " + locationByTrilateration.longitude + "   being connected to " + mapRSSIDistance.keySet().toString());
            counter++;
            records.get(key).setLatLng(locationByTrilateration);
            writeCSV(counter, key, timeToCheck, mapRSSIDistance.keySet().toArray(), mapRSSIDistance.values().toArray(), locationByTrilateration, records.get(key).getVendor());


        }

    }

    public static TreeMap<String, Double> sortbykey(Map<String, Double> map) {
        TreeMap<String, Double> sorted = new TreeMap<>();

        sorted.putAll(map);

        /*for (Map.Entry<String, Double> entry : sorted.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());*/
        return sorted;
    }

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private static void writeCSV(int counter, String key, Date timeToCheck, Object[] objects, Object[] toArray, LatLng locationByTrilateration, String vendor) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(counter);
        sb.append(",");
        sb.append(key);
        sb.append(",");
        sb.append(" ");
        sb.append(",");
        sb.append(objects[0]);
        sb.append(",");
        sb.append(objects[1]);
        sb.append(",");
        sb.append(objects[2]);
        sb.append(",");
        sb.append(toArray[0]);
        sb.append(",");
        sb.append(toArray[1]);
        sb.append(",");
        sb.append(toArray[2]);
        sb.append(",");
        sb.append(locationByTrilateration.latitude);
        sb.append(",");
        sb.append(locationByTrilateration.longitude);
        sb.append(",");
        sb.append(vendor);
        sb.append(",");


        pw.write(sb.toString());

    }

    public static void createRecords() {
        for (Capture captureFix : captures) {
            Record record = new Record(captureFix.getSourceMAC());

            for (Capture capture : captures) {
                if (record.getMacID().equals(capture.getSourceMAC())) {
                    if (capture.getCaptureType().equals(PROBE_REQUEST)) {

                        record.addToRequestList(capture);
                    }
                }
            }
            for (Capture capture : captures) {
                if (capture.getDestinationMAC().equals(record.getMacID())) {
                    if (capture.getCaptureType().equals(PROBE_RESPONSE)) {

                        record.addToResponseList(capture);
                    }
                }
            }
            records.put(record.getMacID(), record);
        }
    }

    public static double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (-69.44 - (levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }
}
