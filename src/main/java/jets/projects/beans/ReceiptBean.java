package jets.projects.beans;

import java.util.Date;

public class ReceiptBean {
    private int id;
    private int userId;
    private String userName;
    private String userEmail;
    private Date date;
    private double totalPaid;

    public ReceiptBean() {
    }

    public ReceiptBean(int id, int userId, String userName, String userEmail, Date date, double totalPaid) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.date = date;
        this.totalPaid = totalPaid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(double totalPaid) {
        this.totalPaid = totalPaid;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ReceiptBean{");
        builder.append("id=").append(id);
        builder.append(", userId=").append(userId);
        builder.append(", userName=").append(userName);
        builder.append(", userEmail=").append(userEmail);
        builder.append(", date=").append(date);
        builder.append(", totalPaid=").append(totalPaid);
        builder.append('}');
        return builder.toString();
    }
}