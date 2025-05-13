<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import="jakarta.servlet.http.HttpSession, jets.projects.admin_user.AdminURLMapper" %>
    <% String adminUsername=null; HttpSession currentSession=request.getSession(false); if (currentSession==null) {
        response.sendRedirect(request.getContextPath() + AdminURLMapper.LOGIN_PAGE); return; } String
        isAdminLoggedIn=(String) currentSession.getAttribute("adminLoggedIn"); if (isAdminLoggedIn !=null &&
        isAdminLoggedIn.equals("true")) { adminUsername=(String) currentSession.getAttribute("adminUsername"); } else {
        response.sendRedirect(request.getContextPath() + AdminURLMapper.LOGIN_PAGE); return; } %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Order Management</title>
            <link rel="stylesheet" href="CSS/styles.css">
            <link rel="stylesheet" href="CSS/orders_.css">
            <link rel="stylesheet"
                href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap">
            <link rel="icon" type="image/x-icon" href="Assets/tabIcon.ico">

        </head>

        <body>
            <div class="admin-container">
                <!-- Sidebar Navigation -->
                <aside class="sidebar" style="position: sticky; top: 0;">
                    <div class="sidebar-header">
                        <h2>Book Alley</h2>
                        <button id="sidebarToggle" class="sidebar-toggle">
                            <span></span>
                            <span></span>
                            <span></span>
                        </button>
                    </div>
                    <nav class="sidebar-nav">
                        <ul>
                            <li><a href="dashboard.jsp"><span class="icon">📊</span> Dashboard</a></li>
                            <li><a href="books.jsp"><span class="icon">📚</span> Books</a></li>
                            <li class="active"><a href="orders.jsp"><span class="icon">📦</span> Orders</a></li>
                            <li><a href="users.jsp"><span class="icon">👥</span> Users</a></li>
                            <li><a href="purchase-history.jsp"><span class="icon">📝</span> Purchase History</a></li>
                            <li class="logout"><a href="#" id="logoutBtn"><span class="icon">🚪</span> Logout</a></li>
                        </ul>
                    </nav>
                </aside>

                <!-- Main Content -->
                <main class="main-content">
                    <header class="content-header">
                        <h1>Order Management</h1>
                        <div class="user-info">
                            <span id="currentUser">
                                <%= adminUsername %>
                            </span>
                        </div>
                    </header>

                    <!-- Orders Content -->
                    <div class="orders-content">
                        <!-- Action Bar -->
                        <div class="action-bar">
                            <div class="filter-container">
                                <label for="statusFilter">Filter by Status:</label>
                                <select id="statusFilter">
                                    <option value="all">All Orders</option>
                                    <option value="Pending">Pending</option>
                                    <option value="Shipped">Shipped</option>
                                    <option value="Delivered">Delivered</option>
                                    <option value="Canceled">Canceled</option>
                                </select>
                            </div>
                            <div class="search-container">
                                <input type="text" id="orderSearch" placeholder="Search orders...">
                                <button id="searchBtn" class="btn">Search</button>
                            </div>
                        </div>

                        <!-- Orders Table -->
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Order ID</th>
                                        <th>Customer</th>
                                        <th>Date</th>
                                        <th>Total</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody id="ordersTable">
                                    <!-- Orders will be loaded here -->
                                </tbody>
                            </table>
                        </div>
                    </div>
                </main>
            </div>

            <!-- Order Details Modal -->
            <div id="orderModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>Order Details</h2>
                        <span class="close">×</span>
                    </div>
                    <div class="modal-body">
                        <div class="order-info">
                            <div class="order-header">
                                <div>
                                    <h3>Order #<span id="modalOrderId"></span></h3>
                                    <p>Date: <span id="modalOrderDate"></span></p>
                                </div>
                                <div class="order-status">
                                    <label for="modalOrderStatus">Status:</label>
                                    <select id="modalOrderStatus">
                                        <option value="Pending">Pending</option>
                                        <option value="Shipped">Shipped</option>
                                        <option value="Delivered">Delivered</option>
                                        <option value="Canceled">Canceled</option>
                                    </select>
                                    <button id="updateStatusBtn" class="btn btn-primary">Update</button>
                                </div>
                            </div>

                            <div class="customer-info">
                                <h4>Customer Information</h4>
                                <p><strong>Name:</strong> <span id="modalCustomerName"></span></p>
                                <p><strong>Email:</strong> <span id="modalCustomerEmail"></span></p>
                                <p><strong>Phone:</strong> <span id="modalCustomerPhone"></span></p>
                            </div>

                            <div class="shipping-info">
                                <h4>Shipping Address</h4>
                                <p id="modalShippingAddress"></p>
                            </div>

                            <div class="order-items">
                                <h4>Order Items</h4>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Book</th>
                                            <th>Price</th>
                                            <th>Quantity</th>
                                            <th>Total</th>
                                        </tr>
                                    </thead>
                                    <tbody id="modalOrderItems">
                                        <!-- Order items will be loaded here -->
                                    </tbody>
                                </table>
                            </div>

                            <div class="order-summary">
                                <div class="summary-item">
                                    <span>Subtotal:</span>
                                    <span id="modalSubtotal"></span>
                                </div>
                                <div class="summary-item">
                                    <span>Shipping:</span>
                                    <span id="modalShipping"></span>
                                </div>
                                <div class="summary-item total">
                                    <span>Total:</span>
                                    <span id="modalTotal"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <script>
                const contextPath = "${pageContext.request.contextPath}";
            </script>
            <script src="JavaScript/common.js"></script>
            <script src="JavaScript/orders.js"></script>
        </body>

        </html>