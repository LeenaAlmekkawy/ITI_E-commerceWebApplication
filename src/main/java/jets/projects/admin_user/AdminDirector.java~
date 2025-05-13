package jets.projects.admin_user;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminDirector extends HttpServlet {

    private static final boolean ADMIN_LOGIN_SERVICE_AVAILABLE = true;

    private void directToErrorPage(HttpServletRequest request,
            HttpServletResponse response,
            String message) throws IOException {
        String errorMessage = URLEncoder.encode(
                message,
                StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath()
                + AdminURLMapper.ERROR_PAGE
                + "?errorMessage=" + errorMessage);
    }

    private boolean redirectOnHavingInvalidSession(HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session) throws IOException {
        String errorMessage;
        if (session == null || session.getAttribute("adminID") == null) {
            if (session != null) {
                session.invalidate();
                errorMessage = "Session Invalid or Timed out! Please Login Again.";
                System.out.println("Session invalid, adminID: " + session.getAttribute("adminID"));
            } else {
                errorMessage = "Not Logged In or Session Timed Out! Please Login Again.";
                System.out.println("No session exists");
            }
            errorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            response.sendRedirect(
                    request.getContextPath() + AdminURLMapper.LOGIN_PAGE + "?errorMessage=" + errorMessage);
            return true;
        }
        return false;
    }

    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession(false);

        System.out.println("Servlet Path: " + servletPath);

        // Handle root admin page
        if (servletPath.equals(AdminURLMapper.HOME)) {
            if (ADMIN_LOGIN_SERVICE_AVAILABLE) {
                if (session != null && session.getAttribute("adminID") != null) {
                    response.sendRedirect(request.getContextPath() + AdminURLMapper.HOME_PAGE);
                } else {
                    response.sendRedirect(request.getContextPath() + AdminURLMapper.LOGIN_PAGE);
                }
                return;
            } else {
                directToErrorPage(request, response, "Main Service is Down ... For Now!");
                return;
            }
        }

        // Orders Servlet
        if (servletPath.equals(AdminURLMapper.ORDERS)) {
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.ORDERS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Orders Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Purchase History Page
        if (servletPath.equals(AdminURLMapper.PURCHASE_HISTORY)) {
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.PURCHASE_HISTORY_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Purchase History Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Purchase History API
        if (servletPath.equals(AdminURLMapper.PURCHASE_HISTORY_API)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.PURCHASE_HISTORY_API_SERVLET);
            if (dispatcher == null) {
                System.out.println("Dispatcher null for " + AdminURLMapper.PURCHASE_HISTORY_API_SERVLET);
                directToErrorPage(request, response, "Purchase History API Servlet not found.");
                return;
            }
            System.out.println("Forwarding to PurchaseHistoryApiServlet");
            dispatcher.forward(request, response);
            return;
        }

        // Purchase History Receipt API (including download)
        if (servletPath.startsWith(AdminURLMapper.PURCHASE_HISTORY_RECEIPT_API.substring(0,
                AdminURLMapper.PURCHASE_HISTORY_RECEIPT_API.length() - 1))) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.PURCHASE_HISTORY_RECEIPT_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Receipt Details API Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        if (servletPath.equals(AdminURLMapper.SEARCH)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.SEARCH_SERVLET);
            dispatcher.forward(request, response);
            return;
        }

        if (servletPath.equals(AdminURLMapper.LOGOUT)) {
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.LOGOUT_SERVLET);
            dispatcher.forward(request, response);
            return;
        }

        // Banners Servlet
        if (servletPath.equals(AdminURLMapper.BANNERS)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.BANNERS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Banners Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Books Servlet
        if (servletPath.equals(AdminURLMapper.BOOKS)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.BOOKS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Books Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Categories Servlet
        if (servletPath.equals(AdminURLMapper.CATEGORIES)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.CATEGORIES_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Categories Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        directToErrorPage(request, response, "No Get Method Registered for this URL.");
    }

    @Override
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession(false);

        if (servletPath.equals(AdminURLMapper.LOGIN)) {
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.LOGIN_SERVLET);
            dispatcher.forward(request, response);
            return;
        }

        // Update Order Status Servlet
        if (servletPath.equals(AdminURLMapper.UPDATE_ORDER_STATUS)) {
            System.out.println("Forwarding to UpdateOrderStatusServlet");
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.UPDATE_ORDER_STATUS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Update Order Status Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Banners Servlet
        if (servletPath.equals(AdminURLMapper.BANNERS)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.BANNERS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Banners Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Books Servlet
        if (servletPath.equals(AdminURLMapper.BOOKS)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.BOOKS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Books Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        // Discounts Servlet
        if (servletPath.equals(AdminURLMapper.DISCOUNTS)) {
            if (redirectOnHavingInvalidSession(request, response, session)) {
                return;
            }
            var dispatcher = getServletContext().getNamedDispatcher(AdminURLMapper.DISCOUNTS_SERVLET);
            if (dispatcher == null) {
                directToErrorPage(request, response, "Discounts Servlet not found.");
                return;
            }
            dispatcher.forward(request, response);
            return;
        }

        directToErrorPage(request, response, "No Post Method Registered for this URL.");
    }

    @Override
    public void doPut(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession(false);

        directToErrorPage(request, response, "No Put Method Registered for this URL.");
    }

    @Override
    public void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        HttpSession session = request.getSession(false);

        directToErrorPage(request, response, "No Delete Method Registered for this URL.");
    }

    @Override
    public void doHead(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        directToErrorPage(request, response, "No Head Method Registered for this URL.");
    }

    @Override
    public void doOptions(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        directToErrorPage(request, response, "No Options Method Registered for this URL.");
    }

    @Override
    public void doTrace(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        directToErrorPage(request, response, "No Trace Method Registered for this URL.");
    }
}