package jets.projects.services;


import jets.projects.client_dto.CreditCardDetailsDto;
import jets.projects.dao.UserDao;
import jets.projects.client_dto.UserDto;
import jets.projects.dto.UserAdminDto;
import jets.projects.entity.User;
import jets.projects.exceptions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UserService {
    private final UserDao userDao;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    public UserService() {
        this.userDao = new UserDao();
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setHashPassword("Hidden");
        dto.setUsername(user.getUsername());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setBirthDate(user.getBirthDate());
        dto.setAccountBalance(user.getBalance());
        return dto;
    }

    // 1. Login
    public UserDto login(String email, String password) throws InvalidCredentialsException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidCredentialsException("Invalid email format");
        }
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidCredentialsException("Invalid password format");
        }

        User user = userDao.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        return convertToDto(user);
    }

    // 2. Register
    public UserDto register(String username, String email, String password, String phoneNumber, String address, LocalDate birthDate)
            throws InvalidInputException, OperationFailedException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException("Invalid email format");
        }
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidInputException("Password must be at least 8 characters with one uppercase and one lowercase letter");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new InvalidInputException("Phone number cannot be empty");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new InvalidInputException("Address cannot be empty");
        }
        if (birthDate == null) {
            throw new InvalidInputException("Birth date cannot be empty");
        }
        if (Period.between(birthDate, LocalDate.now()).getYears() < 10) {
            throw new InvalidInputException("User must be at least 10 years old");
        }

        if (userDao.findByEmail(email).isPresent()) {
            throw new OperationFailedException("Email is already registered");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setHashPassword(password); // Assuming password is hashed
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        user.setBirthDate(birthDate);
        user.setBalance(BigDecimal.ZERO);

        try {
            userDao.save(user);
            return convertToDto(user);
        } catch (Exception e) {
            throw new OperationFailedException("Registration failed", e);
        }
    }

    // 3. Get profile
    public UserDto getProfile(Long userId) throws InvalidInputException, NotFoundException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        return convertToDto(user);
    }

    // 4. Update email
    public boolean updateEmail(Long userId, String newEmail) throws InvalidInputException, OperationFailedException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (newEmail == null || !EMAIL_PATTERN.matcher(newEmail).matches()) {
            throw new InvalidInputException("Invalid email format");
        }
        if (userDao.findByEmail(newEmail).isPresent()) {
            throw new OperationFailedException("Email is already registered");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new OperationFailedException("User not found with ID: " + userId));
        user.setEmail(newEmail);
        try {
            userDao.update(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 5. Update password
    public boolean updatePassword(Long userId, String currentPassword, String newPassword) throws InvalidInputException, OperationFailedException, InvalidCredentialsException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (currentPassword == null || newPassword == null || !PASSWORD_PATTERN.matcher(newPassword).matches()) {
            throw new InvalidInputException("Invalid password format");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new OperationFailedException("User not found with ID: " + userId));
        if (!user.getHashPassword().equals(currentPassword)) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        user.setHashPassword(newPassword);
        try {
            userDao.update(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 6. Update details
    public UserDto updateDetails(Long userId, String newUsername, String newPhoneNumber, String newAddress, LocalDate newBirthDate)
            throws InvalidInputException, OperationFailedException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty");
        }
        if (newPhoneNumber == null || newPhoneNumber.trim().isEmpty()) {
            throw new InvalidInputException("Phone number cannot be empty");
        }
        if (newAddress == null || newAddress.trim().isEmpty()) {
            throw new InvalidInputException("Address cannot be empty");
        }
        if (newBirthDate == null) {
            throw new InvalidInputException("Birth date cannot be empty");
        }
        if (Period.between(newBirthDate, LocalDate.now()).getYears() < 10) {
            throw new InvalidInputException("User must be at least 10 years old");
        }

        User user = userDao.findById(userId)
                .orElseThrow(() -> new OperationFailedException("User not found with ID: " + userId));
        user.setUsername(newUsername);
        user.setPhoneNumber(newPhoneNumber);
        user.setAddress(newAddress);
        user.setBirthDate(newBirthDate);
        try {
            userDao.update(user);
            return convertToDto(user);
        } catch (Exception e) {
            throw new OperationFailedException("Update failed", e);
        }
    }

    // 7. Recharge balance
    public BigDecimal rechargeBalance(Long userId, CreditCardDetailsDto creditCardDetails, BigDecimal amount)
            throws InvalidInputException, OperationFailedException {
        if (userId == null || userId <= 0) {
            throw new InvalidInputException("Invalid user ID");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Amount must be positive");
        }
        validateCreditCardDetails(creditCardDetails);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new OperationFailedException("User not found with ID: " + userId));
        user.setBalance(user.getBalance().add(amount));
        try {
            userDao.update(user);
            return user.getBalance();
        } catch (Exception e) {
            throw new OperationFailedException("Recharge failed", e);
        }
    }

    private void validateCreditCardDetails(CreditCardDetailsDto details) throws InvalidInputException {
        if (details.getNameOnCard() == null || details.getNameOnCard().trim().isEmpty()) {
            throw new InvalidInputException("Name on card cannot be empty");
        }
        if (details.getCardNumber() == null || !Pattern.compile("^\\d{4}-\\d{4}-\\d{4}-\\d{4}$").matcher(details.getCardNumber()).matches()) {
            throw new InvalidInputException("Invalid card number format (expected: xxxx-xxxx-xxxx-xxxx)");
        }
        if (details.getExpiryDate() == null) {
            throw new InvalidInputException("Expiry date cannot be null");
        }
        LocalDate now = LocalDate.now();
        if (details.getExpiryDate().isBefore(now) || details.getExpiryDate().equals(now)) {
            throw new InvalidInputException("Expiry date must be in the future");
        }
        int month = details.getExpiryDate().getMonthValue();
        if (month < 1 || month > 12) {
            throw new InvalidInputException("Expiry month must be between 1 and 12");
        }
        if (details.getCvc() == null || !Pattern.compile("^\\d{3}$").matcher(details.getCvc()).matches()) {
            throw new InvalidInputException("CVC must be a 3-digit number");
        }
    }

    public List<UserAdminDto> getAllUsers() throws NotFoundException {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            throw new NotFoundException("No users found");
        }
        return users.stream().map(this::convertToUserAdminDto).collect(Collectors.toList());
    }

    private UserAdminDto convertToUserAdminDto(User user) {
        UserAdminDto dto = new UserAdminDto();
        dto.setId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBirthDate(user.getBirthDate().toString());
        dto.setAddress(user.getAddress());
        dto.setBalance(user.getBalance());
        dto.setInterests(user.getInterests().stream()
                .map(ui -> ui.getGenre().getName())
                .collect(Collectors.toList()));
        dto.setOrders(userDao.getOrderCountForUser(user.getUserId()));
        return dto;
    }
}

