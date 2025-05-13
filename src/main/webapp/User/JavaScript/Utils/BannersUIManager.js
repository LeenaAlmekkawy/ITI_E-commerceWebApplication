import PromotionsManager from "../Managers/PromotionsManager.js"

export default async function loadBanners() {
  const response = await PromotionsManager.getActivePromotions()
  if (response === null || !response.success) {
    return
  }

  const bannerMessages = document.getElementById("bannerMessages")
  if (!bannerMessages) {
    console.error("Banner messages container not found!")
    return
  }

  const bannerTexts = response.data.map((promo) => promo.message)
  if (bannerTexts.length === 0) {
    // If no promotions, add a default message
    bannerTexts.push("Welcome to Book Alley! Explore our collection of books.")
  }

  // Clear existing content
  bannerMessages.innerHTML = ""

  // Create banner text elements
  bannerTexts.forEach((text, index) => {
    const bannerText = document.createElement("div")
    bannerText.className = "banner-text"

    // Add icon based on content
    if (text.toLowerCase().includes("discount") || text.toLowerCase().includes("sale")) {
      bannerText.innerHTML = `<i class="fas fa-tag"></i> ${text}`
    } else if (text.toLowerCase().includes("new") || text.toLowerCase().includes("arrival")) {
      bannerText.innerHTML = `<i class="fas fa-star"></i> ${text}`
    } else if (text.toLowerCase().includes("free") || text.toLowerCase().includes("shipping")) {
      bannerText.innerHTML = `<i class="fas fa-truck"></i> ${text}`
    } else {
      bannerText.innerHTML = `<i class="fas fa-book"></i> ${text}`
    }

    if (index === 0) {
      bannerText.classList.add("active")
    }
    bannerMessages.appendChild(bannerText)
  })

  // Set up navigation
  const prevBtn = document.getElementById("prevBtn")
  const nextBtn = document.getElementById("nextBtn")

  if (!prevBtn || !nextBtn) {
    console.error("Banner navigation buttons not found!")
    return
  }

  let currentIndex = 0
  const bannerElements = document.querySelectorAll(".banner-text")

  function showBanner(index) {
    bannerElements.forEach((el) => el.classList.remove("active"))
    bannerElements[index].classList.add("active")
  }

  prevBtn.addEventListener("click", () => {
    currentIndex = (currentIndex - 1 + bannerElements.length) % bannerElements.length
    showBanner(currentIndex)
  })

  nextBtn.addEventListener("click", () => {
    currentIndex = (currentIndex + 1) % bannerElements.length
    showBanner(currentIndex)
  })

  // Auto-rotate banners
  setInterval(() => {
    currentIndex = (currentIndex + 1) % bannerElements.length
    showBanner(currentIndex)
  }, 5000)
}
