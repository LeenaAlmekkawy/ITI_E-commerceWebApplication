document.addEventListener("DOMContentLoaded", () => {
    // Initialize charts
    initCharts();

    // Set up SSE for real-time updates
    setupStatsSSE();
});

function formatDate(dateString) {
    const date = new Date(dateString);
    const options = { year: "numeric", month: "long", day: "numeric" };
    return date.toLocaleDateString(undefined, options);
}

function formatCurrency(amount) {
    return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(amount || 0);
}

function setupStatsSSE() {
    const eventSource = new EventSource(`${contextPath}/Admin/DashboardStatsServlet`);

    eventSource.addEventListener("stats", (event) => {
        try {
            const data = JSON.parse(event.data);
            // Update stats
            document.getElementById("totalBooks").textContent = data.totalBooks || 0;
            document.getElementById("totalSales").textContent = formatCurrency(data.totalSales);
            document.getElementById("totalUsers").textContent = data.totalUsers || 0;
            document.getElementById("pendingOrders").textContent = data.pendingOrders || 0;

            // Update charts
            updateSalesChart(data.monthlySales || []);
            updateBooksChart(data.topBooks || []);
        } catch (e) {
            console.error("Error parsing SSE stats:", e);
        }
    });

    eventSource.addEventListener("error", (event) => {
        console.warn("SSE error occurred. Attempting to reconnect...");
        eventSource.close();
        setTimeout(setupStatsSSE, 5000); // Reconnect after 5 seconds
    });

    eventSource.onerror = () => {
        console.warn("SSE connection lost. Retrying...");
        eventSource.close();
        setTimeout(setupStatsSSE, 5000); // Reconnect after 5 seconds
    };
}

function initCharts() {
    // Sales chart placeholder
    const salesChartContainer = document.getElementById("salesChart").parentNode;
    document.getElementById("salesChart").remove();
    const salesPlaceholder = document.createElement("canvas");
    salesPlaceholder.id = "salesChart";
    salesChartContainer.appendChild(salesPlaceholder);
    window.salesChart = null; // Initialize as null

    // Books chart placeholder
    const booksChartContainer = document.getElementById("booksChart").parentNode;
    document.getElementById("booksChart").remove();
    const booksPlaceholder = document.createElement("canvas");
    booksPlaceholder.id = "booksChart";
    booksChartContainer.appendChild(booksPlaceholder);
    window.booksChart = null; // Initialize as null
}

function updateSalesChart(monthlySales) {
    const labels = monthlySales.map(item => `${item.month}/${item.year % 100}`);
    const data = monthlySales.map(item => item.total);

    const ctx = document.getElementById("salesChart").getContext("2d");
    if (window.salesChart) {
        try {
            window.salesChart.destroy();
        } catch (e) {
            console.warn("Failed to destroy sales chart:", e);
        }
    }
    window.salesChart = new Chart(ctx, {
        type: "line",
        data: {
            labels: labels,
            datasets: [{
                label: "Monthly Sales",
                data: data,
                backgroundColor: "rgba(0, 123, 255, 0.2)",
                borderColor: "#007bff",
                borderWidth: 2,
                fill: true
            }]
        },
        options: {
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: "Sales ($)"
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: "Month/Year"
                    }
                }
            }
        }
    });
}

function updateBooksChart(topBooks) {
    const labels = topBooks.map(book => book.title);
    const data = topBooks.map(book => book.soldCount);

    const ctx = document.getElementById("booksChart").getContext("2d");
    if (window.booksChart) {
        try {
            window.booksChart.destroy();
        } catch (e) {
            console.warn("Failed to destroy books chart:", e);
        }
    }
    window.booksChart = new Chart(ctx, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                label: "Book Sales",
                data: data,
                backgroundColor: ["#007bff", "#28a745", "#dc3545", "#ffc107", "#6c757d"],
                borderColor: "#fff",
                borderWidth: 1
            }]
        },
        options: {
            plugins: {
                legend: {
                    display: true
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: "Units Sold"
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: "Book Title"
                    },
                    ticks: {
                        maxRotation: 45,
                        minRotation: 45
                    }
                }
            }
        }
    });
}