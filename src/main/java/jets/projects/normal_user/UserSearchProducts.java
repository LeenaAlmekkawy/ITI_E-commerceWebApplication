package jets.projects.normal_user;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jets.projects.beans.ProductBean;
import jets.projects.dal.ProductsDAL;

public class UserSearchProducts extends HttpServlet {
    private static final ProductsDAL productsDAL = new ProductsDAL();
    
    @Override
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String searchKey = request.getParameter("searchKey");
        if (searchKey == null) {
            String errorMessage = URLEncoder.encode(
                    "Search Key Must be Provided!",
                    StandardCharsets.UTF_8);
            
            response.sendRedirect(request.getContextPath()
                    + UserURLMapper.WELCOME_PAGE
                    + "?errorMessage=" + errorMessage);
            return;
        }
        
        searchKey = searchKey.trim();
        if (searchKey.isEmpty()) {
            String errorMessage = URLEncoder.encode(
                    "Cannot search for an empty string.",
                    StandardCharsets.UTF_8);

            response.sendRedirect(request.getContextPath()
                    + UserURLMapper.HOME_PAGE
                    + "?errorMessage=" + errorMessage);
            return;
        }
        
        List<ProductBean> foundProducts = productsDAL.searchProducts(
                searchKey, false);
        if (foundProducts == null) {
            String errorMessage = URLEncoder.encode(
                    "An error Happened while Searching!",
                    StandardCharsets.UTF_8);
            
            response.sendRedirect(request.getContextPath()
                    + UserURLMapper.HOME_PAGE
                    + "?errorMessage=" + errorMessage);
            return;
        }
        
        HttpSession session = request.getSession();
        session.setAttribute("result", foundProducts);
        response.sendRedirect(request.getContextPath()
                    + UserURLMapper.HOME_PAGE);
    }
}
