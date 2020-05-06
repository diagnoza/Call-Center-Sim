package mathsimproject;

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
        CEventList eventsList = new CEventList();

        Queue consumersQ = new Queue();
        Queue corporateQ = new Queue();

        Source consumerSrc = new Source(consumersQ, eventsList, "Consumers mathsimproject.Source");
        Source corporateSrc = new Source(corporateQ, eventsList, "Corporate customers mathsimproject.Source");

        // not sure we need two of these?
        Sink consumerSink = new Sink("Consumers mathsimproject.Sink");
        Sink corporateSink = new Sink("Corporate customers mathsimproject.Sink");

        Machine cpaConsumers = new Machine(consumersQ, consumerSink, eventsList, "CPA consumer");
        Machine cpaCorporate = new Machine(corporateQ, corporateSink, eventsList, "CPA corporate");
        Machine cpaCorporate2 = new Machine(consumersQ, consumerSink, eventsList, "CPA corporate 2");

        eventsList.start(2000);
    }

}
