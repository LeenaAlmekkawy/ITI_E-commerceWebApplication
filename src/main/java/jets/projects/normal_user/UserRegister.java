package jets.projects.normal_user;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jets.projects.client_dto.UserDto;
import jets.projects.dal.UsersDAL;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.OperationFailedException;
import jets.projects.services.UserService;
import jets.projects.utils.DataValidator;
import jets.projects.utils.JsonResponseConverter;

public class UserRegister extends HttpServlet {

    private final UsersDAL usersDAL = new UsersDAL();

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        System.out.println("do post in user register");
        //System.out.println("Query String = " + request.getQueryString());
        //System.out.println("Request URI = " + request.getRequestURI());
        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        System.out.println("Raw JSON Received: " + json.toString());
        String jsonString = json.toString();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        String username = jsonObject.get("userName").getAsString();

        String email = jsonObject.get("email").getAsString();
        String phoneNumber = jsonObject.get("phoneNumber").getAsString();
        String password = jsonObject.get("password").getAsString();
        String address = jsonObject.get("address").getAsString();
        String birthDate = jsonObject.get("birthDate").getAsString();

        ZonedDateTime zonedDateTime = ZonedDateTime.parse(birthDate);
        LocalDate localDate = zonedDateTime.toLocalDate();

        UserService userService = new UserService();

        Object result = null;
        Boolean returnState = true;

        try {

            result = userService.register(username, email, password, phoneNumber, address, localDate);
        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;

        } catch (OperationFailedException e) {
            result = e.getMessage();
            returnState = false;
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        if (returnState == true) {
            UserDto user = (UserDto) result;
            Long registeredID = user.getUserId();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            session = request.getSession(true);

            session.setAttribute("userID", registeredID);
            System.out.println("user id" + registeredID);

        }
        response.getWriter().write(returnJson);

        /*
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        
        String errorMessage = checkInputForError(email,
                password, firstName, lastName);
        if (errorMessage != null) {
            errorMessage = URLEncoder.encode(errorMessage,
                            StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath()
                    + UserURLMapper.REGISTER_PAGE
                    + "?errorMessage=" + errorMessage);
            return;
        }
        
        NormalUserBean user = new NormalUserBean(-1, email,
                password, firstName, lastName);
        Integer registeredID = usersDAL.register(user);
        if (registeredID == null || registeredID == -1) {
            if (registeredID == null) {
                errorMessage = FixedMessages.DB_ERROR;
            } else {
                errorMessage = "An account is already registerd by this email.";
            }
            
            errorMessage = URLEncoder.encode(
                    errorMessage,
                    StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath()
                    + UserURLMapper.REGISTER_PAGE
                    + "?errorMessage=" + errorMessage);
            return;
        }
        
        user.setID(registeredID);
        // Create a new session if not existed, and refresh old one if exists.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        session = request.getSession(true);
        
        session.setAttribute("userID", registeredID);
        response.sendRedirect(request.getContextPath()
                + UserURLMapper.HOME_PAGE);
         */
    }
    
    private String checkInputForError(String email,
            String password, String firstName, String lastName) {
        if (email == null) {
            return "Email must be provided.";
        }
        
        if (email.isBlank()) {
            return "Email cannot be empty.";
        }
        
        if (!DataValidator.validateEmail(email)) {
            return "Invalid Email Format.";
        }
        
        if (password == null) {
            return "Password must be provided.";
        }
        
        if (password.isBlank()) {
            return "Password cannot be empty.";
        }
        
        if (firstName == null) {
            return "First Name must be provided.";
        }
        
        if (firstName.isBlank()) {
            return "First Name cannot be empty.";
        }
        
        if (lastName == null) {
            return "Last Name must be provided.";
        }
        
        if (lastName.isBlank()) {
            return "Last Name cannot be empty.";
        }
        
        return null;
    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        System.out.println("user register servlet");
    }
}
