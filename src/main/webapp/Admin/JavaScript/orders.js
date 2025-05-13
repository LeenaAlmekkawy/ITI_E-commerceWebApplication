// Initialize pagination variables
const itemsPerPage = 10;
let currentPage = 1;

document.addEventListener("DOMContentLoaded", () => {
    // Load orders
    loadOrders();

    // Status filter
    document.getElementById("statusFilter").addEventListener("change", () => {
        currentPage = 1;
        filterAndDisplayOrders();
    });

    // Search functionality
    document.getElementById("searchBtn").addEventListener("click", () => {
        currentPage = 1;
        filterAndDisplayOrders();
    });

    // Search on enter key
    document.getElementById("orderSearch").addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
            currentPage = 1;
            filterAndDisplayOrders();
        }
    });

    // Update order status
    document.getElementById("updateStatusBtn").addEventListener("click", () => {
        const newStatus = document.getElementById("modalOrderStatus").value;
        if (newStatus === "Delivered") {
            if (!confirm("Marking this order as Delivered will remove it from the list. Continue?")) {
                return;
            }
        }
        updateOrderStatus();
    });

    // Modal close handling
    const modal = document.getElementById("orderModal");
    const closeBtn = modal.querySelector(".close");
    closeBtn.addEventListener("click", () => closeModal(modal));
    modal.addEventListener("click", (e) => {
        if (e.target === modal) {
            closeModal(modal);
        }
    });
});

// Helper functions
function formatDate(dateString) {
    const date = new Date(dateString);
    const options = { year: "numeric", month: "long", day: "numeric" };
    return date.toLocaleDateString(undefined, options);
}

function formatCurrency(amount) {
    return amount.toLocaleString("en-US", { style: "currency", currency: "USD" });
}

function openModal(modal) {
    modal.style.display = "block";
}

function closeModal(modal) {
    modal.style.display = "none";
}

function normalizeStatus(status) {
    // Normalize status to match frontend expectations
    const statusMap = {
        "cancelled": "Canceled",
        "canceled": "Canceled",
        "pending": "Pending",
        "shipped": "Shipped",
        "delivered": "Delivered"
    };
    return statusMap[status.toLowerCase()] || status;
}

function loadOrders() {
    fetch(`${contextPath}/Admin/OrdersServlet`, {
        headers: { "Accept": "application/json" }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(orders => {
            // Normalize status for all orders
            const normalizedOrders = orders.map(order => ({
                ...order,
                status: normalizeStatus(order.status)
            }));
            console.log("Normalized orders:", normalizedOrders);
            localStorage.setItem("orders", JSON.stringify(normalizedOrders));
            filterAndDisplayOrders();
            setupPagination();
        })
        .catch(error => {
            console.error("Error fetching orders:", error);
            const cachedOrders = JSON.parse(localStorage.getItem("orders")) || [];
            filterAndDisplayOrders();
            setupPagination();
        });
}

function filterAndDisplayOrders() {
    const statusFilter = document.getElementById("statusFilter").value;
    const searchTerm = document.getElementById("orderSearch").value.toLowerCase();
    const orders = JSON.parse(localStorage.getItem("orders")) || [];
    let filteredOrders = orders;

    console.log("Status filter:", statusFilter);
    console.log("Search term:", searchTerm);
    console.log("All orders:", orders);

    if (statusFilter !== "all") {
        filteredOrders = filteredOrders.filter(order =>
            order.status.toLowerCase() === statusFilter.toLowerCase()
        );
    }

    if (searchTerm) {
        filteredOrders = filteredOrders.filter(order =>
            String(order.id).toLowerCase().includes(searchTerm) ||
            (order.customer.username || "").toLowerCase().includes(searchTerm)
        );
    }

    console.log("Filtered orders:", filteredOrders);

    // Adjust currentPage if necessary
    const totalPages = Math.ceil(filteredOrders.length / itemsPerPage);
    if (currentPage > totalPages && totalPages > 0) {
        currentPage = totalPages;
    } else if (filteredOrders.length === 0) {
        currentPage = 1;
    }

    // Apply pagination
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedOrders = filteredOrders.slice(startIndex, endIndex);

    displayOrders(paginatedOrders);
    updatePaginationControls(filteredOrders.length);
}

function displayOrders(orders) {
    const ordersTable = document.getElementById("ordersTable");
    ordersTable.innerHTML = "";

    if (orders.length === 0) {
        ordersTable.innerHTML = `
            <tr>
                <td colspan="6" class="no-orders">No orders found</td>
            </tr>
        `;
        return;
    }

    orders.forEach(order => {
        const statusClass = `status-${order.status.toLowerCase()}`;
        console.log(`Order ID: ${order.id}, Status: ${order.status}, Class: ${statusClass}`);
        ordersTable.innerHTML += `
            <tr>
                <td>${order.id}</td>
                <td>${order.customer.name}</td>
                <td>${formatDate(order.date)}</td>
                <td>${formatCurrency(order.total)}</td>
                <td><span class="status-badge ${statusClass}">${order.status}</span></td>
                <td>
                    <div class="action-buttons">
                        <button class="btn" onclick="viewOrder('${order.id}')">View</button>
                    </div>
                </td>
            </tr>
        `;
    });
}

function setupPagination() {
    let paginationDiv = document.getElementById("pagination");
    if (!paginationDiv) {
        paginationDiv = document.createElement("div");
        paginationDiv.id = "pagination";
        paginationDiv.className = "pagination";
        document.querySelector(".table-container").after(paginationDiv);
    }
    updatePaginationControls(JSON.parse(localStorage.getItem("orders"))?.length || 0);
}

function updatePaginationControls(totalItems) {
    let paginationDiv = document.getElementById("pagination");
    if (!paginationDiv) {
        paginationDiv = document.createElement("div");
        paginationDiv.id = "pagination";
        paginationDiv.className = "pagination";
        const tableContainer = document.querySelector(".table-container");
        if (tableContainer) {
            tableContainer.after(paginationDiv);
        } else {
            return; // Exit if table-container is not found to prevent errors
        }
    }
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    paginationDiv.innerHTML = "";

    if (totalPages <= 1) return;

    const prevButton = document.createElement("button");
    prevButton.innerText = "Previous";
    prevButton.disabled = currentPage === 1;
    prevButton.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            filterAndDisplayOrders();
        }
    });
    paginationDiv.appendChild(prevButton);

    const maxPageButtons = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxPageButtons / 2));
    let endPage = Math.min(totalPages, startPage + maxPageButtons - 1);
    if (endPage - startPage + 1 < maxPageButtons) {
        startPage = Math.max(1, endPage - maxPageButtons + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageButton = document.createElement("button");
        pageButton.innerText = i;
        pageButton.classList.toggle("active", i === currentPage);
        pageButton.addEventListener("click", () => {
            currentPage = i;
            filterAndDisplayOrders();
        });
        paginationDiv.appendChild(pageButton);
    }

    const nextButton = document.createElement("button");
    nextButton.innerText = "Next";
    nextButton.disabled = currentPage === totalPages;
    nextButton.addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            filterAndDisplayOrders();
        }
    });
    paginationDiv.appendChild(nextButton);
}

function viewOrder(id) {
    const orders = JSON.parse(localStorage.getItem("orders")) || [];
    const order = orders.find(order => String(order.id) === id);

    if (order) {
        document.getElementById("modalOrderId").textContent = order.id;
        document.getElementById("modalOrderDate").textContent = formatDate(order.date);
        document.getElementById("modalOrderStatus").value = order.status;
        document.getElementById("modalCustomerName").textContent = order.customer.name;
        document.getElementById("modalCustomerEmail").textContent = order.customer.email;
        document.getElementById("modalCustomerPhone").textContent = order.customer.phoneNumber || "N/A";
        document.getElementById("modalShippingAddress").textContent = order.customer.address || "N/A";

        const modalOrderItems = document.getElementById("modalOrderItems");
        modalOrderItems.innerHTML = "";
        order.items.forEach(item => {
            modalOrderItems.innerHTML += `
                <tr>
                    <td>${item.book}</td>
                    <td>${formatCurrency(item.price)}</td>
                    <td>${item.quantity}</td>
                    <td>${formatCurrency(item.total)}</td>
                </tr>
            `;
        });

        document.getElementById("modalSubtotal").textContent = formatCurrency(order.subtotal);
        document.getElementById("modalShipping").textContent = formatCurrency(order.shipping);
        document.getElementById("modalTotal").textContent = formatCurrency(order.total);

        document.getElementById("updateStatusBtn").setAttribute("data-id", order.id);
        openModal(document.getElementById("orderModal"));
    }
}

function updateOrderStatus() {
    const orderId = document.getElementById("updateStatusBtn").getAttribute("data-id");
    const newStatus = document.getElementById("modalOrderStatus").value;

    fetch(`${contextPath}/Admin/UpdateOrderStatusServlet`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: JSON.stringify({ orderId, status: newStatus })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                const orders = JSON.parse(localStorage.getItem("orders")) || [];
                const orderIndex = orders.findIndex(order => String(order.id) === orderId);
                if (orderIndex !== -1) {
                    if (newStatus === "Delivered") {
                        orders.splice(orderIndex, 1);
                    } else {
                        orders[orderIndex].status = normalizeStatus(newStatus);
                    }
                    localStorage.setItem("orders", JSON.stringify(orders));
                }
                closeModal(document.getElementById("orderModal"));
                filterAndDisplayOrders();
            } else {
                alert(`Failed to update order status: ${data.message || "Unknown error"}`);
            }
        })
        .catch(error => {
            console.error("Error updating order status:", error);
            alert("Failed to update order status. Please try again later.");
        });
}