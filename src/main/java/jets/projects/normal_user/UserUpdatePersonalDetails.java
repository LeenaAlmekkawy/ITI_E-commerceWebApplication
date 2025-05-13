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
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.OperationFailedException;
import jets.projects.services.UserService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserUpdatePersonalDetails extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

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
        JsonObject updatedDetails = jsonObject.getAsJsonObject("updatedDetails");

        String username = updatedDetails.get("userName").getAsString();

        String phoneNumber = updatedDetails.get("phoneNumber").getAsString();

        String address = updatedDetails.get("address").getAsString();
        String birthDate = updatedDetails.get("birthDate").getAsString();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(birthDate);
        LocalDate localDate = zonedDateTime.toLocalDate();

        Long registeredID = GetUserID.getUserId(request);

        UserService userService = new UserService();

        Object result = null;
        Boolean returnState = true;

        try {

            result = userService.updateDetails(registeredID, username, phoneNumber, address, localDate);

        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;

        } catch (OperationFailedException e) {
            result = e.getMessage();
            returnState = false;
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        response.getWriter().write(returnJson);

    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    }
}
