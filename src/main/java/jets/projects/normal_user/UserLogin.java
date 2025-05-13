package jets.projects.normal_user;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jets.projects.client_dto.UserDto;
import jets.projects.dal.UsersDAL;
import jets.projects.exceptions.InvalidCredentialsException;
import jets.projects.services.UserService;
import jets.projects.utils.JsonResponseConverter;

public class UserLogin extends HttpServlet {

    private final UsersDAL usersDAL = new UsersDAL();

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        System.out.println(
                "Raw JSON Received: " + json.toString());
        String jsonString = json.toString();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        String email = jsonObject.get("email").getAsString();
        String password = jsonObject.get("password").getAsString();

        UserService userService = new UserService();

        Object result = null;
        Boolean returnState = true;

        try {

            result = userService.login(email, password);
        } catch (InvalidCredentialsException e) {

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
            session.setAttribute("isLoggedIn", "true");
            System.out.println("user id" + registeredID);

        }

        HttpSession session = request.getSession(false);

        Long registeredID = null;

        if (session
                != null) {
            // Retrieve the userID
            registeredID = (Long) session.getAttribute("userID");
            System.out.println("user id " + registeredID);

        }

        System.out.println(
                "user id " + registeredID);

        response.getWriter()
                .write(returnJson);

    }
}
