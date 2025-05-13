package jets.projects.beans;

public class AdminBean {
    private int ID;
    private String password;
    private String displayName;

    public AdminBean() {
        ID = -1;
        password = null;
        displayName = "Not Specified";
    }

    public AdminBean(int ID, String password, String displayName) {
        this.ID = ID;
        this.password = password;
        this.displayName = displayName;
    }

    public int getID() {
        return ID;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("AdminBean{");
        
        builder.append("ID=");
        builder.append(ID);
        
        builder.append(", password=");
        builder.append(password != null ? password : "Hidden");
        
        builder.append(", displayName=");
        builder.append(displayName);
        
        builder.append('}');
        return builder.toString();
    }
}
