package jets.projects.dto;

import java.math.BigDecimal;

public class StatsDto {
    private long totalPurchases;
    private BigDecimal totalRevenue;
    private long uniqueCustomers;

    // Getters and Setters
    public long getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(long totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getUniqueCustomers() {
        return uniqueCustomers;
    }

    public void setUniqueCustomers(long uniqueCustomers) {
        this.uniqueCustomers = uniqueCustomers;
    }
}