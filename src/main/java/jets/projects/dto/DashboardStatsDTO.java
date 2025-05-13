package jets.projects.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardStatsDTO {
    private long totalBooks;
    private BigDecimal totalSales;
    private long totalUsers;
    private long pendingOrders;
    private List<MonthlySales> monthlySales;
    private List<TopBook> topBooks;

    public static class MonthlySales {
        private int year;
        private int month;
        private BigDecimal total;

        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
    }

    public static class TopBook {
        private String title;
        private long soldCount;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public long getSoldCount() { return soldCount; }
        public void setSoldCount(long soldCount) { this.soldCount = soldCount; }
    }

    // Getters and Setters
    public long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getPendingOrders() { return pendingOrders; }
    public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
    public List<MonthlySales> getMonthlySales() { return monthlySales; }
    public void setMonthlySales(List<MonthlySales> monthlySales) { this.monthlySales = monthlySales; }
    public List<TopBook> getTopBooks() { return topBooks; }
    public void setTopBooks(List<TopBook> topBooks) { this.topBooks = topBooks; }
}