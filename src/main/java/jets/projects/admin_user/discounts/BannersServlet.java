package jets.projects.admin_user.discounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BannersServlet extends HttpServlet 
{
    private static final Gson gson = new Gson();
    private static final List<Banner> banners = new ArrayList<>(Arrays.asList(
        new Banner(1, "Summer Sale", "/images/summer_sale.jpg", "Up to 50% off!"),
        new Banner(2, "New Arrivals", "/images/new_arrivals.jpg", "Check out our latest books!")
    ));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String idParam = request.getParameter("id");
            if (idParam == null) 
                response.getWriter().write(gson.toJson(banners));
            else 
            {
                int id = Integer.parseInt(idParam);
                Banner banner = banners.stream().filter(b -> b.id == id).findFirst()
                    .orElseThrow(() -> new Exception("Banner not found"));
                response.getWriter().write(gson.toJson(banner));
            }
        } catch (Exception e) 
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to fetch banner: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String action = request.getParameter("action");
            if ("update".equals(action)) 
            { 
                String idParam = request.getParameter("id");
                if (idParam == null) 
                    throw new Exception("Banner ID required");

                int id = Integer.parseInt(idParam);
                Banner banner = banners.stream().filter(b -> b.id == id).findFirst()
                    .orElseThrow(() -> new Exception("Banner not found"));
                banner.title = "Updated Banner";  
                response.getWriter().write(gson.toJson(new SuccessResponse(true)));
            } 
            else if ("delete".equals(action)) 
            {
                String idParam = request.getParameter("id");
                if (idParam == null) 
                    throw new Exception("Banner ID required");
                
                int id = Integer.parseInt(idParam);
                boolean removed = banners.removeIf(b -> b.id == id);

                if (!removed) 
                    throw new Exception("Banner not found");
                
                response.getWriter().write(gson.toJson(new SuccessResponse(true)));
            } 
            else 
            {    
                int newId = banners.stream().mapToInt(b -> b.id).max().orElse(0) + 1;
                banners.add(new Banner(newId, "New Banner", "/images/placeholder.jpg", ""));
                response.getWriter().write(gson.toJson(new SuccessResponse(true)));
            }
        } 
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to process banner request: " + e.getMessage())));
        }
    }

    // DTO for Banner
    private static class Banner 
    {
        private int id;
        private String title;
        private String image;
        private String text;

        public Banner(int id, String title, String image, String text) {
            this.id = id;
            this.title = title;
            this.image = image;
            this.text = text;
        }
    }

    // DTO for Success Response
    private static class SuccessResponse {
        private boolean success;

        public SuccessResponse(boolean success) {
            this.success = success;
        }
    }

    // DTO for Error Response
    private static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}