package jets.projects.beans;

public class NormalUserBean {
    private int ID;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    
    public NormalUserBean() {
        ID = -1;
        email = "Not Specified";
        password = null;
        firstName = "Not Specified";
        lastName = "Not Specified";
    }

    public NormalUserBean(int ID, String email, String password, String firstName, String lastName) {
        this.ID = ID;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getID() {
        return ID;
    }

    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NormalUserBean{");
        
        builder.append("ID=");
        builder.append(ID);
        
        builder.append(", email=");
        builder.append(email);
        
        builder.append(", password=");
        builder.append(password != null ? password : "Hidden");
        
        builder.append("firstName=");
        builder.append(firstName);
        
        builder.append("lastName=");
        builder.append(lastName);
        
        builder.append('}');
        return builder.toString();
    }
}
