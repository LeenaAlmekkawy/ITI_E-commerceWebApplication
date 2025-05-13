package jets.projects.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class GetUserID {

    public static Long getUserId(HttpServletRequest request) {

        long id = 1L;

        HttpSession session = request.getSession(false);

        if (session != null) {

            if (session.getAttribute("userID") != null) {

                id = (Long) session.getAttribute("userID");

                System.out.println("user id is" + id);
            }
        }
        System.out.println("ohh ");

        return id;
    }
}
