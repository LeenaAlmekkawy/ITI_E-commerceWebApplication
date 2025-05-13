package jets.projects.normal_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.WishlistService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserGetAllWishList extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        WishlistService wishlistService = new WishlistService();

        Object result = null;
        Boolean returnState = true;
        if (registeredID == null) {
            String returnJson = JsonResponseConverter.toJsonResponse("the user must logIn first", false);

            response.getWriter().write(returnJson);
            return;
        }

        try {

            result = wishlistService.getWishlistItems(registeredID);
        } catch (NotFoundException e) {

            result = e.getMessage();
            returnState = false;

        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;

        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        response.getWriter().write(returnJson);

    }

}
