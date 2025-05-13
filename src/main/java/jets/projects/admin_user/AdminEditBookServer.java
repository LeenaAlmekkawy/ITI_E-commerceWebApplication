package jets.projects.admin_user;

import com.google.gson.Gson;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import jets.projects.beans.BookBean;
import jets.projects.client_dto.BookDto;
import jets.projects.services.BookService;
import jets.projects.utils.MapperUtil;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jets.projects.utils.MapperUtil.convertToBookDto;

@ServerEndpoint("/Admin/EditBook")
public class AdminEditBookServer {

    private static final Logger LOGGER = Logger.getLogger(AdminEditBookServer.class.getName());
    private static Set<Session> sessions = new HashSet<>();
    private final BookService bookService;

    public AdminEditBookServer() {
        this.bookService = new BookService();
    }

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info(session.getId() + " has opened a connection in admin edit book server");
        sessions.add(session);
        LOGGER.info("Session count: " + sessions.size());
        sendBooks(session);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Gson gson = new Gson();
        LOGGER.info("Received edit book message: " + message);
        try {
            BookBean updatedBook = gson.fromJson(message, BookBean.class);
            // Validate required fields
            if ( updatedBook.getId() <= 0) {
                throw new IllegalArgumentException("Book ID is required and must be positive");
            }
            if (updatedBook.getTitle() == null || updatedBook.getTitle().trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (updatedBook.getAuthor() == null || updatedBook.getAuthor().trim().isEmpty()) {
                throw new IllegalArgumentException("Author is required");
            }
            if (updatedBook.getIsbn() == null || updatedBook.getIsbn().trim().isEmpty()) {
                throw new IllegalArgumentException("ISBN is required");
            }
            if (updatedBook.getPrice() == null || updatedBook.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Price must be positive");
            }
            if (updatedBook.getMainImage() == null || updatedBook.getMainImage().trim().isEmpty()) {
                throw new IllegalArgumentException("Main image is required");
            }

            BookDto bookDto = convertToBookDto(updatedBook);
            boolean success = bookService.editBook((long) updatedBook.getId(), bookDto);
            if (!success) {
                throw new RuntimeException("Failed to update book in the database");
            }
            LOGGER.info("Successfully edited book ID: " + updatedBook.getId());
            for (Session userSession : sessions) {
                sendBooks(userSession);
            }
            session.getBasicRemote().sendText("{\"status\":\"success\",\"message\":\"Book updated successfully\"}");
        } catch (Exception e) {
            LOGGER.severe("Error editing book: " + e.getMessage());
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
            } catch (Exception ex) {
                LOGGER.severe("Error sending error message: " + ex.getMessage());
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.info("Session " + session.getId() + " closed");
        sessions.remove(session);
    }

    public void sendBooks(Session session) {
        Gson gson = new Gson();
        try {
            List<BookDto> bookDtos = bookService.getAllBooks();
            List<BookBean> bookBeans = bookDtos.stream()
                    .map(MapperUtil::convertToBookBean)
                    .collect(Collectors.toList());
            String booksJson = gson.toJson(bookBeans);
            LOGGER.info("Sending updated books list: " + booksJson);
            session.getBasicRemote().sendText(booksJson);
        } catch (Exception e) {
            LOGGER.severe("Error sending books: " + e.getMessage());
        }
    }
}