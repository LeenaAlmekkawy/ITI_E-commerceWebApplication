package jets.projects.dto;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class ReceiptDTO {
    @SerializedName("id")
    private int id;

    @SerializedName("userId")
    private int userId;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("date")
    private Date date;

    @SerializedName("totalPaid")
    private double totalPaid;

    public ReceiptDTO() {
    }

    public ReceiptDTO(int id, int userId, String userName, String userEmail, Date date, double totalPaid) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.date = date;
        this.totalPaid = totalPaid;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public double getTotalPaid() { return totalPaid; }
    public void setTotalPaid(double totalPaid) { this.totalPaid = totalPaid; }
}