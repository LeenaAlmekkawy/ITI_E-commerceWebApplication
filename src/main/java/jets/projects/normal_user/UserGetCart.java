package jets.projects.normal_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.CartService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserGetCart extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");

        Long registeredID = GetUserID.getUserId(request);

        CartService cartService = new CartService();

        Object result = null;
        Boolean returnState = true;

        try {

            result = cartService.getUserCart(registeredID);
        } catch (NotFoundException e) {

            result = e.getMessage();
            e.printStackTrace();
            returnState = false;

        } catch (InvalidInputException e) {

            result = e.getMessage();
            e.printStackTrace();
            returnState = false;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        // System.out.println(returnJson);
        response.getWriter().write(returnJson);

    }

}
