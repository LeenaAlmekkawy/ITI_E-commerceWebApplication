package jets.projects.client_dto;


import java.math.BigDecimal;

public class OrderItemDto {
    private Long bookID;
    private Long quantity;
    private BigDecimal priceAtPurchase;

    // Getters and Setters
    public Long getBookId() { return bookID; }
    public void setBookId(Long bookId) { this.bookID = bookId; }
    public Long getQuantity() { return quantity; }
    public void setQuantity(Long quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
}
