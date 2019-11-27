package fr.mdales.ordereventsourcing.command;

public class ChooseDeliveryModeCommand {

    private final Integer orderId;
    private final String name;
    private final double price;

    public ChooseDeliveryModeCommand(Integer orderId, String name, double price) {
        this.orderId =orderId;
        this.name=name;
        this.price=price;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
