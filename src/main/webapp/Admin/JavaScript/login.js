document.addEventListener("DOMContentLoaded", () => {
  // Check if user is already logged in

  /*
  if (sessionStorage.getItem("adminLoggedIn") === "true") {
    window.location.href = "dashboard.jsp"
  }
    */

  const loginForm = document.getElementById("loginForm")
  const loginError = document.getElementById("loginError")

  /*loginForm.addEventListener("submit", (e) => {
    e.preventDefault()

    const username = document.getElementById("username").value
    const password = document.getElementById("password").value




    // Simple validation
    if (!username || !password) {
      loginError.textContent = "Please enter both username and password"
      return
    }

    // For demo purposes, hardcoded credentials
    // In a real application, this would be a server request
    if ((username === "leena" || username === "ali") && password === "admin123") {
      // Set login status in localStorage

      sessionStorage.setItem("adminLoggedIn", "true")
      sessionStorage.setItem("adminUsername", username)

      // Redirect to dashboard
      window.location.href = "dashboard.jsp"
    } else {
      loginError.textContent = "Invalid username or password"
    }
  })*/
})

