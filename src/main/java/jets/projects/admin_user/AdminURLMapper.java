package jets.projects.admin_user;

public class AdminURLMapper {

    public static final String ERROR_PAGE = "/Error/error_page.jspx";

    public static final String HOME = "/Admin/Home";
    public static final String HOME_SERVLET = "AdminDirector";

    public static final String LOGIN = "/Admin/Login";
    public static final String LOGIN_SERVLET = "AdminLogin";

    public static final String LOGOUT = "/Admin/Logout";
    public static final String LOGOUT_SERVLET = "AdminLogout";

    public static final String USERS = "/Admin/AdminUsers";

    public static final String SEARCH = "/Admin/AdminSearchProducts";
    public static final String SEARCH_SERVLET = "AdminSearchProducts";

    public static final String LOGIN_PAGE = "/Admin/admin_login.jsp";
    public static final String HOME_PAGE = "/Admin/admin_home.jspx";
    public static final String DASHBOARD_PAGE = "/Admin/dashboard.jsp";

    // Purchase History URLs
//    public static final String PURCHASE_HISTORY = "/Admin/PurchaseHistory";
//    public static final String PURCHASE_HISTORY_SERVLET = "jets.projects.admin_user.purchaseHistory.PurchaseHistoryServlet";
//    public static final String PURCHASE_HISTORY_PAGE = "/purchase-history.jsp";
//    public static final String PURCHASE_HISTORY_STATS_SERVLET= "jets.projects.admin_user.purchaseHistory.StatsServlet";
//    public static final String PURCHASE_HISTORY_STATS_API = "/Admin/StatsServlet";
//
//    public static final String PURCHASE_HISTORY_API = "/Admin/PurchaseHistory/api";
//    public static final String PURCHASE_HISTORY_API_SERVLET = "PurchaseHistoryApiServlet";
//
//    public static final String PURCHASE_HISTORY_RECEIPT_API = "/Admin/PurchaseHistory/api/*";
//    public static final String PURCHASE_HISTORY_RECEIPT_SERVLET = "jets.projects.admin_user.purchaseHistory.ReceiptDetailsApiServlet";

    // Orders URLs
    public static final String ORDERS = "/Admin/OrdersServlet";
    public static final String ORDERS_SERVLET = "jets.projects.admin_user.orders.OrdersServlet";

    // Update Order Status URLs
    public static final String UPDATE_ORDER_STATUS = "/Admin/UpdateOrderStatusServlet";
    public static final String UPDATE_ORDER_STATUS_SERVLET = "jets.projects.admin_user.orders.UpdateOrderStatusServlet";

    // Banners URLs
    public static final String BANNERS = "/Admin/BannersServlet";
    public static final String BANNERS_SERVLET = "jets.projects.admin_user.discounts.BannersServlet";

    // Books URLs
    public static final String BOOKS = "/Admin/BooksServlet";
    public static final String BOOKS_SERVLET = "jets.projects.admin_user.discounts.BooksServlet";

    // Categories URLs
    public static final String CATEGORIES = "/Admin/CategoriesServlet";
    public static final String CATEGORIES_SERVLET = "jets.projects.admin_user.discounts.CategoriesServlet";

    // Discounts URLs
    public static final String DISCOUNTS = "/Admin/DiscountsServlet";
    public static final String DISCOUNTS_SERVLET = "jets.projects.admin_user.discounts.DiscountsServlet";

    public static final String BOOKS_PAGE = "/Admin/books.jsp";
}