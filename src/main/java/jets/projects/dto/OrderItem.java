package jets.projects.dto;

public class OrderItem {
    private String book;
    private double price;
    private int quantity;
    private double total;

    // Constructors
    public OrderItem() {}

    public OrderItem(String book, double price, int quantity, double total) {
        this.book = book;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    // Getters and Setters
    public String getBook() { return book; }
    public void setBook(String book) { this.book = book; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}