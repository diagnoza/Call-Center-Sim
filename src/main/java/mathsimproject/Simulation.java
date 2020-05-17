package mathsimproject;

import java.util.Random;

/**
 * Example program for using eventlists
 *
 * @author Joel Karel
 * @version %I%, %G%
 */

public class Simulation {

    public CEventList list;
    public Queue queue;
    public Source source;
    public Sink sink;
    public Machine mach;

    // For testing purposes we want to have our own Random, that way we can have a fixed seed and test that
    // code changes don't break anything
    public static Random random;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        // Create an eventlist
//        mathsimproject.CEventList l = new mathsimproject.CEventList();
//        // A queue for the machine
//        mathsimproject.Queue q = new mathsimproject.Queue();
//        // A source
//        mathsimproject.Source s = new mathsimproject.Source(q, l, "mathsimproject.Source 1");
//        // A sink
//        mathsimproject.Sink si = new mathsimproject.Sink("mathsimproject.Sink 1");
//        // A machine
//        mathsimproject.Machine m = new mathsimproject.Machine(q, si, l, "mathsimproject.Machine 1");
//        // start the eventlist
//        l.start(2000); // 2000 is maximum time

        run();
    }


    private static void run() {
        // for testing purposes we want to have our own Random so we can set a fixed seed
        random = new Random();
        random.setSeed(22357L);

        CEventList eventsList = new CEventList();

        Queue consumersQ = new Queue();
        Queue corporateQ = new Queue();

        Source consumerSrc = new Source(consumersQ, eventsList, "Consumer Source");
        Source corporateSrc = new Source(corporateQ, eventsList, "Corporate Source", 30);

        Sink allCustomersSink = new Sink("Customers Sink");

        CorporateManager corporateManager = new CorporateManager(corporateQ, consumersQ, 2);

        // Shift: 6am - 2pm
        Machine cpaConsumers = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer", 6*60*60, 14*60*60);
        Machine cpaConsumers2 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer2", 6*60*60, 14*60*60);
        Machine cpaConsumers3= new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer3", 6*60*60, 14*60*60);
        Machine cpaCorporate = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate", 6*60*60, 14*60*60);
        Machine cpaCorporate2 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate2", 6*60*60, 14*60*60);
        Machine cpaCorporate3 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate3", 6*60*60, 14*60*60);

        // Shift: 2pm - 10pm
        Machine cpaConsumers4 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer",  14*60*60, 22*60*60);
        Machine cpaConsumers5 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer2",  14*60*60, 22*60*60);
        Machine cpaConsumers6 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer3",  14*60*60, 22*60*60);
        Machine cpaCorporate7 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate", 14*60*60, 22*60*60);
        Machine cpaCorporate8 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate2", 14*60*60, 22*60*60);
        Machine cpaCorporate9 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate3", 14*60*60, 22*60*60);

        // Shift: 10pm - 6am
        Machine cpaConsumers10 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer", 22*60*60, 6*60*60);
        Machine cpaConsumers11 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer2",  22*60*60, 6*60*60);
        Machine cpaConsumers12 = new Machine(consumersQ, allCustomersSink, eventsList, "CPA consumer3",  22*60*60, 6*60*60);
        Machine cpaCorporate13 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate", 22*60*60, 6*60*60);
        Machine cpaCorporate14 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate2", 22*60*60, 6*60*60);
        Machine cpaCorporate15 = new Machine(corporateManager, allCustomersSink, eventsList, "CPA corporate3", 22*60*60, 6*60*60);

        eventsList.start(2*24*60*60);

        System.out.println("\n" + stringProductStamps(allCustomersSink));
    }

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
}
