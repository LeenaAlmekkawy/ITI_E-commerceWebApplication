package jets.projects.admin_user.discounts;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CategoriesServlet extends HttpServlet 
{
    private static final Gson gson = new Gson();
    private static final List<Category> categories = Arrays.asList(
        new Category("Fiction", "Fiction"),
        new Category("Non-Fiction", "Non-Fiction"),
        new Category("Science Fiction", "Science Fiction"),
        new Category("Fantasy", "Fantasy"),
        new Category("Mystery", "Mystery"),
        new Category("Romance", "Romance"),
        new Category("Thriller", "Thriller"),
        new Category("Biography", "Biography"),
        new Category("History", "History"),
        new Category("Self-Help", "Self-Help")
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write(gson.toJson(categories));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to fetch categories: " + e.getMessage())));
        }
    }

    // DTO for Category
    private static class Category 
    {
        private String id;
        private String name;

        public Category(String id, String name) {
            this.id = id;
            this.name = name;
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