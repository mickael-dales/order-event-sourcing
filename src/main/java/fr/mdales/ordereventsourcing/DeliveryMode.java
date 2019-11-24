package fr.mdales.ordereventsourcing;

public class DeliveryMode {
    private final String mode;
    private final Double price;

    public DeliveryMode(String mode, double price) {
        this.mode = mode;
        this.price = price;
    }

    public String getMode() {
        return mode;
    }

    public Double getPrice() {
        return price;
    }
}
