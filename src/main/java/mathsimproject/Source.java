package mathsimproject;

/**
 * A source of products
 * This class implements CProcess so that it can execute events.
 * By continuously creating new events, the source keeps busy.
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class Source implements CProcess {
    /**
     * Eventlist that will be requested to construct events
     */
    private CEventList list;
    /**
     * mathsimproject.Queue that buffers products for the machine
     */
    private ProductAcceptor queue;
    /**
     * Name of the source
     */
    private String name;
    /**
     * Mean interarrival time
     */
    private double meanArrTime;
    /**
     * Interarrival times (in case pre-specified)
     */
    private double[] interarrivalTimes;
    /**
     * Interarrival time iterator
     */
    private int interArrCnt;

    /**
     * Constructor, creates objects
     * Interarrival times are exponentially distributed with mean 33
     *
     * @param q The receiver of the products
     * @param l The eventlist that is requested to construct events
     * @param n Name of object
     */
    public Source(ProductAcceptor q, CEventList l, String n) {
        list = l;
        queue = q;
        name = n;
        meanArrTime = 33;
        // put first event in list for initialization
        list.add(this, 0, drawRandomExponential(meanArrTime)); //target,type,time
    }

    /**
     * Constructor, creates objects
     * Interarrival times are exponentially distributed with specified mean
     *
     * @param q The receiver of the products
     * @param l The eventlist that is requested to construct events
     * @param n Name of object
     * @param m Mean arrival time
     */
    public Source(ProductAcceptor q, CEventList l, String n, double m) {
        list = l;
        queue = q;
        name = n;
        meanArrTime = m;
        // put first event in list for initialization
        list.add(this, 0, drawRandomExponential(meanArrTime)); //target,type,time
    }

    /**
     * Constructor, creates objects
     * Interarrival times are prespecified
     *
     * @param q  The receiver of the products
     * @param l  The eventlist that is requested to construct events
     * @param n  Name of object
     * @param ia interarrival times
     */
    public Source(ProductAcceptor q, CEventList l, String n, double[] ia) {
        list = l;
        queue = q;
        name = n;
        meanArrTime = -1;
        interarrivalTimes = ia;
        interArrCnt = 0;
        // put first event in list for initialization
        list.add(this, 0, interarrivalTimes[0]); //target,type,time
    }

    @Override
    public void execute(int type, double tme) {
        // show arrival
        System.out.println("Arrival (" + name + ") at time = " + tme);
        // give arrived product to queue
        Product p = new Product(this.name.equals("Corporate Source"));
        p.stamp(tme, "Creation", name);
        queue.giveProduct(p);
        // generate duration
        if (meanArrTime > 0) {
            // This is hacky but the simplest way to do it as far as I'm concerned. Generates next arrival time based
			// on type customer this source is for.
			double duration = 0;
			if (this.name.equals("Consumer Source")) {
				double lambda_t = 2.0/60 + 1.8/60*Math.sin((54000 + tme)*2*Math.PI/86400);
				duration = drawNonStationaryExponential(3.8/60, lambda_t);

			} else if (this.name.equals("Corporate Source")){
				if (tme >= 8*60*60 && tme <= 18*60*60)
					duration = drawRandomExponential(60);
				else
					duration = drawRandomExponential(60/0.2);
			} else {
                throw new IllegalArgumentException("The name of the source has to be" +
                        " \"Consumer Source\" or \"Corporate Source\"");
            }
            // Create a new event in the eventlist
            list.add(this, 0, tme + duration); //target,type,time
        } else {
            interArrCnt++;
            if (interarrivalTimes.length > interArrCnt) {
                list.add(this, 0, tme + interarrivalTimes[interArrCnt]); //target,type,time
            } else {
                list.stop();
            }
        }
    }

    public static double drawRandomExponential(double mean) {
        // draw a [0,1] uniform distributed number
        double u = Simulation.random.nextDouble();
        //double u = Math.random();
        // Convert it into a exponentially distributed random variate with mean 33
        double res = -mean * Math.log(u);
        return res;
    }

    public static double drawNonStationaryExponential(double lambda_s, double lambda_t) {
		// draw two [0,1] uniform distributed number
		double u1 = Simulation.random.nextDouble();
		double u2 = Simulation.random.nextDouble();

		// Return exponentially distributed random variate
		if (u1 <= lambda_t/lambda_s)
			return -1/lambda_s*Math.log(u2);
		// Draw again
		else
			return drawNonStationaryExponential(lambda_s, lambda_t);
	}
}