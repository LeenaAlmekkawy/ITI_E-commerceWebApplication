package jets.projects.normal_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.CartService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserGetCartSubtotal extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        CartService cartService = new CartService();

        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("no session");
        } else {
            System.out.println(session.getAttribute("userID"));
        }

        System.out.println("user id " + registeredID);

        Object result = null;
        Boolean returnState = true;
        if (registeredID == null) {
            String returnJson = JsonResponseConverter.toJsonResponse("the user must logIn first", false);

            response.getWriter().write(returnJson);
            return;
        }

        try {

            result = cartService.getCartSubtotal(registeredID);
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
