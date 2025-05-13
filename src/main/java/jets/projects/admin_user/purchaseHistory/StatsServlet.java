package jets.projects.admin_user.purchaseHistory;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.services.StatsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

//@WebServlet("/Admin/StatsServlet")
public class StatsServlet extends HttpServlet {
    private final StatsService statsService = new StatsService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        PrintWriter out = response.getWriter();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                out.write("data: " + gson.toJson(statsService.getPurchaseStats()) + "\n\n");
                out.flush();
                Thread.sleep(5000); // Update every 5 seconds
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            out.write("data: " + gson.toJson(Map.of("error", "Stats update failed")) + "\n\n");
        } finally {
            out.close();
        }
    }
}