package jets.projects.utils;

import java.util.regex.Pattern;

public class DataValidator {
    private static final Pattern EMAIL_PATTERN 
            = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    private static final Pattern PHONE_PATTERN 
            = Pattern.compile("^(?:\\+20|0)(10|11|12|15)\\d{8}$");

    public static boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean validatePhoneNumber(String phone) {
        return PHONE_PATTERN.matcher(phone).matches();
    }
}
