package mathsimproject;

/**
 * Instantiates a machine and schedules its demise (after 8h) and its rebirth again (16h after demise)
 */
public class MachineScheduler implements CProcess {

    private Machine machine;
    private CEventList eventsList;
    private RequestAcceptor requestAcceptor;
    private Sink sink;
    private String name;

    public MachineScheduler(CEventList eventsList, RequestAcceptor requestAcceptor, Sink sink, String name) {
        this.eventsList = eventsList;
        this.requestAcceptor = requestAcceptor;
        this.sink = sink;
        this.name = name;
    }

    @Override
    public void execute(int type, double tme) {
        if (type == 0) {
            machine = new Machine(requestAcceptor, sink, eventsList, name);
            // schedule stopping this machine
            eventsList.add(this, 1, tme + 8 * 60 * 60);
        } else if (type == 1 && machine != null) {
            // 8 hours have passed, time to end our shift
            machine.stop();
            // schedule starting a new machine on the same shift the next day (24 h - 8h)
            eventsList.add(this, 0, tme + 16 * 60 * 60);
        }
    }
}
