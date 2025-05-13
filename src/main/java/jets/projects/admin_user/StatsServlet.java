package jets.projects.admin_user;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.dto.DashboardStatsDTO;
import jets.projects.services.StatsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class StatsServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(StatsServlet.class.getName());
    private final StatsService statsService = new StatsService();
    private final Gson gson = new Gson();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("Access-Control-Allow-Origin", "*"); // Allow cross-origin if needed

        PrintWriter out = response.getWriter();

        try {
            while (!response.isCommitted()) {
                try {
                    DashboardStatsDTO stats = statsService.getDashboardStats();
                    String eventData = gson.toJson(stats);

                    out.write("event: stats\n");
                    out.write("data: " + eventData + "\n\n");
                    out.flush();

                    LOGGER.info("Sent SSE stats update");

                    Thread.sleep(5000); // Update every 5 seconds
                } catch (InterruptedException e) {
                    LOGGER.warning("SSE thread interrupted: " + e.getMessage());
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    break;
                } catch (Exception e) {
                    LOGGER.severe("Error fetching stats for SSE: " + e.getMessage());
                    out.write("event: error\n");
                    out.write("data: " + gson.toJson(new ErrorResponse("Failed to fetch stats")) + "\n\n");
                    out.flush();
                }
            }
        } finally {
            LOGGER.info("Closing SSE connection");
            out.close();
        }
    }

    private static class ErrorResponse {
        private final String error;

        ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}