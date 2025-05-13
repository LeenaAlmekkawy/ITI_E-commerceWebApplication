package jets.projects.client_dto;

public class CartItemDto
{
    private Long bookID;
    private Integer quantity;

    public Long getBookId() { return bookID; }
    public void setBookId(Long bookId) { this.bookID = bookId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}