package jets.projects.client_dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserDto {
    private Long userID;
    private String email;
    private String hashPassword;
    private String userName;
    private String phoneNumber;
    private String address;
    private LocalDate birthDate;
    private BigDecimal accountBalance;

    // Getters and Setters
    public Long getUserId() { return userID; }
    public void setUserId(Long userId) { this.userID = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHashPassword() { return hashPassword; }
    public void setHashPassword(String hashPassword) { this.hashPassword = hashPassword; }
    public String getUsername() { return userName; }
    public void setUsername(String username) { this.userName = username; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public BigDecimal getAccountBalance() { return accountBalance; }
    public void setAccountBalance(BigDecimal accountBalance) { this.accountBalance = accountBalance; }
}
