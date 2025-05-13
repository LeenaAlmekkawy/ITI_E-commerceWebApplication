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

public class BooksServlet extends HttpServlet 
{
    private static final Gson gson = new Gson();
    private static final List<Book> books = new ArrayList<>(Arrays.asList(
        new Book(1, "The Great Gatsby", "/images/gatsby.jpg", 20.00, "percentage", 10.0),
        new Book(2, "1984", "/images/1984.jpg", 15.00, null, 0.0),
        new Book(3, "Pride and Prejudice", "/images/pride.jpg", 18.00, "fixed", 5.0)
    ));

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String discountedParam = request.getParameter("discounted");
            if ("true".equals(discountedParam)) 
            {
                List<Book> discountedBooks = books.stream()
                    .filter(b -> b.discountValue > 0)
                    .toList();
                response.getWriter().write(gson.toJson(discountedBooks));
            } 
            else                
                response.getWriter().write(gson.toJson(books));

        } catch (Exception e) 
        {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to fetch books: " + e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String action = request.getParameter("action");
            if ("removeDiscount".equals(action)) {
                String idParam = request.getParameter("id");
                if (idParam == null) {
                    throw new Exception("Book ID required");
                }
                int id = Integer.parseInt(idParam);
                Book book = books.stream().filter(b -> b.id == id).findFirst()
                    .orElseThrow(() -> new Exception("Book not found"));
                book.discountType = null;
                book.discountValue = 0.0;
                response.getWriter().write(gson.toJson(new SuccessResponse(true)));
            } else {
                throw new Exception("Invalid action");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(gson.toJson(new ErrorResponse("Failed to remove discount: " + e.getMessage())));
        }
    }

    // DTO for Book
    private static class Book 
    {
        private int id;
        private String title;
        private String bookImage;
        private double price;
        private String discountType; 
        private double discountValue;

        public Book(int id, String title, String bookImage, double price, String discountType, double discountValue) 
        {
            this.id = id;
            this.title = title;
            this.bookImage = bookImage;
            this.price = price;
            this.discountType = discountType;
            this.discountValue = discountValue;
        }
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