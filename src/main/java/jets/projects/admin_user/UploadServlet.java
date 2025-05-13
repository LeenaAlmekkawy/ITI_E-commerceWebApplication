package jets.projects.admin_user;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10,      // 10 MB
        maxRequestSize = 1024 * 1024 * 50    // 50 MB
)
public class UploadServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UploadServlet.class.getName());
    private static final String BASE_URL = "http://localhost:8080/ITI-Jets-EcommApplication/Admin/Images/";
    private static final String UPLOAD_DIR = "C:/Users/ibrah/OneDrive/Desktop/Tools/apache-tomcat-10.1.39/webapps/ITI-Jets-EcommApplication/Admin/Images/";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info("Received file upload request");
        System.out.println("hello in upload");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                throw new IllegalArgumentException("No file uploaded or file is empty");
            }

            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid file name");
            }

            // Use a fixed upload directory
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new IOException("Failed to create upload directory: " + UPLOAD_DIR);
                }
                LOGGER.info("Created upload directory: " + UPLOAD_DIR);
            }

            // Verify directory is writable
            if (!Files.isWritable(Paths.get(UPLOAD_DIR))) {
                throw new IOException("Upload directory is not writable: " + UPLOAD_DIR);
            }

            File file = new File(uploadDir, fileName);
            LOGGER.info("Attempting to write file to: " + file.getAbsolutePath());
            filePart.write(file.getAbsolutePath());
            LOGGER.info("File uploaded successfully: " + file.getAbsolutePath());
            System.out.println("File saved to: " + file.getAbsolutePath());

            // Generate the full URL in the required format
            String fileUrl = BASE_URL + fileName;
            response.getWriter().write("{\"status\":\"success\",\"fileUrl\":\"" + fileUrl + "\"}");
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LOGGER.severe("Error uploading file: " + e.getMessage());
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}