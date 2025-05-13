package jets.projects.normal_user;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.client_dto.BookDto;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.BookService;
import jets.projects.utils.JsonResponseConverter;

public class UserSearchBooks extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        String keyWord = request.getParameter("keywords");

        BookService bookService = new BookService();

        List<BookDto> allbooks = null;

        try {
            allbooks = bookService.searchBooks(keyWord);
        } catch (NotFoundException e) {
            String result = JsonResponseConverter.toJsonResponse("No results found!", false);

            response.getWriter().write(result);
            return;

        }

        String result = JsonResponseConverter.toJsonResponse(allbooks, true);

        response.getWriter().write(result);

    }
}
