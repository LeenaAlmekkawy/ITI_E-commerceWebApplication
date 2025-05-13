package jets.projects.normal_user;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.client_dto.CreditCardDetailsDto;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OperationFailedException;
import jets.projects.exceptions.OutOfStockException;
import jets.projects.services.OrderService;
import jets.projects.utils.GetUserID;
import jets.projects.utils.JsonResponseConverter;

public class UserCheckoutUsingCreditCard extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Long registeredID = GetUserID.getUserId(request);

        OrderService orderServce = new OrderService();

        System.out.println("do post in checkcard");

        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        String jsonString = json.toString();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
        JsonObject creditCardDetails = jsonObject.getAsJsonObject("creditCardDetails");

        CreditCardDetailsDto creditCardDetailsDto = new CreditCardDetailsDto();

        String address = jsonObject.get("address").getAsString();

        creditCardDetailsDto.setNameOnCard(creditCardDetails.get("nameOnCard").getAsString());

        creditCardDetailsDto.setCardNumber(creditCardDetails.get("cardNumber").getAsString());

        creditCardDetailsDto.setCvc(creditCardDetails.get("cvc").getAsString());

        String yearStr = creditCardDetails.get("expiryYear").getAsString();
        String monthStr = creditCardDetails.get("expiryMonth").getAsString();

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);

        if (month < 1 || month > 12) {
            month = 1;
        }

        LocalDate expiryDate = LocalDate.of(year, month, 1);

        creditCardDetailsDto.setExpiryDate(expiryDate);

        Object result = null;
        Boolean returnState = true;

        System.out.println("data: " + creditCardDetailsDto.getExpiryDate());
        System.out.println("nameoncard: " + creditCardDetailsDto.getNameOnCard());
        System.out.println("cardNumber: " + creditCardDetailsDto.getCardNumber());
        System.out.println("cvc: " + creditCardDetailsDto.getCvc());
        System.out.println("address: " + address);

        //InvalidInputException, NotFoundException, OutOfStockException
        try {
            Long orderID = orderServce.checkoutWithCreditCard(registeredID, creditCardDetailsDto, address);

            result = "Order placed successfully, with ID#" + orderID.toString();

        } catch (InvalidInputException e) {

            result = e.getMessage();
            returnState = false;

        } catch (NotFoundException e) {
            result = e.getMessage();
            returnState = false;
        } catch (OutOfStockException e) {
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
