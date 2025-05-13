package jets.projects.normal_user;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.dal.UsersDAL;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OutOfStockException;
import jets.projects.services.CartService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserUpdateCartItemQuantity extends HttpServlet {

    private final UsersDAL usersDAL = new UsersDAL();

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        CartService cartService = new CartService();

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

        //userID, bookID, newQuantity
        Long bookID = jsonObject.get("bookID").getAsLong();
        Integer newQuantity = jsonObject.get("newQuantity").getAsInt();

        Object result = null;
        Boolean returnState = true;

        //InvalidInputException, NotFoundException, OutOfStockException
        try {

            if (cartService.updateCart(registeredID, bookID, newQuantity)) {
                result = "Book in cart updated successfully";
            } else {
                result = "Book not found in the cart!";
                returnState = false;
            }
        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;

        } catch (NotFoundException e) {
            result = e.getMessage();
            returnState = false;
        } catch (OutOfStockException e) {
            result = e.getMessage();
            returnState = false;
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        response.getWriter().write(returnJson);

    }

}
