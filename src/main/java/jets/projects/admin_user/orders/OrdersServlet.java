package jets.projects.admin_user.orders;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.client_dto.OrderDto;
import jets.projects.services.OrderService;

public class OrdersServlet extends HttpServlet {
    private static final Gson gson = new Gson();
    private final OrderService orderService;

    public OrdersServlet() {
        this.orderService = new OrderService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            List<OrderDto> orders = orderService.getAllOrders();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(orders));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to fetch orders: " + e.getMessage())));
        }
    }

    // Error response DTO
    private static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}