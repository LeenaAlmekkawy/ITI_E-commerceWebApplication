package jets.projects.normal_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.PurchaseHistoryService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserGetAllPurchaseHistoryList extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        System.out.println("hello in get purches history");

        PurchaseHistoryService purchaseHistoryService = new PurchaseHistoryService();

        Object result = null;
        Boolean returnState = true;

        try {

            result = purchaseHistoryService.getPurchaseHistory(registeredID);
            System.out.println("reslut");
        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;
        } catch (NotFoundException e) {

            result = e.getMessage();
            returnState = false;
        } catch (Exception e) {
            e.printStackTrace();
        }

        String returnJson = JsonResponseConverter.toJsonResponse(result, returnState);

        response.getWriter().write(returnJson);

    }

}
