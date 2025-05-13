package jets.projects.beans;

public class CartItemBean {
    private int productID;
    private int quantity;

    public CartItemBean() {
        productID = -1;
        quantity = 0;
    }

    public CartItemBean(int productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }

    public int getProductID() {
        return productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CartItemBean{");
        
        builder.append("productID=");
        builder.append(productID);
        
        builder.append(", quantity=");
        builder.append(quantity);
        
        builder.append('}');
        return builder.toString();
    }
}
