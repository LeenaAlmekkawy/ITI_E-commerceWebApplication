package jets.projects.normal_user;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OperationFailedException;
import jets.projects.services.WishlistService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserAddWishListItem extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        WishlistService wishlistService = new WishlistService();

        System.out.println("do post in wish list remove item");

        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        System.out.println("Raw JSON Received: " + json.toString());
        String jsonString = json.toString();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

        //userID, bookID, quantity
        Long bookID = jsonObject.get("bookID").getAsLong();

        Object result = null;
        Boolean returnState = true;

        //InvalidInputException, NotFoundException, OutOfStockException
        try {

            if (wishlistService.addToWishlist(registeredID, bookID)) {
                result = "Book added to wishlist successfully.";

            }
        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;

        } catch (NotFoundException e) {
            result = e.getMessage();
            returnState = false;
        } catch (OperationFailedException e) {
            result = e.getMessage();
            returnState = false;
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        response.getWriter().write(returnJson);

    }

}
