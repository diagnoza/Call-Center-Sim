package mathsimproject;

/**
 * Machine in a factory
 *
 * @author Joel Karel
 * @version %I%, %G%
 */
public class Machine implements CProcess, ProductAcceptor {
    /**
     * Product that is being handled
     */
    private Product product;
    /**
     * Eventlist that will manage events
     */
    private final CEventList eventlist;
    /**
     * Queue from which the machine has to take products
     */
    private RequestAcceptor queue;
    /**
     * Sink to dump products
     */
    private ProductAcceptor sink;
    /**
     * Status of the machine (b=busy, i=idle)
     */
    private char status;
    /**
     * Machine name
     */
    private final String name;
    /**
     * Mean processing time
     */
    private double meanProcTime;
    /**
     * Processing times (in case pre-specified)
     */
    private double[] processingTimes;
    /**
     * Processing time iterator
     */
    private int procCnt;

    /**
     * Machine starts working at this timepoint
     */
    private double startTime;
    /**
     * Machine stops working at this timepoint
     */
    private double finishTime;

    /**
     * The simulation runs for this number of days
     */
    private int numDays;

    private boolean stop;

    /**
     * Constructor
     * Service times are exponentially distributed with mean 30
     *
     * @param q mathsimproject.Queue from which the machine has to take products
     * @param s Where to send the completed products
     * @param e Eventlist that will manage events
     * @param n The name of the machine
     */
    public Machine(RequestAcceptor q, ProductAcceptor s, CEventList e, String n) {
        status = 'i';
        queue = q;
        sink = s;
        eventlist = e;
        name = n;
        meanProcTime = 30;
        stop = false;
        queue.askProduct(this);
    }

    /**
     * Constructor
     * Service times are exponentially distributed with specified mean
     *
     * @param q mathsimproject.Queue from which the machine has to take products
     * @param s Where to send the completed products
     * @param e Eventlist that will manage events
     * @param n The name of the machine
     * @param m Mean processing time
     */
    public Machine(RequestAcceptor q, ProductAcceptor s, CEventList e, String n, double m) {
        status = 'i';
        queue = q;
        sink = s;
        eventlist = e;
        name = n;
        meanProcTime = m;
        queue.askProduct(this);
    }

    /**
     * Constructor
     * Service times are pre-specified
     *
     * @param q  mathsimproject.Queue from which the machine has to take products
     * @param s  Where to send the completed products
     * @param e  Eventlist that will manage events
     * @param n  The name of the machine
     * @param st service times
     */
    public Machine(RequestAcceptor q, ProductAcceptor s, CEventList e, String n, double[] st) {
        status = 'i';
        queue = q;
        sink = s;
        eventlist = e;
        name = n;
        meanProcTime = -1;
        processingTimes = st;
        procCnt = 0;
        queue.askProduct(this);
    }

    /**
     * Method to have this object execute an event
     *
     * @param type The type of the event that has to be executed
     * @param tme  The current time
     */
    public void execute(int type, double tme) {
        // show arrival
//        System.out.println("Product finished (by " + name + ") at time = " + tme);
        // Remove product from system
        product.stamp(tme, "Production complete", name);
        sink.giveProduct(product);
        product = null;
        // set machine status to idle
        status = 'i';
        // Ask the queue for products
        if (!stop)
            queue.askProduct(this);
    }

    /**
     * Let the machine accept a product and let it start handling it
     *
     * @param p The product that is offered
     * @return true if the product is accepted and started, false in all other cases
     */
    @Override
    public boolean giveProduct(Product p) {
        // Only accept something if the machine is idle
        double tme = eventlist.getTime();
//        if (status == 'i' && ((tme%(24*60*60) > startTime && tme%(24*60*60) <= finishTime) ||
//                (startTime == 22*60*60 && finishTime == 6*60*60 && (tme%(24*60*60) > 22*60*60 || tme%(24*60*60) <= 6*60*60)))) {
        if (status == 'i') {
            // accept the product
            product = p;
            // mark starting time
            product.stamp(eventlist.getTime(), "Production started", name);
            // start production
            startProduction(p.isCorporate());
            // Flag that the product has arrived
            return true;
        }
        // Flag that the product has been rejected
        else return false;
    }

    /**
     * Starting routine for the production
     * Start the handling of the current product with an exponentially distributed processing time with average 30
     * This time is placed in the eventlist
     */
    private void startProduction(boolean corporate) {
        double tme = eventlist.getTime();

        // generate duration
        if (meanProcTime > 0) {
            double duration = 0;
            if (corporate)
                duration = drawTruncatedNormal(3.6 * 60, 1.2 * 60, 45);
            else
                duration = drawTruncatedNormal(1.2 * 60, 35, 25);

            // Create a new event in the eventlist
            eventlist.add(this, 0, tme + duration); //target,type,time
            // set status to busy
            status = 'b';
        } else {
            // lws: unsure what this is..
            if (processingTimes.length > procCnt) {
                eventlist.add(this, 0, eventlist.getTime() + processingTimes[procCnt]); //target,type,time
                // set status to busy
                status = 'b';
                procCnt++;
            } else {
                eventlist.stop();
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

    /**
	 *
	 * @param mean Average
	 * @param std Standard deviation
	 * @param trunc Lower bound: Numbers lower than trunc will be rejected.
	 * @return sample: a random variate from a truncated normal distribution by means of the Box-Muller Method and
	 * 	 				Rejection Sampling
	 */
	public static double drawTruncatedNormal(double mean, double std, double trunc){
		//Generate two random variates
		double u1 = Simulation.random.nextDouble();
		double u2 = Simulation.random.nextDouble();

		// Generate standard normal samples with Box-Muller Method
		double sample = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2*Math.PI * u2);
		sample = mean + std*sample;

		// Rejection Sampling: Repeat if the generated variate is too low.
		if (sample >= trunc)
			return sample;
		else
			return drawTruncatedNormal(mean, std, trunc);
	}

	public void stop() {
	    this.stop = true;
	    // this setting of busy here might be unnecessary
	    this.status = 'b';
    }
}