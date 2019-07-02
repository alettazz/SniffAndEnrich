package src.parser;

import src.helper.HistoryHolder;
import src.models.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RequestResponseParser {
    private static final String PROBE_REQUEST = "Probe Request";
    private static final String PROBE_RESPONSE = "Probe Response";
    private static final String BROADCAST = "ff:ff:ff:ff:ff:ff";
    private static final int TABLE_HEADER = 0;
    private static final int MINIMUM_CAPTURE_SIZE = 6;
    private static ArrayList<Capture> captures;
    private static int counter = 1;
    private static StringBuilder sb;
    private static LatLng previous = new LatLng(0, 0);
    private static PrintWriter pw = null;
    private static HashMap<String, Record> records = new HashMap<String, Record>();
    private static ArrayList<String> strangers = new ArrayList<>();
    private static Map<String, String> macVendors;
    private static PrintWriter pw2 = null;
    private static HashMap<String, LatLng> history = new HashMap<>();
    private static String historyFile;
    private static String outputFile;
    private static Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
    private static HashMap<String, List<Trilat>> rssiHeatmap = new HashMap<>();

    public static void run(String csvFile, String output, String historyFileParam) {

        //initializing the files
        historyFile = historyFileParam;
        outputFile = output;

        String line = "";
        String cvsSplitBy = ",";
        captures = new ArrayList<>();

        initOUI();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] split = line.split(cvsSplitBy);
                //note: depending on input file captures may differ in timeformat and delimiter!
                if (split.length > MINIMUM_CAPTURE_SIZE) {
                    Capture capture = new Capture(split[0], split[1], split[2], split[3], split[4], split[5], split[6], split[7], split[8], split[9], split[10], split[11]);
                    captures.add(capture);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        captures.remove(TABLE_HEADER);
        System.out.print("Capture number: " + captures.size());

        Collections.sort(captures, new Capture.CaptureComparator());

        initHeatmap();

        System.out.print("Start time  ");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        System.out.println(sdf.format(cal.getTime()));

        createRecords();

        System.out.println("Records created " + records.size());

        initFile();

        processRecordIntoActivity();

        initHistoryFile();

        createHistory();

        writeHistoryToFile();

        pw.close();
        pw2.close();
        System.out.println("Files closed");


        Calendar cal1 = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        System.out.println("End time" + sdf1.format(cal1.getTime()));


    }

    private static void initHeatmap() {

        String csvFile = "/Users/alettatordai/IdeaProjects/SniffAndEnrich/heatmap_1.csv";
        String line = "";
        String cvsSplitBy = ";";


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            while ((line = br.readLine()) != null) {

                String[] split = line.split(cvsSplitBy);

                List<Trilat> trilats = new ArrayList<>();
                if (!rssiHeatmap.containsKey(split[0])) {

                    trilats.add(new Trilat(split[1], split[2], split[0]));

                    rssiHeatmap.put(split[0], trilats);

                } else {

                    trilats = rssiHeatmap.get(split[0]);
                    trilats.add(new Trilat(split[1], split[2], split[0]));
                    rssiHeatmap.put(split[0], trilats);

                }


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initHistoryFile() {

        try {
            System.out.println(historyFile);
            pw2 = new PrintWriter(new File(historyFile));

            sb = new StringBuilder();
            sb.append("mac_address"); //key
            sb.append(',');
            sb.append("vendor");
            sb.append(',');
            sb.append("time");
            sb.append(',');
            sb.append("LAT");
            sb.append(',');
            sb.append("LON");
            sb.append(',');

            pw2.write(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void writeHistoryToFile() {

        HistoryHolder.getInstance().cleanHistoryFromDuplicateEntries();
        for (Map.Entry<String, ArrayList<LatLng>> stringArrayListEntry : HistoryHolder.getInstance().getHistory().entrySet()) {

            String key = stringArrayListEntry.getKey();
            for (LatLng latLng : stringArrayListEntry.getValue()) {

                //checkIfInBuildingsRange
                if (latLng.getLatitude() > 46 && latLng.getLatitude() < 47 && latLng.getLongitude() < 25 && latLng.getLongitude() > 24) {

                    calendar.setTime(latLng.getDate());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);// gets hour in 24h format
                    int minute = calendar.get(Calendar.MINUTE);// gets hour in 24h format
                    int second = calendar.get(Calendar.SECOND);// gets hour in 24h format

                    String properDate = buildTime(hour, minute, second);
                    Date date = new Date(properDate);

                    sb = new StringBuilder();
                    sb.append("\n");
                    sb.append(key);
                    sb.append(",");
                    sb.append(matchMACtoVendor(key));
                    sb.append(",");
                    sb.append(date.getTime());
                    sb.append(",");
                    sb.append(latLng.getLatitude());
                    sb.append(",");
                    sb.append(latLng.getLongitude());
                    sb.append(",");
                    pw2.write(sb.toString());
                }
            }
        }
    }

    private static String buildTime(int hour, int minute, int second) {
        Calendar currentCalendar = GregorianCalendar.getInstance();

        //set manually the month and day for caputer that dont contain timestamp
        currentCalendar.set(2019, Calendar.FEBRUARY, 26, hour - 12, minute, second);
        currentCalendar.set(Calendar.MONTH, Calendar.FEBRUARY);
        String format;
        format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").format(currentCalendar.getTime());

        if (currentCalendar.get(Calendar.MONTH) != Calendar.FEBRUARY) {

            System.out.println("stpd " + currentCalendar.get(Calendar.MONTH));

        }
        return format;
    }

    private static void createHistory() {
        for (Map.Entry<String, Record> entry : records.entrySet()) {
            String key = entry.getKey();
            records.get(key).resetUsed();

            Record value = entry.getValue();
            Date timeToCheck = new Date();

            for (Capture request : value.getRequests()) {

                timeToCheck = request.getTime();
                ArrayList<Capture> toTrilat = new ArrayList<>();
                String checkWlan = "";

                if (!value.getResponses().isEmpty() && checkProperWlan(value.getResponses().get(0).getWlanSSID())) {
                    checkWlan = value.getResponses().get(0).getWlanSSID();
                }

                for (int i = 0; i < value.getResponses().size(); i++) {
                    Date dateToCheck = value.getResponses().get(i).getTime();
                    long t = timeToCheck.getTime();
                    Date startDate = new Date(t - 10 * (6000));
                    Date endDate = new Date(t + 10 * (6000));

                    if (!dateToCheck.before(startDate) && !dateToCheck.after(endDate)) {

                        if (!checkWlan.equals(value.getResponses().get(i).getWlanSSID()) && checkProperWlan(value.getResponses().get(i).getWlanSSID())) {
                            toTrilat.add(value.getResponses().get(i));
                            value.getResponses().remove(i);
                        }

                        if (!value.getResponses().isEmpty() && i > 0 && value.getResponses().size() > i) {
                            toTrilat.add(value.getResponses().get(i - 1));
                            checkWlan = value.getResponses().get(i - 1).getWlanSSID();
                        }

                    }
                    if (toTrilat.size() >= 3) {
                        writeHistory(key, timeToCheck, toTrilat);
                        toTrilat = new ArrayList<>();
                    }
                }

            }
        }

    }

    private static boolean checkProperWlan(String wlanSSID) {
        return wlanSSID.equals("Internet") || wlanSSID.equals("Internet5Ga") || wlanSSID.equals("Internet5Gb") || wlanSSID.equals("Internet5Gc") ||
                wlanSSID.equals("Internet5G") /* || wlanSSID.equals("Pince2")*/;
    }


    private static void writeHistory(String key, Date timeToCheck, ArrayList<Capture> toTrilat) {

        ArrayList<Trilat> trilat = new ArrayList<Trilat>();
        for (Capture capture : toTrilat) {

            trilat.add(new Trilat(matchLocationSSID(capture.getWlanSSID()), calculateDistance(Double.parseDouble(capture.getRssi().substring(0, 3))), Double.parseDouble(capture.getRssi().substring(0, 3))));
        }
        LatLng locationByTrilateration;

        double[] locationByTrilaterationRes = CSVReader.getLocationByMultipleTrilateration(trilat);
        LatLng locationByTrilaterationRes2 = CSVReader.getLocationByTrilateration(trilat);

        Date historyTime = getCurrentElementTime(toTrilat);

        //the old way of retrieving location with trilateration
        locationByTrilateration = new LatLng(locationByTrilaterationRes2.getLatitude(), locationByTrilaterationRes2.getLongitude(), timeToCheck);

        //heatmap values used for trilateration
        locationByTrilateration = new LatLng(locationByTrilaterationRes[0], locationByTrilaterationRes[1], timeToCheck);

        if (locationByTrilateration.getLongitude() != 0 && locationByTrilateration.getLatitude() != 0 && historyTime != null) {

            //adding the entry to the local history database
            HistoryHolder.getInstance().addHistoryEntryToMac(key, locationByTrilateration);

            history.put(key, locationByTrilateration);

        }

    }

    private static Date getCurrentElementTime(ArrayList<Capture> trilat) {

        for (Capture capture : trilat) {
            if (!capture.isUsed()) {
                capture.setUsed(true);
                return capture.getTime();
            }
        }
        return null;
    }

    private static void initOUI() {

        String csvFile = "/Users/alettatordai/IdeaProjects/SniffAndEnrich/vendorMacs.csv";
        String line = "";
        String cvsSplitBy = ";";
        macVendors = new HashMap<String, String>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] split = line.split(cvsSplitBy);
                Vendor vendor = new Vendor(split[0], split[1]);
                macVendors.put(vendor.getMacPreffix(), vendor.getVendor());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void initFile() {


        try {
            System.out.println(outputFile);
            pw = new PrintWriter(new File(outputFile));

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

            pw.write(sb.toString());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static LatLng matchLocationSSID(String SSID) {
        LatLng latlng = new LatLng(0, 0, new Date());
        switch (SSID) {
            case "Internet":
                latlng = new LatLng(46.522929, 24.598748, new Date());
                break;
            case "Internet5G":
                latlng = new LatLng(46.555, 24.601, new Date());
                break;
            case "Internet5Ga":
                latlng = new LatLng(46.522743, 24.598609, new Date());
                break;
            case "Internet5Gb":
                latlng = new LatLng(46.523024, 24.598556, new Date());
                break;
            case "SapiPark":
                latlng = new LatLng(46.602, 24.598, new Date());
                break;
            case "Pince2":
                latlng = new LatLng(46.423044, 24.598436, new Date());
                break;
            case "Emikroszkop":
                latlng = new LatLng(46.523044, 24.598436, new Date());
                break;
            case "Internet5Gc":
                latlng = new LatLng(46.523038, 24.598767, new Date());
                break;
        }
        return latlng;

    }

    private static void processRecordIntoActivity() {

        double rssi = 0;

        for (Map.Entry<String, Record> entry : records.entrySet()) {
            String key = entry.getKey();
            String vendor = matchMACtoVendor(key);

            Record value = entry.getValue();
            Date timeToCheck = new Date();

            Map<String, Double> mapRSSIDistance = new HashMap<>();


            if (entry.getValue().getRequests().size() == 1 && !entry.getValue().getRequests().get(0).getWlanSSID().equals("Internet")) {
                if (entry.getValue().getRequests().get(0).getWlanSSID().equals("")) {
                    strangers.add(entry.getValue().getRequests().get(0).getSourceMAC());

                }
            }

            //statistics for the most common devices

            if (vendor.contains("Samsumg")) {
                System.out.println("Samsung" + value.getResponses().size());
            }
            if (vendor.contains("HUAWEI")) {
                System.out.println("Huawei" + value.getResponses().size());
            }
            if (vendor.contains("Appl")) {
                System.out.println("Apple" + value.getResponses().size());
            }

            for (Capture request : value.getRequests()) {

                if (request.getDestinationMAC().equals(BROADCAST)) {
                    timeToCheck = request.getTime();

                    if (/*request.getWlanSSID().equals("Emikroszkop") ||*/ request.getWlanSSID().equals("Internet") || request.getWlanSSID().equals("Internet5G") || request.getWlanSSID().equals("Internet5Ga") || request.getWlanSSID().equals("Internet5Gb") || request.getWlanSSID().equals("Internet5Gc")) {
                        //check the broadcasted ssid is one of the known from infrastructure

                        rssi = Double.parseDouble(request.getRssi().substring(0, 3));
                        mapRSSIDistance.put(request.getWlanSSID(), calculateDistance(Double.parseDouble(request.getRssi().substring(0, 2))));
                    }
                }


                ArrayList<Capture> filteredResponses = new ArrayList<>();
                for (Capture respons : value.getResponses()) {
                    if (respons.getTime().equals(timeToCheck)) {//plus min
                        if (respons.getWlanSSID().equals("Emikroszkop") || respons.getWlanSSID().equals("Internet") || respons.getWlanSSID().equals("Internet5G") || respons.getWlanSSID().equals("Internet5Ga") || respons.getWlanSSID().equals("Internet5Gb") || respons.getWlanSSID().equals("Internet5Gc")) {
                            // || respons.getWlanSSID().equals("Internet5Gc") is optional please check the file that has to be processed
                            filteredResponses.add(respons);
                            mapRSSIDistance.put(respons.getWlanSSID(), calculateDistance(Double.parseDouble(respons.getRssi().substring(0, 2))));
                        }

                    }
                    rssi = Double.parseDouble(respons.getRssi().substring(0, 3));

                }
                value.updateWithFileteredResponses(filteredResponses);

                pinPoint(key, timeToCheck, mapRSSIDistance, rssi);
            }


        }
    }

    private static void pinPoint(String key, Date timeToCheck, Map<String, Double> mapRSSIDistance, double rssiValue) {

        ArrayList<Trilat> trilat = new ArrayList<Trilat>();
        for (Map.Entry<String, Double> stringDoubleEntry : mapRSSIDistance.entrySet()) {
            //latlng and distance
            trilat.add(new Trilat(matchLocationSSID(stringDoubleEntry.getKey()), stringDoubleEntry.getValue(), rssiValue));

        }

        if (mapRSSIDistance.entrySet().size() >= 3) {

            System.out.println("trilat");
            mapRSSIDistance = sortbykey(mapRSSIDistance);

            double[] locationByTrilaterationRes = CSVReader.getLocationByMultipleTrilateration(trilat);

            LatLng locationByTrilateration = new LatLng(locationByTrilaterationRes[0], locationByTrilaterationRes[1], new Date());
            counter++;
            records.get(key).setLatLng(locationByTrilateration);
            records.get(key).setVendor(matchMACtoVendor(key));
            System.out.println(counter + " MAC address: " + key + " at time of: " + timeToCheck + " was at:   " + locationByTrilateration.getLatitude() + " " + locationByTrilateration.getLongitude() + "   being connected to " + mapRSSIDistance.keySet().toString() + " having a device of " + records.get(key).getVendor());
            if (previous != locationByTrilateration) {

                writeCSV(counter, key, new Date(), mapRSSIDistance.keySet().toArray(), mapRSSIDistance.values().toArray(), locationByTrilateration, records.get(key).getVendor());
            } else {
                previous = locationByTrilateration;
            }

        }

    }

    private static String matchMACtoVendor(String key) {
        String s = macVendors.get(key.substring(0, 8).toUpperCase());
        // System.out.println(key + " " + s);

        if (s == null) {
            s = "";
        }
        return s;
    }

    public static TreeMap<String, Double> sortbykey(Map<String, Double> map) {
        TreeMap<String, Double> sorted = new TreeMap<>();

        sorted.putAll(map);

        return sorted;
    }

    private static void writeCSV(int counter, String key, Date timeToCheck, Object[] objects, Object[] toArray, LatLng locationByTrilateration, String vendor) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n");
        sb.append(counter);
        sb.append(",");
        sb.append(key);
        sb.append(",");
        sb.append(vendor);
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
        sb.append(locationByTrilateration.getLatitude());
        sb.append(",");
        sb.append(locationByTrilateration.getLongitude());
        sb.append(",");


        pw.write(sb.toString());

    }

    public static void createRecords() {
        for (Capture captureFix : captures) {
            Record record = new Record(captureFix.getSourceMAC());
            record.setVendor(matchMACtoVendor(record.getMacID()));

            for (Capture capture : captures) {
                if (record.getMacID().equals(capture.getSourceMAC())) {
                    //if (capture.getCaptureType().equals(PROBE_REQUEST)) {
                    record.addToRequestList(capture);
                    // }
                }
            }
            for (Capture capture : captures) {
                if (capture.getDestinationMAC().equals(record.getMacID())) {
                    if (capture.getCaptureType().equals(PROBE_RESPONSE)) {

                        if (checkProperWlan(capture.getWlanSSID())) {

                            record.addToResponseList(capture);
                        }
                    }
                }
            }

            records.put(record.getMacID(), record);
        }
    }

    public static double calculateDistance(double rssi) {
        double txPower = -59;//hard coded power value. Usually ranges between -59 to -65

        if (rssi == 0) {
            return -1.0;
        }

        double ratio = rssi * 1.0 / txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            return (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    public static ArrayList<Capture> getCaptures() {
        return captures;
    }

    public static HashMap<String, List<Trilat>> getRssiHeatmap() {
        return rssiHeatmap;
    }
}
