package jets.projects.beans;

public class UserBean {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String registrationDate;
    private int orders;

    // Constructor
    public UserBean(int id, String firstName, String lastName, String email,
            String phone, String address, String registrationDate, int orders) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.registrationDate = registrationDate;
        this.orders = orders;
    }

    public int getId() {
        return id;
    }

}
