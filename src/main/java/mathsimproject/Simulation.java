package mathsimproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Example program for using eventlists
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

public class Simulation {

    // For testing purposes we want to have our own Random, that way we can have a fixed seed and test that
    // code changes don't break anything
    public static Random random;
    // Time at which we open our service for the first time
    public final double STARTING_TIME = 6 * 60 * 60;
    public CEventList eventsList;
    public Queue consumersQ;
    public Queue corporateQ;
    public Source consumersSource;
    public Source corporateSource;
    public Sink allSink;
    public CorporateManager corporateManager;

    // WIP: we could use this to then produce a file for MATLAB to read
    private static StringBuilder stringProductStamps(Sink sink) {
        // not sure why getNumbers(), getTimes() & getEvents() are implemented like that
        // why not just return numbers.clone()?

        StringBuilder stringBuilder = new StringBuilder();

        // all events have the same three timestamps (at least for now)
        stringBuilder.append(" Call arrival, Call taken by CPA, Call ended\n");
        for (Product p : sink.getProducts()) {
            stringBuilder.append(p.getTimes());
            stringBuilder.append(p.getStations());
            stringBuilder.append("\n");
        }

        return stringBuilder;
    }

    private static void exportToCsv(List<Product> products, String filename) {

        try (PrintWriter writer = new PrintWriter(new File(filename))) {

            StringBuilder sb = new StringBuilder();
            sb.append(" Call arrival, Call taken by CPA, Call ended, handled by:\n");
            for (Product p : products) {
                sb.append(p.getTimes().get(0));
                sb.append(",");
                sb.append(p.getTimes().get(1));
                sb.append(",");
                sb.append(p.getTimes().get(2));
                sb.append(",");
                sb.append(p.getStations().get(1));
                sb.append("\n");
            }


            writer.write(sb.toString());


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

    }

    public void run() {
        // for testing purposes we want to have our own Random so we can set a fixed seed
        random = new Random();
        random.setSeed(22357L);

        // we open our doors for the first time at 6am
        eventsList = new CEventList(STARTING_TIME);

        consumersQ = new Queue();
        corporateQ = new Queue();

        consumersSource = new Source(consumersQ, eventsList, "Consumer Source");
        corporateSource = new Source(corporateQ, eventsList, "Corporate Source");

        allSink = new Sink("Customers Sink");

        corporateManager = new CorporateManager(corporateQ, consumersQ, 2);

        hireCPAs(Cpa_Type.Corporate, 5, "CPA corporate 6am-2pm", 6 * 60 * 60);
        hireCPAs(Cpa_Type.Corporate, 5, "CPA corporate 2pm-10pm", 14 * 60 * 60);
        hireCPAs(Cpa_Type.Corporate, 3, "CPA corporate 10pm-6am", 22 * 60 * 60);
        hireCPAs(Cpa_Type.Consumer, 8, "CPA consumer 6am-2pm", 6 * 60 * 60);
        hireCPAs(Cpa_Type.Consumer, 8, "CPA consumer 2pm-10pm", 14 * 60 * 60);
        hireCPAs(Cpa_Type.Consumer, 5, "CPA consumer 10pm-6am", 22 * 60 * 60);

        // 30 days: 30*24 + 6 hours (when we open our service for the first time)
        // TODO: requirements aren't met if length of run is low (few days). Something wrong with STARTING_TIME?
        //       nothing wrong?
        eventsList.start(STARTING_TIME + 100 * 24 * 60 * 60);
        System.out.println("\n" + stringProductStamps(allSink));

        Map<String, Double> reqs = computeRequirements(allSink);

        System.out.println("Requirements: " + reqs + "\n");
        System.out.println("Meets requirements: " + verifyRequirements(reqs));


        // export data for analysis in Matlab
        ArrayList<Product> consumers = new ArrayList<>();
        ArrayList<Product> corporate = new ArrayList<>();
        for (Product p : allSink.getProducts()) {
            if (p.getStations().get(0).equals("Consumer Source"))
                consumers.add(p);
            else
                corporate.add(p);
        }
        exportToCsv(consumers, "consumers.csv");
        exportToCsv(corporate, "corporate.csv");
    }

    /**
     * Schedules events for the creation of machines for the first time. These events in turn will schedule more events
     * for the destruction of machines at the end of their shift and their re-start the next day.
     *
     * @param type
     * @param numberCPAs
     * @param shiftName
     * @param firstShiftTime
     */
    private void hireCPAs(Cpa_Type type, int numberCPAs, String shiftName, double firstShiftTime) {
        // who shall the machine request calls from:
        RequestAcceptor requestAcceptor = null;
        if (type == Cpa_Type.Corporate) {
            // Corporate Agents (CPA corporate) request calls from CorporateManager
            requestAcceptor = corporateManager;
        } else if (type == Cpa_Type.Consumer) {
            // Consumer Agents (CPA consumer) request calls from consumer queue (no middle man)
            requestAcceptor = consumersQ;
        }

        for (int count = 0; count < numberCPAs; count++) {
            eventsList.add(new MachineScheduler(eventsList, requestAcceptor, allSink, shiftName + " #" + (count + 1)), 0, firstShiftTime);
        }
    }

    /**
     * Computes the requirements for the different times specified in project PDF
     *
     * @param sink
     * @return: e.g.:
     * {
     * "Corporate 3 min": 0.54,
     * "Consumer 5 min": 0.23124,
     * "Consumer 10 min": 0.23164,
     * "Corporate 7 min": 0.68
     * }
     */
    private Map<String, Double> computeRequirements(Sink sink) {
        // NOTE: This is probably not a good way of doing it. I just started writing one of the cases with streams and
        // then it just was easier to repeat the process. Doing the Map thing in case it helps with debugging/testing
        // later on, might be silly too.
        Map<String, Double> retMap = new HashMap<>();

        // Note: Unless we change the Product.stamping (I think), this is what we'll have:
        //   product.getTimes().get(0): call arrival
        //   product.getTimes().get(1): call taken by machine (start)
        //   product.getTimes().get(2): call finished (end)

        // Corporate requirements:
        double totalCorporate = sink.getProducts()
                .stream()
                .filter(p -> p.getStations().get(0).equals("Corporate Source"))
                .count();

        double corpLess3min = sink.getProducts().stream()
                .filter(p -> p.getStations().get(0).equals("Corporate Source"))
                .filter(p -> p.getTimes().get(1) - p.getTimes().get(0) < 3 * 60)
                .count();

        double corpLess7min = sink.getProducts().stream()
                .filter(p -> p.getStations().get(0).equals("Corporate Source"))
                .filter(p -> p.getTimes().get(1) - p.getTimes().get(0) < 7 * 60)
                .count();

        retMap.put("Corporate 3 min", corpLess3min / totalCorporate);
        retMap.put("Corporate 7 min", corpLess7min / totalCorporate);

        // Consumer requirements:
        double totalConsumers = sink.getProducts()
                .stream()
                .filter(p -> p.getStations().get(0).equals("Consumer Source"))
                .count();

        double conLess5min = sink.getProducts().stream()
                .filter(p -> p.getStations().get(0).equals("Consumer Source"))
                .filter(p -> p.getTimes().get(1) - p.getTimes().get(0) < 5 * 60)
                .count();

        double conLess10min = sink.getProducts().stream()
                .filter(p -> p.getStations().get(0).equals("Consumer Source"))
                .filter(p -> p.getTimes().get(1) - p.getTimes().get(0) < 10 * 60)
                .count();

        retMap.put("Consumer 5 min", conLess5min / totalConsumers);
        retMap.put("Consumer 10 min", conLess10min / totalConsumers);

        return retMap;
    }

    // NOTE: This is ugly.
    //       This Map parameter is probably not a good idea.. Might aid in testing/debugging but not good design
    private boolean verifyRequirements(Map<String, Double> requirementsMap) {
        if (requirementsMap.getOrDefault("Consumer 5 min", 0.0) < 0.9) return false;
        if (requirementsMap.getOrDefault("Consumer 10 min", 0.0) < 0.95) return false;
        if (requirementsMap.getOrDefault("Corporate 3 min", 0.0) < 0.95) return false;
        if (requirementsMap.getOrDefault("Corporate 7 min", 0.0) < 0.99) return false;

        // In java 0.0/0.0 produces NaN, not an exception. And I guess NaN > 0.9 given that if I don't do this it meets
        // the requirements
        for (double val : requirementsMap.values()) {
            if (Double.isNaN(val))
                return false;
        }

        return true;
    }
}


