package jets.projects.filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jets.projects.admin_user.AdminURLMapper;

public class UserLoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("hello in filter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getServletPath();

        if (path.endsWith(AdminURLMapper.LOGIN_PAGE) || path.endsWith(AdminURLMapper.LOGIN) || path.endsWith(AdminURLMapper.LOGOUT)) {
            chain.doFilter(request, response);
        } else {
            if (session == null) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + AdminURLMapper.LOGIN_PAGE);
            } else {
                String isAdminLoggedIn = (String) session.getAttribute("adminLoggedIn");
                if ((isAdminLoggedIn != null && isAdminLoggedIn.equals("true"))) {
                    chain.doFilter(request, response);
                } else {
                    httpResponse.sendRedirect(httpRequest.getContextPath() + AdminURLMapper.LOGIN_PAGE);
                }
            }

        }
        /*

        if ((isAdminLoggedIn != null && isAdminLoggedIn.equals("ture"))) {
            chain.doFilter(request, response);
        } else {

            httpResponse.sendRedirect(httpRequest.getContextPath()
                    + AdminURLMapper.LOGIN_PAGE);

        }
         */

    }

}
