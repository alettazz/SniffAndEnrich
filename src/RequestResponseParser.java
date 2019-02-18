import java.io.*;
import java.util.*;

public class RequestResponseParser {
    private static final String PROBE_REQUEST = "Probe Request";
    private static final String PROBE_RESPONSE = "Probe Response";
    private static final String BROADCAST = "ff:ff:ff:ff:ff:ff";
    private static ArrayList<Capture> captures;
    private static int counter = 1;
    private static StringBuilder sb;
    static PrintWriter pw = null;

    private static HashMap<String, Record> records = new HashMap<String, Record>();

    public static void main(String[] args) {
        String csvFile = "C:/Users/Aletta/Desktop/sniffngo/MERESEK/FinalCulomnizationCPY.csv";
        String line = "";
        String cvsSplitBy = ",";
        captures = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] split = line.split(cvsSplitBy);
                Capture capture = new Capture(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9], split[10], split[11], split[12], split[13], split[14]);
                captures.add(capture);
                //System.out.println(capture);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        captures.remove(0);

        Collections.sort(captures, new Capture.CaptureComparator());

        System.out.println("kezdet");

        createRecords();

        System.out.println("vege" + records.size());

        initFile();

        processRecordIntoActivity();

        pw.close();
    }

    private static void initFile() {
        try {
            pw = new PrintWriter(new File("test_dist.csv"));
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
                latlng = new LatLng(46.522764, 24.598590);
                break;
            case "Internet5Gb":
                latlng = new LatLng(46.523028, 24.598560);
                break;
            case "SapiPark":
                latlng = new LatLng(46.522764, 24.598590);
                break;
            case "Pince2":
                latlng = new LatLng(46.523044, 24.598436);
                break;
            case "Emikroszkop":
                latlng = new LatLng(46.522748, 24.598819);
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
            for (Capture request : value.getRequests()) {

                if (request.getDestinationMAC().equals(BROADCAST)) {
                    timeToCheck = request.getTime();
                    if (request.getWlanSSID().equals("Internet")) {

                        mapRSSIDistance.put(request.getWlanSSID(), calculateDistance(Double.parseDouble(request.getRssi().substring(0, 2)), 2.4));
                    }
                }
            }
            for (Capture respons : value.getResponses()) {
                if (respons.getTime().equals(timeToCheck)) {//plus min
                    if (!respons.getWlanSSID().equals("Emikroszkop")) {

                        mapRSSIDistance.put(respons.getWlanSSID(), calculateDistance(Double.parseDouble(respons.getRssi().substring(0, 2)), 2.4));
                    }

                }

            }
            pinPoint(key, timeToCheck, mapRSSIDistance);
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
            mapRSSIDistance = sortbykey(mapRSSIDistance);
            LatLng locationByTrilateration = CSVReader.getLocationByTrilateration(trilat);
            System.out.println(counter + " MAC address: " + key + " at time of: " + timeToCheck + " was at:   " + locationByTrilateration.latitude + " " + locationByTrilateration.longitude + "   being connected to " + mapRSSIDistance.keySet().toString());
            counter++;
            writeCSV(counter, key, timeToCheck, mapRSSIDistance.keySet().toArray(), mapRSSIDistance.values().toArray(), locationByTrilateration);


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

    private static void writeCSV(int counter, String key, Date timeToCheck, Object[] objects, Object[] toArray, LatLng locationByTrilateration) {
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
