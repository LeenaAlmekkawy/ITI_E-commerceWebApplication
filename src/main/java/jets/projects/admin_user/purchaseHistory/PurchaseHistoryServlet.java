package jets.projects.admin_user.purchaseHistory;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.dto.PurchaseDTO;
import jets.projects.services.PurchaseHistoryService;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/Admin/PurchaseHistoryServlet/*")
public class PurchaseHistoryServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(PurchaseHistoryServlet.class.getName());
    private final PurchaseHistoryService purchaseService = new PurchaseHistoryService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            if (pathInfo != null && pathInfo.startsWith("/receipt/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    handleReceiptDetails(response, Long.parseLong(parts[1]));
                    return;
                }
            }

            int page = parseIntParam(request.getParameter("page"), 1);
            int size = parseIntParam(request.getParameter("size"), 10);
            String search = request.getParameter("search");
            LocalDate dateFrom = parseDateParam(request.getParameter("dateFrom"));
            LocalDate dateTo = parseDateParam(request.getParameter("dateTo"));

            LOGGER.info("Fetching purchases: page=" + page + ", size=" + size + ", search=" + search + ", dateFrom=" + dateFrom + ", dateTo=" + dateTo);

            List<PurchaseDTO> purchases = purchaseService.findAll(page, size, search, dateFrom, dateTo);
            long totalItems = purchaseService.countFiltered(search, dateFrom, dateTo);
            int totalPages = (int) Math.ceil((double) totalItems / size);

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("purchases", purchases);
            responseData.put("totalPages", totalPages);
            responseData.put("currentPage", page);

            out.print(gson.toJson(responseData));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching purchase history", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Failed to fetch purchase history: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    private int parseIntParam(String param, int defaultValue) {
        try {
            return param != null && !param.isEmpty() ? Integer.parseInt(param) : defaultValue;
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid integer param: " + param);
            return defaultValue;
        }
    }

    private LocalDate parseDateParam(String param) {
        try {
            return param != null && !param.isEmpty() ? LocalDate.parse(param) : null;
        } catch (Exception e) {
            LOGGER.warning("Invalid date param: " + param);
            return null;
        }
    }

    private void handleReceiptDetails(HttpServletResponse response, Long id) throws IOException {
        PrintWriter out = response.getWriter();
        try {
            Optional<PurchaseDTO> purchase = purchaseService.findById(id);
            if (purchase.isPresent()) {
                out.print(gson.toJson(purchase.get()));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(Map.of("error", "Receipt not found")));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching receipt: " + id, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Map.of("error", "Failed to fetch receipt: " + e.getMessage())));
        } finally {
            out.flush();
        }
    }
}