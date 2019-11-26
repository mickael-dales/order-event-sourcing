package fr.mdales.ordereventsourcing.web;

import java.util.ArrayList;
import java.util.List;

public class FullOrderDTO {
    private final List<String> items = new ArrayList<>();
    private String deliveryMode;
    private double deliveryModePrice;
    private double amount = 0;
    private boolean paid;

    public FullOrderDTO(String deliveryMode, double deliveryModePrice, double amount, boolean paid) {
        this.deliveryMode = deliveryMode;
        this.deliveryModePrice = deliveryModePrice;
        this.amount = amount;
        this.paid = paid;
    }

    public List<String> getItems() {
        return items;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public double getDeliveryModePrice() {
        return deliveryModePrice;
    }

    public double getAmount() {
        return amount;
    }

    public boolean isPaid() {
        return paid;
    }

}
