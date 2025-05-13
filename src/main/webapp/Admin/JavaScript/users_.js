var usersString = [];
document.addEventListener("DOMContentLoaded", () => {
    // Search functionality
    const searchBtn = document.getElementById("searchBtn");
    searchBtn.addEventListener("click", () => {
        const searchTerm = document.getElementById("userSearch").value.toLowerCase();
        searchUsers(searchTerm);
    });

    // Search on enter key
    const userSearch = document.getElementById("userSearch");
    userSearch.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
            const searchTerm = e.target.value.toLowerCase();
            searchUsers(searchTerm);
        }
    });

    // Cancel button
    const cancelBtn = document.getElementById("cancelBtn");
    cancelBtn.addEventListener("click", () => {
        closeModal(document.getElementById("userModal"));
    });

    // Cancel delete button
    const cancelDeleteBtn = document.getElementById("cancelDeleteBtn");
    cancelDeleteBtn.addEventListener("click", () => {
        closeModal(document.getElementById("deleteModal"));
    });

    // Confirm delete button
    const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
    confirmDeleteBtn.addEventListener("click", () => {
        deleteUser();
    });

    // Make edit and delete buttons work globally
    window.confirmDelete = confirmDelete;
});

// Load users from WebSocket data
function loadUsers() {
    let users = JSON.parse(usersString) || [];
    displayUsers(users);
}

// Display users in the table
/*
function displayUsers(users) {
    const usersTable = document.getElementById("usersTable");
    usersTable.innerHTML = "";

    users.forEach((user) => {
        usersTable.innerHTML += `
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.orders}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn" onclick="viewUser(${user.id})">View</button>
                        <button class="btn btn-danger" onclick="confirmDelete(${user.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `;
    });
}
    */

function displayUsers(users) {
    const usersTable = document.getElementById("usersTable");
    usersTable.innerHTML = "";

    totalPages = Math.ceil(users.length / itemsPerPage);
    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const paginatedUsers = users.slice(startIndex, endIndex);

    if (paginatedUsers.length === 0) {
        usersTable.innerHTML = `<tr><td colspan="5" class="text-center">No users found.</td></tr>`;
        return;
    }

    paginatedUsers.forEach((user) => {
        usersTable.innerHTML += `
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.orders}</td>
                <td>
                    <div class="action-buttons">
                        <button class="btn" onclick="viewUser(${user.id})">View</button>
                        <button class="btn btn-danger" onclick="confirmDelete(${user.id})">Delete</button>
                    </div>
                </td>
            </tr>
        `;
    });

    updateUserPagination();
}


// Search users
function searchUsers(searchTerm) {
    const users = JSON.parse(usersString) || [];

    if (!searchTerm) {
        displayUsers(users);
        return;
    }

    const filteredUsers = users.filter(
        (user) =>
            user.username.toLowerCase().includes(searchTerm) ||
            user.email.toLowerCase().includes(searchTerm)
    );

    displayUsers(filteredUsers);
}

// View user
function viewUser(id) {
    const users = JSON.parse(usersString) || [];
    const user = users.find((user) => user.id === id);

    if (user) {
        populateUserForm(user);
    }
}

// Populate user form with data
function populateUserForm(user) {
    document.getElementById("modalTitle").textContent = "View User";
    document.getElementById("userId").value = user.id;
    document.getElementById("id").value = user.id;
    document.getElementById("username").value = user.username || "N/A";
    document.getElementById("email").value = user.email;
    document.getElementById("phoneNumber").value = user.phoneNumber || "N/A";
    document.getElementById("birthDate").value = formatDate(user.birthDate) || "N/A";
    document.getElementById("address").value = user.address || "N/A";
    document.getElementById("balance").value = user.balance || "0.00";
    document.getElementById("interests").value = user.interests ? user.interests.join(", ") : "N/A";

    openModal("userModal");
}

// Confirm delete
function confirmDelete(id) {
    document.getElementById("confirmDeleteBtn").setAttribute("data-id", id);
    openModal("deleteModal");
}

// Delete user
function deleteUser() {
    const id = Number.parseInt(document.getElementById("confirmDeleteBtn").getAttribute("data-id"));
    deleteWebSocket.send(id);
    closeModal(document.getElementById("deleteModal"));
}

// Helper functions
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = "block";
    document.body.style.overflow = "hidden";
}

function closeModal(modal) {
    modal.style.display = "none";
    document.body.style.overflow = "auto";
}

function formatDate(dateString) {
    if (!dateString) return "N/A";
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
}

// WebSocket handling
var viewWebSocket;
var deleteWebSocket;
function connect() {
    viewWebSocket = new WebSocket("ws://" + window.location.host + "/ITI-Jets-EcommApplication/Admin/ViewUsers");
    viewWebSocket.onerror = (error) => {
        console.error('WebSocket Error:', error);
    };
    viewWebSocket.onopen = onOpen;
    viewWebSocket.onmessage = viewOnMessage;

    deleteWebSocket = new WebSocket("ws://" + window.location.host + "/ITI-Jets-EcommApplication/Admin/DeleteUser");
    deleteWebSocket.onerror = (error) => {
        console.error('WebSocket Error:', error);
    };
    deleteWebSocket.onopen = onOpen;
    deleteWebSocket.onmessage = deleteOnMessage;
}

function onOpen() {
    console.log("Connection established in users.js");
}

function viewOnMessage(event) {
    console.log(event.data);
    usersString = event.data;
    loadUsers();
}

function deleteOnMessage(event) {
    console.log(event.data);
    usersString = event.data;
    loadUsers();
}

//--------------------------------------
let currentPage = 1;
let totalPages = 1;
const itemsPerPage = 7;

document.getElementById("prevUserPage").addEventListener("click", () => {
    if (currentPage > 1) {
        currentPage--;
        const users = JSON.parse(usersString) || [];
        displayUsers(users);
    }
});

document.getElementById("nextUserPage").addEventListener("click", () => {
    if (currentPage < totalPages) {
        currentPage++;
        const users = JSON.parse(usersString) || [];
        displayUsers(users);
    }
});

function updateUserPagination() {
    document.getElementById("userPageIndicator").textContent = `Page ${currentPage} of ${totalPages}`;
    document.getElementById("prevUserPage").disabled = currentPage === 1;
    document.getElementById("nextUserPage").disabled = currentPage === totalPages;
}

