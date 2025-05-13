package jets.projects.admin_user;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRootRedirect extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/Admin/Home");

        // response.sendRedirect(request.getContextPath() + AdminURLMapper.LOGIN_PAGE);
    }
}
