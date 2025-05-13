package jets.projects.admin_user;

import com.google.gson.Gson;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jets.projects.dto.UserAdminDto;
import jets.projects.exceptions.NotFoundException;
import jets.projects.services.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ServerEndpoint("/Admin/ViewUsers")
public class AdminViewUsersServer {
    private static final Set<Session> sessions = new HashSet<>();
    private final UserService userService;
    private final Gson gson;

    public AdminViewUsersServer() {
        this.userService = new UserService();
        this.gson = new Gson();
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Session " + session.getId() + " opened connection");
        sessions.add(session);
        sendUsers(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if ("refresh".equalsIgnoreCase(message)) {
            sendUsers(session);
        }
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " closed");
        sessions.remove(session);
    }

    private void sendUsers(Session session) {
        try {
            List<UserAdminDto> users = userService.getAllUsers();
            String usersJson = gson.toJson(users);
            System.out.println("Sending users: " + usersJson);
            session.getBasicRemote().sendText(usersJson);
        } catch (NotFoundException e) {
            System.out.println("No users found: " + e.getMessage());
            try {
                session.getBasicRemote().sendText(gson.toJson(new ErrorResponse("No users found")));
            } catch (Exception ex) {
                System.out.println("Error sending response: " + ex.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error fetching users: " + e.getMessage());
            try {
                session.getBasicRemote().sendText(gson.toJson(new ErrorResponse("Failed to fetch users")));
            } catch (Exception ex) {
                System.out.println("Error sending response: " + ex.getMessage());
            }
        }
    }

    public static void notifyAllClients() {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText("refresh");
            } catch (Exception e) {
                System.out.println("Error notifying client: " + e.getMessage());
            }
        }
    }

    private static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}