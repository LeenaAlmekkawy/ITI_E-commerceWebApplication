package jets.projects.normal_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.OrderService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserGetOrderDetails extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        OrderService orderService = new OrderService();

        Object result = null;
        Boolean returnState = true;

        long orderId = Long.parseLong(request.getParameter("orderID"));

        System.out.println("order id " + orderId);

        try {

            result = orderService.getOrderDetailsForClient(registeredID, orderId);
        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;
        } catch (NotFoundException e) {

            result = e.getMessage();
            returnState = false;
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        response.getWriter().write(returnJson);

    }

}
