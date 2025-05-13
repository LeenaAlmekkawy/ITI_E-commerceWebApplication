package jets.projects.normal_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.client_dto.BookDto;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.BookService;
import jets.projects.utils.JsonResponseConverter;

public class UserGetBookDetails extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        Long bookId = null;
        try {
            bookId = Long.parseLong(request.getParameter("bookID"));
        } catch (Exception e) {
            String result = JsonResponseConverter.toJsonResponse("Invalid number format!", false);

            response.getWriter().write(result);
            return;
        }

        BookService bookService = new BookService();

        BookDto book = null;

        try {
            book = bookService.getBookById(bookId);
        } catch (NotFoundException e) {
            String result = JsonResponseConverter.toJsonResponse("Book not found!", false);

            response.getWriter().write(result);
            return;

        }

        String result = JsonResponseConverter.toJsonResponse(book, true);

        response.getWriter().write(result);

    }

}
