/**
 *	Example program for using eventlists
 *	@author Joel Karel
 *	@version %I%, %G%
 */

package mathsimproject;

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
    	// Create an eventlist
	CEventList l = new CEventList();
	// A queue for the machine
	Queue q = new Queue();
	// A source
	Source consumerSource = new Source(q,l,"CorporateSource", 30);
	// A sink
	Sink si = new Sink("Sink 1");
	// A machine
	Machine m = new Machine(q,si,l,"Machine 1");
	// start the eventlist
	l.start(86500); // 2000 is maximum time
    }
    
}
