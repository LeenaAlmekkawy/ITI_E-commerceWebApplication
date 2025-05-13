package jets.projects.beans;

import java.util.ArrayList;
import java.util.List;

public class UserCartBean {
    private int userID;
    private List<CartItemBean> cartItems;
    
    public UserCartBean() {
        userID = -1;
        cartItems = new ArrayList<>();
    }

    public UserCartBean(int userID, List<CartItemBean> cartItems) {
        this.userID = userID;
        this.cartItems = cartItems;
    }

    public int getUserID() {
        return userID;
    }

    public List<CartItemBean> getCartItems() {
        return cartItems;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setCartItems(List<CartItemBean> cartItems) {
        this.cartItems = cartItems;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserCartBean{");
        
        builder.append("userID=");
        builder.append(userID);
        
        builder.append(", cartItems=");
        builder.append(cartItems);
        
        builder.append('}');
        return builder.toString();
    }
}
