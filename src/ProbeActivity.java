import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProbeActivity {

    private ArrayList<Probe> probes=  new ArrayList<>();

    public ProbeActivity() {
    }

    public void addProbe(Probe probes) {
        this.probes.add(probes);
    }

    public void orderActivitiesByDate(){
        Collections.sort(probes,new ProbeActivityComparator());

    }

    public ArrayList<Probe> getProbes() {
        return probes;
    }

    public void setProbes(ArrayList<Probe> probes) {
        this.probes = probes;
    }

    @Override
    public String toString() {
        return "ProbeActivity{" +
                "probes=" + probes +
                '}';
    }
    public static class ProbeActivityComparator implements Comparator<Probe> {
        @Override
        public int compare(Probe o1, Probe o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
    }
