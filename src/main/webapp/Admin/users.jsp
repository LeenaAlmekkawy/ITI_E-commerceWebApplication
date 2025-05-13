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
            <title>Book Alley Admin - User Management</title>
            <link rel="stylesheet" href="CSS/styles.css">
            <link rel="stylesheet" href="CSS/users.css">
            <link rel="stylesheet"
                href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap">
            <link rel="icon" type="image/x-icon" href="Assets/tabIcon.ico">

        </head>

        <body onload="connect()">
            <div class="admin-container">
                <!-- Sidebar Navigation -->
                <aside class="sidebar">
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
                            <li><a href="orders.jsp"><span class="icon">📦</span> Orders</a></li>
                            <li class="active"><a href="users.jsp"><span class="icon">👥</span> Users</a></li>
                            <li><a href="purchase-history.jsp"><span class="icon">📝</span> Purchase History</a></li>
                            <li class="logout"><a href="#" id="logoutBtn"><span class="icon">🚪</span> Logout</a></li>
                        </ul>
                    </nav>
                </aside>

                <!-- Main Content -->
                <main class="main-content">
                    <header class="content-header">
                        <h1>User Management</h1>
                        <div class="user-info">
                            <span id="currentUser">
                                <%=adminUsername%>
                            </span>
                        </div>
                    </header>

                    <!-- Users Content -->
                    <div class="users-content">
                        <!-- Action Bar -->
                        <div class="action-bar">
                            <div class="search-container">
                                <input type="text" id="userSearch" placeholder="Search users...">
                                <button id="searchBtn" class="btn">Search</button>
                            </div>
                        </div>

                        <!-- Users Table -->
                        <div class="table-container">
                            <table>
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Orders</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody id="usersTable">
                                    <!-- Users will be loaded here -->
                                </tbody>
                            </table>
                        </div>
                        <div class="pagination">
                            <button id="prevUserPage" class="btn">Previous</button>
                            <span id="userPageIndicator"></span>
                            <button id="nextUserPage" class="btn">Next</button>
                        </div>
                    </div>
                </main>
            </div>

            <!-- User Details Modal -->
            <div id="userModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 id="modalTitle">View User</h2>
                        <span class="close">×</span>
                    </div>
                    <div class="modal-body">
                        <form id="userForm">
                            <input type="hidden" id="userId">
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="id">ID</label>
                                    <input type="text" id="id" name="id" readonly>
                                </div>
                                <div class="form-group">
                                    <label for="username">Name</label>
                                    <input type="text" id="username" name="username" readonly>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="email">Email</label>
                                    <input type="email" id="email" name="email" readonly>
                                </div>
                                <div class="form-group">
                                    <label for="phoneNumber">Phone</label>
                                    <input type="tel" id="phoneNumber" name="phoneNumber" readonly>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="birthDate">Birthday</label>
                                    <input type="text" id="birthDate" name="birthDate" readonly>
                                </div>
                                <div class="form-group">
                                    <label for="balance">Balance</label>
                                    <input type="text" id="balance" name="balance" readonly>
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="address">Address</label>
                                <textarea id="address" name="address" rows="3" readonly></textarea>
                            </div>
                            <div class="form-group">
                                <label for="interests">Interests</label>
                                <textarea id="interests" name="interests" rows="3" readonly></textarea>
                            </div>
                            <div class="form-actions">
                                <button type="button" id="cancelBtn" class="btn">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Delete Confirmation Modal -->
            <div id="deleteModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>Confirm Deletion</h2>
                        <span class="close">×</span>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure you want to delete this user? This action cannot be undone.</p>
                        <div class="form-actions">
                            <button type="button" id="cancelDeleteBtn" class="btn">Cancel</button>
                            <button type="button" id="confirmDeleteBtn" class="btn btn-danger">Delete</button>
                        </div>
                    </div>
                </div>
            </div>

            <script src="JavaScript/common.js"></script>
            <script src="JavaScript/users_.js"></script>
        </body>

        </html>