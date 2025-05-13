package jets.projects.dto;
import java.util.List;
public class PurchaseHistoryDTO {
    private List<ReceiptDTO> purchases;
    private int totalPages;
    private int totalPurchases;
    private double totalRevenue;
    private long uniqueCustomers;
    public PurchaseHistoryDTO(List<ReceiptDTO> purchases, int totalPages, int totalPurchases, double totalRevenue, long uniqueCustomers) {
        this.purchases = purchases; this.totalPages = totalPages; this.totalPurchases = totalPurchases;
        this.totalRevenue = totalRevenue; this.uniqueCustomers = uniqueCustomers;
    }
    public List<ReceiptDTO> getPurchases() { return purchases; }
    public int getTotalPages() { return totalPages; }
    public int getTotalPurchases() { return totalPurchases; }
    public double getTotalRevenue() { return totalRevenue; }
    public long getUniqueCustomers() { return uniqueCustomers; }
}