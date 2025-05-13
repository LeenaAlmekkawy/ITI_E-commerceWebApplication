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
            <title>BookStore Admin - Discounts & Promotions</title>
            <link rel="stylesheet" href="CSS/styles.css">
            <link rel="stylesheet" href="CSS/discounts.css">
            <link rel="stylesheet"
                href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap">
            <link rel="icon" type="image/x-icon" href="Assets/tabIcon.ico">
        </head>

        <body>
            <div class="admin-container">
                <!-- Sidebar Navigation -->
                <aside class="sidebar">
                    <div class="sidebar-header">
                        <h2>BookStore</h2>
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
                            <li><a href="users.jsp"><span class="icon">👥</span> Users</a></li>
                            <li class="active"><a href="discounts.jsp"><span class="icon">🏷️</span> Discounts</a></li>
                            <li><a href="purchase-history.jsp"><span class="icon">📝</span> Purchase History</a></li>
                            <li class="logout"><a href="#" id="logoutBtn"><span class="icon">🚪</span> Logout</a></li>
                        </ul>
                    </nav>
                </aside>

                <!-- Main Content -->
                <main class="main-content">
                    <header class="content-header">
                        <h1>Discounts & Promotions</h1>
                        <div class="user-info">
                            <span id="currentUser">
                                <%=adminUsername%>
                            </span>
                        </div>
                    </header>

                    <!-- Discounts Content -->
                    <div class="discounts-content">
                        <!-- Tabs -->
                        <div class="tabs">
                            <button class="tab-btn active" data-tab="banners">Promotional Banners</button>
                            <button class="tab-btn" data-tab="discounts">Book Discounts</button>
                        </div>

                        <!-- Banners Tab -->
                        <div class="tab-content active" id="banners-tab">
                            <div class="action-bar">
                                <button id="addBannerBtn" class="btn btn-primary">Add New Banner</button>
                            </div>

                            <div class="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Banner</th>
                                            <th>Title</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody id="bannersTable">
                                        <!-- Banners will be loaded here -->
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- Discounts Tab -->
                        <div class="tab-content" id="discounts-tab">
                            <div class="action-bar">
                                <button id="addDiscountBtn" class="btn btn-primary">Add New Discount</button>
                                <button id="removeAllDiscountsBtn" class="btn btn-danger">Remove All Discounts</button>
                            </div>

                            <div class="table-container">
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Cover</th>
                                            <th>Title</th>
                                            <th>Original Price</th>
                                            <th>Discount</th>
                                            <th>Final Price</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody id="discountedBooksTable">
                                        <!-- Discounted books will be loaded here -->
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </main>
            </div>

            <!-- Add/Edit Banner Modal -->
            <div id="bannerModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 id="bannerModalTitle">Add New Banner</h2>
                        <span class="close">&times;</span>
                    </div>
                    <div class="modal-body">
                        <form id="bannerForm">
                            <input type="hidden" id="bannerId">
                            <div class="form-group">
                                <label for="bannerTitle">Banner Title*</label>
                                <input type="text" id="bannerTitle" name="bannerTitle" required>
                            </div>
                            <div class="form-group">
                                <label for="bannerImage">Banner Image*</label>
                                <input type="file" id="bannerImage" name="bannerImage" accept="image/*" required>
                                <div id="bannerImagePreview" class="banner-preview"></div>
                            </div>
                            <div class="form-group">
                                <label for="bannerText">Banner Text</label>
                                <textarea id="bannerText" name="bannerText" rows="3"></textarea>
                            </div>
                            <div class="form-actions">
                                <button type="button" id="cancelBannerBtn" class="btn">Cancel</button>
                                <button type="submit" class="btn btn-primary">Save Banner</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Add/Edit Discount Modal -->
            <div id="discountModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 id="discountModalTitle">Add New Discount</h2>
                        <span class="close">&times;</span>
                    </div>
                    <div class="modal-body">
                        <form id="discountForm">
                            <input type="hidden" id="discountId">
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="discountType">Discount Type*</label>
                                    <select id="discountType" name="discountType" required>
                                        <option value="percentage">Percentage</option>
                                        <option value="fixed">Fixed Amount</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="discountValue">Discount Value*</label>
                                    <input type="number" id="discountValue" name="discountValue" step="0.01" required>
                                    <span id="valueType">%</span>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="discountOperation">Discount Operation*</label>
                                    <select id="discountOperation" name="discountOperation" required>
                                        <option value="reset">Reset (Replace current discount)</option>
                                        <option value="add">Add to current discount</option>
                                        <option value="subtract">Subtract from current discount</option>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="applyTo">Apply To*</label>
                                    <select id="applyTo" name="applyTo" required>
                                        <option value="all">All Books</option>
                                        <option value="category">Specific Category</option>
                                        <option value="books">Group of Books</option>
                                    </select>
                                </div>
                            </div>
                            <div id="categorySelector" class="form-group hidden">
                                <label for="category">Select Category</label>
                                <select id="category" name="category">
                                    <option value="">Select Category</option>
                                    <option value="Fiction">Fiction</option>
                                    <option value="Non-Fiction">Non-Fiction</option>
                                    <option value="Science Fiction">Science Fiction</option>
                                    <option value="Fantasy">Fantasy</option>
                                    <option value="Mystery">Mystery</option>
                                    <option value="Romance">Romance</option>
                                    <option value="Thriller">Thriller</option>
                                    <option value="Biography">Biography</option>
                                    <option value="History">History</option>
                                    <option value="Self-Help">Self-Help</option>
                                </select>
                            </div>
                            <div id="booksSelector" class="form-group hidden">
                                <label for="books">Select Books</label>
                                <div class="books-selection">
                                    <div class="books-list" id="availableBooks">
                                        <h4>Available Books</h4>
                                        <div class="books-container" id="availableBooksContainer">
                                            <!-- Available books will be loaded here -->
                                        </div>
                                    </div>
                                    <div class="books-controls">
                                        <button type="button" id="addSelectedBooks" class="btn">→</button>
                                        <button type="button" id="removeSelectedBooks" class="btn">←</button>
                                    </div>
                                    <div class="books-list" id="selectedBooks">
                                        <h4>Selected Books</h4>
                                        <div class="books-container" id="selectedBooksContainer">
                                            <!-- Selected books will be loaded here -->
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="form-actions">
                                <button type="button" id="cancelDiscountBtn" class="btn">Cancel</button>
                                <button type="submit" class="btn btn-primary">Apply Discount</button>
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
                        <span class="close">&times;</span>
                    </div>
                    <div class="modal-body">
                        <p id="deleteMessage">Are you sure you want to delete this item? This action cannot be undone.
                        </p>
                        <div class="form-actions">
                            <button type="button" id="cancelDeleteBtn" class="btn">Cancel</button>
                            <button type="button" id="confirmDeleteBtn" class="btn btn-danger">Delete</button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Remove All Discounts Confirmation Modal -->
            <div id="removeAllDiscountsModal" class="modal">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2>Confirm Remove All Discounts</h2>
                        <span class="close">&times;</span>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure you want to remove all discounts from all books? This action cannot be undone.
                        </p>
                        <div class="form-actions">
                            <button type="button" id="cancelRemoveAllBtn" class="btn">Cancel</button>
                            <button type="button" id="confirmRemoveAllBtn" class="btn btn-danger">Remove All</button>
                        </div>
                    </div>
                </div>
            </div>
            <script>
                const contextPath = "${pageContext.request.contextPath}";
            </script>

            <script src="JavaScript/common.js"></script>
            <script src="JavaScript/discount_.js"></script>
        </body>

        </html>