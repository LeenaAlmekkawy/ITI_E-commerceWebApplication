document.addEventListener("DOMContentLoaded", () => {
  const footer = document.getElementById("siteFooter")
  if (!footer) return

  const currentYear = new Date().getFullYear()

  footer.innerHTML = `
    <p>
      <span>© ${currentYear} Book Alley. All Rights Reserved.</span>
      <span class="pipe">|</span>
      <span><a href="About/about.html" id="aboutUSLink">About Us</a></span>
      <span class="pipe">|</span>
      <span>Privacy Policy</span>
      <span class="pipe">|</span>
      <span>Terms of Service</span>
    </p>
    <div class="social-links">
      <a href="#" aria-label="Facebook"><i class="fab fa-facebook-f"></i></a>
      <a href="#" aria-label="Twitter"><i class="fab fa-twitter"></i></a>
      <a href="#" aria-label="Instagram"><i class="fab fa-instagram"></i></a>
      <a href="#" aria-label="Pinterest"><i class="fab fa-pinterest-p"></i></a>
    </div>
  `
})
