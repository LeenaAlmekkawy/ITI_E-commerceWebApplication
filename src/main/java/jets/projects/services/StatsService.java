package jets.projects.services;

import jets.projects.dao.BookDao;
import jets.projects.dao.OrderDao;
import jets.projects.dao.PurchaseHistoryDao;
import jets.projects.dao.UserDao;
import jets.projects.dto.DashboardStatsDTO;
import jets.projects.dto.StatsDto;
import jets.projects.entity.Book;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StatsService {
    private final PurchaseHistoryDao purchaseDAO;
    private static final Logger LOGGER = Logger.getLogger(StatsService.class.getName());
    private final BookDao bookDao;
    private final UserDao userDao;
    private final OrderDao orderDao;

    public StatsService() {
        this.bookDao = new BookDao();
        this.userDao = new UserDao();
        this.orderDao = new OrderDao();
        this.purchaseDAO = new PurchaseHistoryDao();
    }

    public StatsDto getPurchaseStats() {
        StatsDto stats = new StatsDto();
        stats.setTotalPurchases(purchaseDAO.countTotalPurchases());
        stats.setTotalRevenue(purchaseDAO.sumTotalRevenue());
        stats.setUniqueCustomers(purchaseDAO.countUniqueCustomers());
        return stats;
    }

    public DashboardStatsDTO getDashboardStats() {
        try {
            DashboardStatsDTO dto = new DashboardStatsDTO();

            // Basic stats
            dto.setTotalBooks(bookDao.countTotalBooks());
            dto.setTotalSales(purchaseDAO.sumTotalRevenue());
            dto.setTotalUsers(userDao.countTotalUsers());
            dto.setPendingOrders(orderDao.countPendingOrders());

            // Monthly sales (last 12 months)
            LocalDateTime startDate = LocalDateTime.now().minusMonths(12);
            List<Object[]> salesData = purchaseDAO.sumSalesByMonth(startDate);
            List<DashboardStatsDTO.MonthlySales> monthlySales = new ArrayList<>();
            for (Object[] row : salesData) {
                DashboardStatsDTO.MonthlySales ms = new DashboardStatsDTO.MonthlySales();
                ms.setYear((Integer) row[0]);
                ms.setMonth((Integer) row[1]);
                ms.setTotal((BigDecimal) row[2]);
                monthlySales.add(ms);
            }
            dto.setMonthlySales(monthlySales);

            // Top 5 selling books
            List<Book> topBooksData = bookDao.findTopSellingBooks(5);
            List<DashboardStatsDTO.TopBook> topBooks = new ArrayList<>();
            for (Book book : topBooksData) {
                DashboardStatsDTO.TopBook tb = new DashboardStatsDTO.TopBook();
                tb.setTitle(book.getTitle());
                tb.setSoldCount(book.getSoldCount());
                topBooks.add(tb);
            }
            dto.setTopBooks(topBooks);

            return dto;
        } catch (Exception e) {
            LOGGER.severe("Error fetching dashboard stats: " + e.getMessage());
            throw new RuntimeException("Failed to fetch dashboard stats", e);
        }
    }
}