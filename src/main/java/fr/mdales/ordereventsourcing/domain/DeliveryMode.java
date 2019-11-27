package fr.mdales.ordereventsourcing.domain;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeliveryMode)) return false;

        DeliveryMode that = (DeliveryMode) o;

        if (getMode() != null ? !getMode().equals(that.getMode()) : that.getMode() != null) return false;
        return getPrice() != null ? getPrice().equals(that.getPrice()) : that.getPrice() == null;
    }

    @Override
    public int hashCode() {
        int result = getMode() != null ? getMode().hashCode() : 0;
        result = 31 * result + (getPrice() != null ? getPrice().hashCode() : 0);
        return result;
    }
}
