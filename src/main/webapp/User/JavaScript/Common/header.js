import URL_Mapper from "../Utils/URL_Mapper.js"
import UserAuthTracker from "./UserAuthTracker.js"
import MessagePopup from "../Common/MessagePopup.js"
import ProfileManager from "../Managers/ProfileManager.js"
import DataValidator from "../Utils/DataValidator.js"
import User from "../Models/User.js"

document.addEventListener("DOMContentLoaded", () => {
  const header = document.getElementById("siteHeader")
  if (!header) {
    console.log("Could not load the header component.")
    return
  }

  // Paths for redirect logic
  const welcomePagePathName = URL_Mapper.WELCOME
  const productsPagePathName = URL_Mapper.PRODUCTS

  // Function to check if the user should be redirected
  function checkRedirect() {
    const isAuthenticated = UserAuthTracker.isAuthenticated
    if (isAuthenticated) {
      return
    }

    const pathName = window.location.pathname
    if (pathName !== welcomePagePathName && pathName !== productsPagePathName) {
      window.location.href = URL_Mapper.WELCOME + `?errorMessage=Not Logged In`
    }
  }
  checkRedirect()

  // Render header based on auth status
  function renderHeader() {
    const isAuthenticated = UserAuthTracker.isAuthenticated

    // Updated header structure with hamburger menu
    header.innerHTML = `
      <div class="header-left">
        <div class="logo-container">
          <img src="${URL_Mapper.ASSETS.LOGO}" alt="Book Alley Logo" onclick="window.location.href='${URL_Mapper.WELCOME}'">
        </div>
        <h1 class="site-title" onclick="window.location.href='${URL_Mapper.WELCOME}'">Book Alley</h1>
      </div>

      <button class="hamburger-menu" id="hamburgerMenu">
        <span></span>
        <span></span>
        <span></span>
      </button>

      <div class="header-right" id="headerRight">
        <nav class="navbar">
          <a href="${URL_Mapper.WELCOME}"><i class="fas fa-home"></i> <span class="nav-text">Home</span></a>
          <a href="${URL_Mapper.PRODUCTS}"><i class="fas fa-book"></i> <span class="nav-text">Books</span></a>
          <a href="${URL_Mapper.WISH_LIST}" class="wishlist-link"><i class="fas fa-heart"></i> <span class="nav-text">Wishlist</span></a>
          <a href="${URL_Mapper.CART}" class="cart-link"><i class="fas fa-shopping-cart"></i> <span class="nav-text">Cart</span></a>
          <a href="${URL_Mapper.ABOUT}"><i class="fas fa-info-circle"></i> <span class="nav-text">About</span></a>
        </nav>

        <div class="auth-actions">
          ${
            isAuthenticated
              ? `
            <a href="${URL_Mapper.PROFILE}" class="profile-link">
              <i class="fas fa-user"></i>
              <span class="nav-text">Profile</span>
            </a>
            <button id="logoutButton" class="auth-button logout-button"><i class="fas fa-sign-out-alt"></i> <span class="nav-text">Logout</span></button>
          `
              : `
            <button id="loginButton" class="auth-button login-button"><i class="fas fa-sign-in-alt"></i> <span class="nav-text">Login</span></button>
            <button id="registerButton" class="auth-button register-button"><i class="fas fa-user-plus"></i> <span class="nav-text">Register</span></button>
          `
          }
        </div>
      </div>
    `

    // Force icon display after rendering
    const forceIconDisplay = () => {
      const icons = header.querySelectorAll("i.fas")
      icons.forEach((icon) => {
        icon.style.display = "inline-block"
      })
    }

    // Run immediately and after a short delay to ensure icons are displayed
    forceIconDisplay()
    setTimeout(forceIconDisplay, 100)

    // Hamburger menu toggle functionality
    const hamburgerMenu = document.getElementById("hamburgerMenu")
    const headerRight = document.getElementById("headerRight")
    hamburgerMenu.addEventListener("click", () => {
      headerRight.classList.toggle("active")
      hamburgerMenu.classList.toggle("active")
    })

    // Close menu when clicking outside
    document.addEventListener("click", (e) => {
      if (!header.contains(e.target) && headerRight.classList.contains("active")) {
        headerRight.classList.remove("active")
        hamburgerMenu.classList.remove("active")
      }
    })

    // Add event listeners for buttons
    if (isAuthenticated) {
      const logoutButton = header.querySelector("#logoutButton")
      if (logoutButton) {
        logoutButton.addEventListener("click", handleLogout)
      }
    } else {
      const loginButton = header.querySelector("#loginButton")
      const registerButton = header.querySelector("#registerButton")

      if (loginButton) {
        loginButton.addEventListener("click", () => {
          document.getElementById("loginModal").classList.remove("hidden")
          document.body.style.overflow = "hidden" // Prevent scrolling when modal is open
          headerRight.classList.remove("active") // Close menu on login click
          hamburgerMenu.classList.remove("active")
        })
      }

      if (registerButton) {
        registerButton.addEventListener("click", () => {
          document.getElementById("registerModal").classList.remove("hidden")
          document.body.style.overflow = "hidden" // Prevent scrolling when modal is open
          headerRight.classList.remove("active") // Close menu on register click
          hamburgerMenu.classList.remove("active")
        })
      }
    }
  }

  // Create login modal
  const loginModal = document.createElement("div")
  loginModal.className = "auth-modal-overlay hidden"
  loginModal.id = "loginModal"
  loginModal.innerHTML = `
    <div class="auth-modal">
      <button class="auth-close-button" aria-label="Close">×</button>
      <div class="auth-modal-content">
        <h2>Login</h2>
        <form id="loginForm">
          <div class="form-group">
            <label for="loginEmail">Email</label>
            <input type="email" id="loginEmail" name="email" required placeholder="Enter your email">
          </div>
          <div class="form-group">
            <label for="loginPassword">Password</label>
            <input type="password" id="loginPassword" name="password" required placeholder="Enter your password">
          </div>
          <div class="auth-modal-buttons">
            <button type="submit" class="submit-button">Login</button>
            <button type="button" class="cancel-button">Cancel</button>
          </div>
        </form>
        <div class="form-footer">
          <p>Don't have an account? <a href="#" id="switchToRegister">Register here</a></p>
        </div>
      </div>
    </div>
  `

  // Create register modal
  const registerModal = document.createElement("div")
  registerModal.className = "auth-modal-overlay hidden"
  registerModal.id = "registerModal"
  registerModal.innerHTML = `
    <div class="auth-modal">
      <button class="auth-close-button" aria-label="Close">×</button>
      <div class="auth-modal-content">
        <h2>Create Account</h2>
        <form id="registerForm">
          <div class="form-group">
            <label for="registerName">Full Name</label>
            <input type="text" id="registerName" name="name" required placeholder="Enter your full name">
          </div>
          <div class="form-group">
            <label for="registerEmail">Email</label>
            <input type="email" id="registerEmail" name="email" required placeholder="Enter your email">
          </div>
          <div class="form-group">
            <label for="registerPhone">Phone Number</label>
            <input type="text" id="registerPhone" name="phoneNumber" required placeholder="Enter your phone number">
          </div>
          <div class="form-group">
            <label for="registerAddress">Address</label>
            <input type="text" id="registerAddress" name="address" required placeholder="Enter your address">
          </div>
          <div class="form-group">
            <label for="registerBirthDate">Birth Date</label>
            <input type="date" id="registerBirthDate" name="birthDate" required>
          </div>
          <div class="form-group">
            <label for="registerPassword">Password</label>
            <input type="password" id="registerPassword" name="password" required placeholder="Create a password">
          </div>
          <div class="form-group">
            <label for="registerConfirmPassword">Confirm Password</label>
            <input type="password" id="registerConfirmPassword" name="confirmPassword" required placeholder="Confirm your password">
          </div>
          <div class="auth-modal-buttons">
            <button type="submit" class="submit-button">Register</button>
            <button type="button" class="cancel-button">Cancel</button>
          </div>
        </form>
        <div class="form-footer">
          <p>Already have an account? <a href="#" id="switchToLogin">Login here</a></p>
        </div>
      </div>
    </div>
  `

  // Append modals to body
  document.body.appendChild(loginModal)
  document.body.appendChild(registerModal)

  // Close modal handlers
  document.querySelectorAll(".auth-close-button, .cancel-button").forEach((button) => {
    button.addEventListener("click", (e) => {
      e.preventDefault()
      const modal = button.closest(".auth-modal-overlay")
      if (modal) {
        modal.classList.add("hidden")
        document.body.style.overflow = "" // Restore scrolling
      }
    })
  })

  // Close modal when clicking outside
  document.querySelectorAll(".auth-modal-overlay").forEach((overlay) => {
    overlay.addEventListener("click", (e) => {
      if (e.target === overlay) {
        overlay.classList.add("hidden")
        document.body.style.overflow = "" // Restore scrolling
      }
    })
  })

  // Switch between login and register
  document.getElementById("switchToRegister").addEventListener("click", (e) => {
    e.preventDefault()
    document.getElementById("loginModal").classList.add("hidden")
    document.getElementById("registerModal").classList.remove("hidden")
  })

  document.getElementById("switchToLogin").addEventListener("click", (e) => {
    e.preventDefault()
    document.getElementById("registerModal").classList.add("hidden")
    document.getElementById("loginModal").classList.remove("hidden")
  })

  // Form submission handlers
  async function handleLogin(e) {
    e.preventDefault()
    const email = document.getElementById("loginEmail").value
    const password = document.getElementById("loginPassword").value

    if (!DataValidator.isEmailValid(email)) {
      MessagePopup.show("Invalid email!", true)
      return
    }

    if (!DataValidator.isPasswordValid(password)) {
      MessagePopup.show("Invalid password!", true)
      return
    }

    try {
      const response = await ProfileManager.login(email, password)
      if (!response || !response.success) {
        MessagePopup.show(response?.data || "Login failed", true)
        return
      }

      let parsedUser
      try {
        parsedUser = User.fromJSON(response.data)
      } catch (e) {
        console.error("Could not parse user object:", e)
        MessagePopup.show("Invalid user data", true)
        return
      }

      UserAuthTracker.userObject = parsedUser
      renderHeader()
      MessagePopup.show("Login successful!", false)
      document.getElementById("loginModal").classList.add("hidden")
      document.body.style.overflow = "" // Restore scrolling
    } catch (error) {
      console.error("Login error:", error)
      MessagePopup.show("Login failed", true)
    }
  }

  async function handleRegister(e) {
    e.preventDefault()
    const userName = document.getElementById("registerName").value
    const email = document.getElementById("registerEmail").value
    const phoneNumber = document.getElementById("registerPhone").value
    const address = document.getElementById("registerAddress").value
    const birthDate = document.getElementById("registerBirthDate").value
    const password = document.getElementById("registerPassword").value
    const confirmPassword = document.getElementById("registerConfirmPassword").value

    if (!DataValidator.isEmailValid(email)) {
      MessagePopup.show("Invalid email!", true)
      return
    }

    if (!DataValidator.isPasswordValid(password)) {
      MessagePopup.show("Invalid password!", true)
      return
    }

    if (password !== confirmPassword) {
      MessagePopup.show(`Passwords don't match!`, true)
      return
    }

    if (!DataValidator.isUserNameValid(userName)) {
      MessagePopup.show("Invalid user name!", true)
      return
    }

    if (!DataValidator.isPhoneValid(phoneNumber)) {
      MessagePopup.show("Invalid phone number!", true)
      return
    }

    if (!DataValidator.isAddressValid(address)) {
      MessagePopup.show("Invalid address!", true)
      return
    }

    if (!DataValidator.isBirthDateValid(birthDate)) {
      MessagePopup.show("Invalid birth date!", true)
      return
    }

    const userData = {
      email,
      password,
      userName,
      phoneNumber,
      address,
      birthDate,
    }

    try {
      const response = await ProfileManager.register(userData)
      if (!response || !response.success) {
        MessagePopup.show(response?.data || "Registration failed", true)
        return
      }

      let parsedUser
      try {
        parsedUser = User.fromJSON(response.data)
      } catch (e) {
        console.error("Could not parse user object:", e)
        MessagePopup.show("Invalid user data", true)
        return
      }

      UserAuthTracker.userObject = parsedUser
      renderHeader()
      MessagePopup.show("Registration successful!", false)
      document.getElementById("registerModal").classList.add("hidden")
      document.body.style.overflow = "" // Restore scrolling
    } catch (error) {
      console.error("Registration error:", error)
      MessagePopup.show("Registration failed", true)
    }
  }

  function handleLogout() {
    UserAuthTracker.userObject = null
    window.location.href = URL_Mapper.WELCOME
  }

  // Attach form submission handlers
  document.getElementById("loginForm").addEventListener("submit", handleLogin)
  document.getElementById("registerForm").addEventListener("submit", handleRegister)

  // Initial render
  renderHeader()

  // Listen for auth changes
  UserAuthTracker.onAuthChange = renderHeader
})