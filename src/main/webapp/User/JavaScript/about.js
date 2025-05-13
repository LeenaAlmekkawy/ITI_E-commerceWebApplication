document.addEventListener("DOMContentLoaded", () => {
  // Add any specific functionality for the about page here

  // Example: Animate the about cards on scroll
  const observerOptions = {
    root: null,
    rootMargin: "0px",
    threshold: 0.1,
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add("fade-in")
        observer.unobserve(entry.target)
      }
    })
  }, observerOptions)

  document.querySelectorAll(".about-card").forEach((card) => {
    observer.observe(card)
  })
})
