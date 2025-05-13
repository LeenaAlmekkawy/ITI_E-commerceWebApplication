package jets.projects.admin_user.discounts;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DiscountsServlet extends HttpServlet 
{
    private static final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String action = request.getParameter("action");
            if ("removeAll".equals(action)) 
                response.getWriter().write(gson.toJson(new SuccessResponse(true)));
            else 
            {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = request.getReader()) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                DiscountRequest discountRequest = gson.fromJson(sb.toString(), DiscountRequest.class);

                // Validate input
                if (discountRequest.discountType == null || discountRequest.discountValue < 0) {
                    throw new Exception("Invalid discount type or value");
                }
                if (!"percentage".equals(discountRequest.discountType) && !"fixed".equals(discountRequest.discountType)) {
                    throw new Exception("Unsupported discount type");
                }
                if (discountRequest.applyTo == null || (!"all".equals(discountRequest.applyTo) &&
                    !"category".equals(discountRequest.applyTo) && !"books".equals(discountRequest.applyTo))) {
                    throw new Exception("Invalid applyTo value");
                }
                if ("category".equals(discountRequest.applyTo) && discountRequest.categoryId == null) {
                    throw new Exception("Category ID required for category discount");
                }
                if ("books".equals(discountRequest.applyTo) && (discountRequest.bookIds == null || discountRequest.bookIds.isEmpty())) {
                    throw new Exception("Book IDs required for books discount");
                }

                // Simulate discount application
                response.getWriter().write(gson.toJson(new SuccessResponse(true)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to process discount request: " + e.getMessage())));
        }
    }

    // DTO for Discount Request
    private static class DiscountRequest 
    {
        private String discountType;
        private double discountValue;
        private String applyTo;
        private String categoryId;
        private List<Integer> bookIds;

        public String getDiscountType() { return discountType; }
        public double getDiscountValue() { return discountValue; }
        public String getApplyTo() { return applyTo; }
        public String getCategoryId() { return categoryId; }
        public List<Integer> getBookIds() { return bookIds; }
    }

    // DTO for Success Response
    private static class SuccessResponse 
    {
        private boolean success;

        public SuccessResponse(boolean success) {
            this.success = success;
        }
    }

    // DTO for Error Response
    private static class ErrorResponse 
    {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}