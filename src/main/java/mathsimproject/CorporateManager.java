package mathsimproject;

import java.util.List;

public class CorporateManager implements RequestAcceptor {

    private Queue corporateQ;
    private Queue consumerQ;
    private List<Machine> machines;
    // "number kc of CSA corporate are kept idle to handle incoming corporate calls"
    private int kc;

    public CorporateManager(Queue corporateQ, Queue consumerQ, int kc) {
        this.consumerQ = consumerQ;
        this.corporateQ = corporateQ;
//        this.machines = machines;
        this.kc = kc;
    }

    @Override
    public boolean askProduct(Machine machine) {

        if (corporateQ.getNumProducts() > 0) {
            // if there are corporate clients waiting, request for accepting a call goes to their queue
            corporateQ.askProduct(machine);
        } else {
            // if corporate queue is empty, corporate agent is "idle", so we let this agent file a request to answer a call
            // form the consumers queue
            if (corporateQ.getNumRequesters() < kc) {
                // if the number of corporate agents that are idle does not reach kc, add this one too
                corporateQ.askProduct(machine);
            } else {
                // TODO: This is part of the policy we need to decide on.
                // for now, if kc requirement is met, we add them to both
                corporateQ.askProduct(machine);
                consumerQ.askProduct(machine);
            }
        }

        return false;
    }
}
