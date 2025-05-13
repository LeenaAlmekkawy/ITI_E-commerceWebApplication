package jets.projects.normal_user;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.client_dto.BookDto;
import jets.projects.services.BookService;
import jets.projects.utils.JsonResponseConverter;

public class UserGetAllBooksList extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        BookService bookService = new BookService();

        List<BookDto> allbooks = bookService.getAllBooks();

        String result = JsonResponseConverter.toJsonResponse(allbooks, true); // Print result

        response.getWriter().write(result);

    }

}
