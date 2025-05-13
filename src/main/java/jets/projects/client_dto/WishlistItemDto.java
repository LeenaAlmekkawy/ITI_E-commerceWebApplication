package jets.projects.client_dto;


public class WishlistItemDto {
    private Long userID;
    private Long bookID;

    // Getters and Setters
    public Long getUserId() { return userID; }
    public void setUserId(Long userId) { this.userID = userId; }
    public Long getBookId() { return bookID; }
    public void setBookId(Long bookId) { this.bookID = bookId; }
}
