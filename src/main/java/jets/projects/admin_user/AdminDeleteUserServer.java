package jets.projects.admin_user;

import com.google.gson.Gson;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jets.projects.dao.UserDao;

@ServerEndpoint("/Admin/DeleteUser")
public class AdminDeleteUserServer {
    private final UserDao userDao;
    private final Gson gson;

    public AdminDeleteUserServer() {
        this.userDao = new UserDao();
        this.gson = new Gson();
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("DeleteUserServer session opened");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            Long userId = Long.parseLong(message);
            userDao.deleteById(userId);
            System.out.println("Deleted user ID: " + userId);
            AdminViewUsersServer.notifyAllClients();
        } catch (NumberFormatException e) {
            System.out.println("Invalid user ID: " + message);
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}