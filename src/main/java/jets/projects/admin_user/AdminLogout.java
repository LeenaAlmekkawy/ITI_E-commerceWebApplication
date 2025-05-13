package jets.projects.admin_user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import jets.projects.stats.OnlineStats;

public class AdminLogout extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            OnlineStats.userLoggedOut();
        }
        response.sendRedirect(request.getContextPath()
                        + AdminURLMapper.LOGIN_PAGE);
    }
}
