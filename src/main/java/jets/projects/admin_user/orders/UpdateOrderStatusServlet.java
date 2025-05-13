package jets.projects.admin_user.orders;

import java.io.BufferedReader;
import java.io.IOException;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.services.OrderService;
import jets.projects.exceptions.InvalidInputException;
import jets.projects.exceptions.NotFoundException;
import jets.projects.exceptions.OperationFailedException;

public class UpdateOrderStatusServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private final OrderService orderService;

    public UpdateOrderStatusServlet() {
        this.orderService = new OrderService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            System.out.println("UpdateOrderStatusServlet: Received request: " + sb.toString());
            UpdateRequest updateRequest = gson.fromJson(sb.toString(), UpdateRequest.class);

            if (updateRequest.getOrderId() == null || updateRequest.getStatus() == null) {
                System.out.println("UpdateOrderStatusServlet: Invalid order ID or status");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new UpdateResponse(false, "Invalid order ID or status")));
                return;
            }

            Long orderId;
            try {
                orderId = Long.parseLong(updateRequest.getOrderId());
            } catch (NumberFormatException e) {
                System.out.println("UpdateOrderStatusServlet: Invalid order ID format: " + updateRequest.getOrderId());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(gson.toJson(new UpdateResponse(false, "Invalid order ID format")));
                return;
            }
            String newStatus = updateRequest.getStatus();
            boolean success = orderService.updateOrderStatus(orderId, newStatus);
            String message = success ? "Status updated successfully" : "Failed to update status";

            if (success && "DELIVERED".equalsIgnoreCase(newStatus)) {
                message += "; Order moved to purchase history with receipt";
            }

            response.getWriter().write(gson.toJson(new UpdateResponse(success, message)));
        } catch (InvalidInputException e) {
            System.out.println("UpdateOrderStatusServlet: Invalid input: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new UpdateResponse(false, "Invalid input: " + e.getMessage())));
        } catch (NotFoundException e) {
            System.out.println("UpdateOrderStatusServlet: Not found: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(new UpdateResponse(false, "Not found: " + e.getMessage())));
        } catch (OperationFailedException e) {
            System.out.println("UpdateOrderStatusServlet: Operation failed: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new UpdateResponse(false, "Operation failed: " + e.getMessage())));
        } catch (Exception e) {
            System.out.println("UpdateOrderStatusServlet: Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new UpdateResponse(false, "Unexpected error: " + e.getMessage())));
        }
    }

    private static class UpdateRequest {
        private String orderId;
        private String status;

        public String getOrderId() {
            return orderId;
        }

        public String getStatus() {
            return status;
        }
    }

    private static class UpdateResponse {
        private boolean success;
        private String message;

        public UpdateResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}