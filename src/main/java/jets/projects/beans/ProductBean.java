package jets.projects.beans;

import java.util.Date;

public class ProductBean {
    private int ID;
    private String name;
    private String desc;
    private String imgURL;
    private boolean isAvailable;
    private int availableQuantity;
    private Date addedAt;

    public ProductBean() {
        ID = -1;
        name = "Not Specified";
        desc = "Not Specified";
        imgURL = null;
        isAvailable = true;
        availableQuantity = 1;
        addedAt = null;
    }

    public ProductBean(int ID, String name, String desc, String imgURL,
            boolean isAvailable, int availableQuantity, Date addedAt) {
        this.ID = ID;
        this.name = name;
        this.desc = desc;
        this.imgURL = imgURL;
        this.isAvailable = isAvailable;
        this.availableQuantity = availableQuantity;
        this.addedAt = addedAt;
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
    
    public String getImgURL() {
        return imgURL;
    }

    public boolean isIsAvailable() {
        return isAvailable;
    }
    
    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ProductBean{");
        
        builder.append("ID=");
        builder.append(ID);
        
        builder.append(", name=");
        builder.append(name);
        
        builder.append(", desc=");
        builder.append(desc);
        
        builder.append(", imgURL=");
        builder.append(imgURL != null ? imgURL : "No Image Specified");
        
        builder.append(", isAvailable=");
        builder.append(isAvailable);
        
        builder.append(", availableQuantity=");
        builder.append(availableQuantity);
        
        builder.append(", addedAt=");
        builder.append(addedAt);
        
        builder.append('}');
        return builder.toString();
    }
}
