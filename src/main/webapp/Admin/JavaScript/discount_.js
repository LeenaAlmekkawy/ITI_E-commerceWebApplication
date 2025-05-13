document.addEventListener("DOMContentLoaded", () => {
  // Base URL for AJAX calls
  const baseUrl = `${contextPath}/Admin`;

  // Initial data loading
  loadBanners();
  loadDiscountedBooks();

  // Tab switching
  const tabBtns = document.querySelectorAll(".tab-btn");
  tabBtns.forEach((btn) => {
      btn.addEventListener("click", () => {
          document.querySelectorAll(".tab-btn").forEach((b) => b.classList.remove("active"));
          document.querySelectorAll(".tab-content").forEach((c) => c.classList.remove("active"));
          btn.classList.add("active");
          const tabId = btn.getAttribute("data-tab") + "-tab";
          document.getElementById(tabId).classList.add("active");
      });
  });

  // Add banner button
  document.getElementById("addBannerBtn").addEventListener("click", () => {
      document.getElementById("bannerModalTitle").textContent = "Add New Banner";
      document.getElementById("bannerForm").reset();
      document.getElementById("bannerId").value = "";
      document.getElementById("bannerImagePreview").style.backgroundImage = "";
      openModal("bannerModal");
  });

  // Add discount button
  document.getElementById("addDiscountBtn").addEventListener("click", () => {
      document.getElementById("discountModalTitle").textContent = "Add New Discount";
      document.getElementById("discountForm").reset();
      document.getElementById("discountId").value = "";
      document.getElementById("categorySelector").classList.add("hidden");
      document.getElementById("booksSelector").classList.add("hidden");
      document.getElementById("valueType").textContent = "%";
      openModal("discountModal");
  });

  // Remove all discounts button
  document.getElementById("removeAllDiscountsBtn").addEventListener("click", () => {
      openModal("removeAllDiscountsModal");
  });

  // Confirm remove all discounts
  document.getElementById("confirmRemoveAllBtn").addEventListener("click", () => {
      removeAllDiscounts();
  });

  // Cancel remove all discounts
  document.getElementById("cancelRemoveAllBtn").addEventListener("click", () => {
      closeModal(document.getElementById("removeAllDiscountsModal"));
  });

  // Discount type change
  document.getElementById("discountType").addEventListener("change", function () {
      document.getElementById("valueType").textContent = this.value === "percentage" ? "%" : "$";
  });

  // Apply to change
  document.getElementById("applyTo").addEventListener("change", function () {
      const categorySelector = document.getElementById("categorySelector");
      const booksSelector = document.getElementById("booksSelector");
      categorySelector.classList.add("hidden");
      booksSelector.classList.add("hidden");
      if (this.value === "category") {
          categorySelector.classList.remove("hidden");
      } else if (this.value === "books") {
          booksSelector.classList.remove("hidden");
          loadAvailableBooks();
      }
  });

  // Banner form submission
  document.getElementById("bannerForm").addEventListener("submit", (e) => {
      e.preventDefault();
      saveBanner();
  });

  // Discount form submission
  document.getElementById("discountForm").addEventListener("submit", (e) => {
      e.preventDefault();
      applyDiscount();
  });

  // Banner image preview
  document.getElementById("bannerImage").addEventListener("change", (e) => {
      const file = e.target.files[0];
      if (file) {
          const reader = new FileReader();
          reader.onload = (e) => {
              document.getElementById("bannerImagePreview").style.backgroundImage = `url(${e.target.result})`;
          };
          reader.readAsDataURL(file);
      }
  });

  // Books selection buttons
  document.getElementById("addSelectedBooks").addEventListener("click", () => {
      moveSelectedBooks("available", "selected");
  });

  document.getElementById("removeSelectedBooks").addEventListener("click", () => {
      moveSelectedBooks("selected", "available");
  });

  // Cancel buttons
  document.getElementById("cancelBannerBtn").addEventListener("click", () => {
      closeModal(document.getElementById("bannerModal"));
  });

  document.getElementById("cancelDiscountBtn").addEventListener("click", () => {
      closeModal(document.getElementById("discountModal"));
  });

  document.getElementById("cancelDeleteBtn").addEventListener("click", () => {
      closeModal(document.getElementById("deleteModal"));
  });

  // Confirm delete button
  document.getElementById("confirmDeleteBtn").addEventListener("click", () => {
      const type = document.getElementById("confirmDeleteBtn").getAttribute("data-type");
      const id = parseInt(document.getElementById("confirmDeleteBtn").getAttribute("data-id"));
      if (type === "banner") {
          deleteBanner(id);
      } else if (type === "book-discount") {
          removeBookDiscount(id);
      }
  });

  // Make functions available globally
  window.editBanner = editBanner;
  window.confirmDeleteBanner = confirmDeleteBanner;
  window.confirmRemoveBookDiscount = confirmRemoveBookDiscount;

  // AJAX Functions
  function getBanners() {
      return fetch(`${baseUrl}/BannersServlet`, {
          method: "GET",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .catch(error => {
              console.error("Error fetching banners:", error);
              alert("Failed to load banners. Please try again.");
              throw error;
          });
  }

  function getBanner(id) {
      return fetch(`${baseUrl}/BannersServlet?id=${id}`, {
          method: "GET",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .catch(error => {
              console.error(`Error fetching banner ${id}:`, error);
              alert("Failed to load banner details. Please try again.");
              throw error;
          });
  }

  function saveBanner() {
      const bannerId = document.getElementById("bannerId").value;
      const formData = new FormData(document.getElementById("bannerForm"));
      const url = bannerId ? `${baseUrl}/BannersServlet?action=update&id=${bannerId}` : `${baseUrl}/BannersServlet`;

      fetch(url, {
          method: "POST",
          body: formData
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .then(() => {
              closeModal(document.getElementById("bannerModal"));
              loadBanners();
          })
          .catch(error => {
              console.error("Error saving banner:", error);
              alert("Failed to save banner. Please try again.");
          });
  }

  function deleteBanner(id) {
      fetch(`${baseUrl}/BannersServlet?action=delete&id=${id}`, {
          method: "POST",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .then(() => {
              closeModal(document.getElementById("deleteModal"));
              loadBanners();
          })
          .catch(error => {
              console.error(`Error deleting banner ${id}:`, error);
              alert("Failed to delete banner. Please try again.");
          });
  }

  function getBooks() {
      return fetch(`${baseUrl}/BooksServlet`, {
          method: "GET",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .catch(error => {
              console.error("Error fetching books:", error);
              alert("Failed to load books. Please try again.");
              throw error;
          });
  }

  function getDiscountedBooks() {
      return fetch(`${baseUrl}/BooksServlet?discounted=true`, {
          method: "GET",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .catch(error => {
              console.error("Error fetching discounted books:", error);
              alert("Failed to load discounted books. Please try again.");
              throw error;
          });
  }

  function applyDiscount() {
      const discountType = document.getElementById("discountType").value;
      const discountValue = parseFloat(document.getElementById("discountValue").value);
      const applyTo = document.getElementById("applyTo").value;
      let categoryId = null;
      let bookIds = [];

      if (applyTo === "category") {
          categoryId = document.getElementById("category").value;
      } else if (applyTo === "books") {
          const selectedBooks = document.getElementById("selectedBooksContainer").querySelectorAll(".book-item");
          bookIds = Array.from(selectedBooks).map(book => parseInt(book.getAttribute("data-id")));
      }

      const discountData = { discountType, discountValue, applyTo, categoryId, bookIds };

      fetch(`${baseUrl}/DiscountsServlet`, {
          method: "POST",
          headers: {
              "Content-Type": "application/json",
              "Accept": "application/json"
          },
          body: JSON.stringify(discountData)
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .then(() => {
              closeModal(document.getElementById("discountModal"));
              loadDiscountedBooks();
          })
          .catch(error => {
              console.error("Error applying discount:", error);
              alert("Failed to apply discount. Please try again.");
          });
  }

  function removeBookDiscount(id) {
      fetch(`${baseUrl}/BooksServlet?action=removeDiscount&id=${id}`, {
          method: "POST",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .then(() => {
              closeModal(document.getElementById("deleteModal"));
              loadDiscountedBooks();
          })
          .catch(error => {
              console.error(`Error removing discount for book ${id}:`, error);
              alert("Failed to remove discount. Please try again.");
          });
  }

  function removeAllDiscounts() {
      fetch(`${baseUrl}/DiscountsServlet?action=removeAll`, {
          method: "POST",
          headers: { "Accept": "application/json" }
      })
          .then(response => {
              if (!response.ok) throw new Error(`HTTP error! Status: ${response.status}`);
              return response.json();
          })
          .then(() => {
              closeModal(document.getElementById("removeAllDiscountsModal"));
              loadDiscountedBooks();
              alert("All discounts have been removed.");
          })
          .catch(error => {
              console.error("Error removing all discounts:", error);
              alert("Failed to remove all discounts. Please try again.");
          });
  }

  // Load functions
  function loadBanners() {
      getBanners().then(displayBanners);
  }

  function loadAvailableBooks() {
      getBooks().then(books => {
          const availableBooksContainer = document.getElementById("availableBooksContainer");
          const selectedBooksContainer = document.getElementById("selectedBooksContainer");
          availableBooksContainer.innerHTML = "";
          selectedBooksContainer.innerHTML = "";

          books.forEach(book => {
              availableBooksContainer.innerHTML += `
                  <div class="book-item" data-id="${book.id}">
                      <img src="${book.bookImage}" alt="${book.title}">
                      <span>${book.title}</span>
                  </div>
              `;
          });

          const bookItems = availableBooksContainer.querySelectorAll(".book-item");
          bookItems.forEach(item => {
              item.addEventListener("click", function () {
                  this.classList.toggle("selected");
              });
          });
      });
  }

  function loadDiscountedBooks() {
      getDiscountedBooks().then(books => {
          const discountedBooksTable = document.getElementById("discountedBooksTable");
          discountedBooksTable.innerHTML = "";

          if (books.length === 0) {
              discountedBooksTable.innerHTML = `
                  <tr>
                      <td colspan="6" class="text-center">No books found.</td>
                  </tr>
              `;
              return;
          }

          books.forEach(book => {
              const finalPrice = book.discountType === "percentage"
                  ? book.price * (1 - book.discountValue / 100)
                  : book.price - book.discountValue;
              const discountDisplay = book.discountValue > 0
                  ? (book.discountType === "percentage" ? `${book.discountValue}%` : formatCurrency(book.discountValue))
                  : "0";

              discountedBooksTable.innerHTML += `
                  <tr>
                      <td><img src="${book.bookImage}" alt="${book.title}" class="book-cover"></td>
                      <td>${book.title}</td>
                      <td>${formatCurrency(book.price)}</td>
                      <td>${discountDisplay}</td>
                      <td>${formatCurrency(finalPrice)}</td>
                      <td>
                          <div class="action-buttons">
                              ${book.discountValue > 0 ? `<button class="btn btn-danger" onclick="confirmRemoveBookDiscount(${book.id})">Remove Discount</button>` : ""}
                          </div>
                      </td>
                  </tr>
              `;
          });
      });
  }

  // Display functions
  function displayBanners(banners) {
      const bannersTable = document.getElementById("bannersTable");
      bannersTable.innerHTML = "";

      if (banners.length === 0) {
          bannersTable.innerHTML = `
              <tr>
                  <td colspan="4" class="text-center">No banners found.</td>
              </tr>
          `;
          return;
      }

      banners.forEach(banner => {
          bannersTable.innerHTML += `
              <tr>
                  <td>${banner.id}</td>
                  <td><img src="${banner.image}" alt="${banner.title}" class="banner-thumbnail"></td>
                  <td>${banner.title}</td>
                  <td>
                      <div class="action-buttons">
                          <button class="btn" onclick="editBanner(${banner.id})">Edit</button>
                          <button class="btn btn-danger" onclick="confirmDeleteBanner(${banner.id})">Delete</button>
                      </div>
                  </td>
              </tr>
          `;
      });
  }

  // Action functions
  function editBanner(id) {
      getBanner(id).then(banner => {
          document.getElementById("bannerModalTitle").textContent = "Edit Banner";
          document.getElementById("bannerId").value = banner.id;
          document.getElementById("bannerTitle").value = banner.title;
          document.getElementById("bannerText").value = banner.text || "";
          document.getElementById("bannerImagePreview").style.backgroundImage = `url(${banner.image})`;
          openModal("bannerModal");
      });
  }

  function confirmDeleteBanner(id) {
      document.getElementById("deleteMessage").textContent =
          "Are you sure you want to delete this banner? This action cannot be undone.";
      document.getElementById("confirmDeleteBtn").setAttribute("data-type", "banner");
      document.getElementById("confirmDeleteBtn").setAttribute("data-id", id);
      openModal("deleteModal");
  }

  function confirmRemoveBookDiscount(id) {
      document.getElementById("deleteMessage").textContent = "Are you sure you want to remove the discount from this book?";
      document.getElementById("confirmDeleteBtn").setAttribute("data-type", "book-discount");
      document.getElementById("confirmDeleteBtn").setAttribute("data-id", id);
      openModal("deleteModal");
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

  function moveSelectedBooks(fromType, toType) {
      const fromContainer = document.getElementById(`${fromType}BooksContainer`);
      const toContainer = document.getElementById(`${toType}BooksContainer`);
      const selectedBooks = fromContainer.querySelectorAll(".book-item.selected");

      selectedBooks.forEach(book => {
          book.classList.remove("selected");
          toContainer.appendChild(book);
          book.addEventListener("click", function () {
              this.classList.toggle("selected");
          });
      });
  }

  function formatCurrency(number) {
      return new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(number);
  }
});