# SniffAndEnrich
SniffAndEnrich is a university thesis project which uses already existing tools and programms to mine Wifi data and to extract and scale into personal trackable routes.
It's main script the probeSniffer(brannondorsey/sniff-probe) is the core to gather all the probe request further on t o be worked with. This project is more about processing the data. >
# The DatabaseCleaner 
> - takes a csv file to organize probe requests and filter unrelevant data 
> - builds activities for a device seen event and writes it to a new .CSV row 
# SniffAndEnrich v2.0
Main script of sniffing been changed 
> Captures packets with tcpdump on Kali distribution in monitor mode:
> - tcpdump -i wlan0(monitormode nic) -w output.pcap -e -s 256 type mgt subtype probe-req or subtype-probe resp 
 - exports csv parsed with fields: 
> Frame: frame.time, frame.time_delta, frame.protocols(radiotap:wlan_radio:wlan)
> Radiotap: wlan_radio.channel, wlan_radio.frequency, wlan_radio.signal_dbm, wlan_radio.duration
> Wlan: wlan.fc.type_subtype(4 - probe request, 5 - probe response), wlan.ra_resolved , wlan.bssid_resolved
> Wlan.tag : Vendor Extension,  Device Name, Primary Device Type, wps.primary_device_type.subcategory_telephone
 - creates records for all distinct mac addresses
 - matches probes and responses to a mac addresses connection activity
 - based on the known AP LatLngs calculates using Trilateration the physical position of a mac address beholder in the minute of probing
 - lists location currently only on system output

# SniffAndEnrich

