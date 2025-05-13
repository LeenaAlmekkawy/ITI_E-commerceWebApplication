
package jets.projects.dto;

import java.util.Date;
import java.util.List;

public class Order 
{
    private String id;
    private Customer customer;
    private Date date;
    private double subtotal;
    private double shipping;
    private double tax;
    private double total;
    private String status;
    private List<OrderItem> items;

    // Constructors
    public Order() {}

    public Order(String id, Customer customer, Date date, double subtotal, double shipping, double tax, double total, String status, List<OrderItem> items) {
        this.id = id;
        this.customer = customer;
        this.date = date;
        this.subtotal = subtotal;
        this.shipping = shipping;
        this.tax = tax;
        this.total = total;
        this.status = status;
        this.items = items;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getShipping() { return shipping; }
    public void setShipping(double shipping) { this.shipping = shipping; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}