package mathsimproject;

public interface RequestAcceptor {
    /**
     * Asks a queue to give a product to a machine
     * True is returned if a product could be delivered; false if the request is queued
     */
    public boolean askProduct(Machine machine);
}
