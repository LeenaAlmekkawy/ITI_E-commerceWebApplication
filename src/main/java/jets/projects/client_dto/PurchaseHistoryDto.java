package jets.projects.client_dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PurchaseHistoryDto {

    private Long itemID;
    private Long userID;
    private LocalDateTime date;
    private BigDecimal totalPaid;
    private String receiptFileUrl;

    // Getters and Setters
    public Long getItemId() {
        return itemID;
    }

    public void setItemId(Long itemId) {
        this.itemID = itemId;
    }

    public Long getUserId() {
        return userID;
    }

    public void setUserId(Long userId) {
        this.userID = userId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getReceiptFileUrl() {
        return receiptFileUrl;
    }

    public void setReceiptFileUrl(String receiptFileUrl) {
        this.receiptFileUrl = receiptFileUrl;
    }
}
