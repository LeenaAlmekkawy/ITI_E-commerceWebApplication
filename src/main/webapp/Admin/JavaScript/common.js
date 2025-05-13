document.addEventListener("DOMContentLoaded", () => {





  // Sidebar toggle
  const sidebarToggle = document.getElementById("sidebarToggle")
  const sidebar = document.querySelector(".sidebar")

  if (sidebarToggle) {
    sidebarToggle.addEventListener("click", () => {
      sidebar.classList.toggle("active")
    })
  }

  // Logout functionality
  const logoutBtn = document.getElementById("logoutBtn")

  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault()



      // Clear localStorage
      sessionStorage.removeItem("adminLoggedIn")
      sessionStorage.removeItem("adminUsername")

      // Redirect to login page
      window.location.href = "admin_login.jsp"
    })
  }

  // Close modals when clicking outside
  const modals = document.querySelectorAll(".modal")

  modals.forEach((modal) => {
    modal.addEventListener("click", (e) => {
      if (e.target === modal) {
        closeModal(modal)
      }
    })

    const closeBtn = modal.querySelector(".close")
    if (closeBtn) {
      closeBtn.addEventListener("click", () => {
        closeModal(modal)
      })
    }
  })

})

// Helper functions
function openModal(modalId) {
  const modal = document.getElementById(modalId)
  modal.style.display = "block"
  document.body.style.overflow = "hidden"
}

function closeModal(modal) {
  modal.style.display = "none"
  document.body.style.overflow = "auto"
}

function formatCurrency(amount) {
  return "$" + Number.parseFloat(amount).toFixed(2)
}

function formatDate(dateString) {
  const options = { year: "numeric", month: "short", day: "numeric" }
  return new Date(dateString).toLocaleDateString(undefined, options)
}

