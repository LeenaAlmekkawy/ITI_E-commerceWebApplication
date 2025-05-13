package jets.projects.admin_user.purchaseHistory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jets.projects.utils.JpaUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

@WebServlet("/Admin/ReceiptDownloadServlet/*")
public class ReceiptDownloadServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ReceiptDownloadServlet.class.getName());
    private EntityManagerFactory emf;

    @Override
    public void init() throws ServletException {
        emf = JpaUtil.getEntityManagerFactory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            LOGGER.warning("Invalid pathInfo: " + pathInfo);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Purchase ID required");
            return;
        }

        try {
            Long purchaseId = Long.parseLong(pathInfo.substring(1));
            LOGGER.info("Fetching receipt for purchase ID: " + purchaseId);

            String receiptUrl = getReceiptUrl(purchaseId);
            if (receiptUrl == null || receiptUrl.isEmpty()) {
                LOGGER.warning("No receipt URL found for purchase ID: " + purchaseId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Receipt URL not found for purchase ID: " + purchaseId);
                return;
            }

            // Convert relative URL to filesystem path
            String relativePath = receiptUrl.startsWith("/ITI-Jets-EcommApplication/receipts/")
                    ? receiptUrl.substring("/ITI-Jets-EcommApplication".length())
                    : receiptUrl;
            String filePath = getServletContext().getRealPath(relativePath);
            LOGGER.info("Resolved file path: " + filePath);

            File pdfFile = new File(filePath);
            if (!pdfFile.exists() || !pdfFile.isFile()) {
                LOGGER.severe("PDF file not found at: " + filePath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "PDF file not found for purchase ID: " + purchaseId);
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"receipt_" + purchaseId + ".pdf\"");
            response.setContentLengthLong(pdfFile.length());

            try (FileInputStream in = new FileInputStream(pdfFile);
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                LOGGER.info("Successfully served PDF for purchase ID: " + purchaseId);
            } catch (IOException e) {
                LOGGER.severe("Error serving PDF for purchase ID " + purchaseId + ": " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving receipt");
            }
        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid purchase ID: " + pathInfo);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid purchase ID");
        } catch (Exception e) {
            LOGGER.severe("Error processing download for path " + pathInfo + ": " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching receipt");
        }
    }

    private String getReceiptUrl(Long purchaseId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT p.receiptFileUrl FROM PurchaseHistory p WHERE p.id = :id",
                    String.class
            );
            query.setParameter("id", purchaseId);
            return query.getSingleResult();
        } catch (NoResultException e) {
            LOGGER.warning("No receipt URL found for purchase ID: " + purchaseId);
            return null;
        } catch (Exception e) {
            LOGGER.severe("Error fetching receipt URL for purchase ID " + purchaseId + ": " + e.getMessage());
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void destroy() {
        if (emf != null) {
            emf.close();
        }
    }
}