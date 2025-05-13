import URL_Mapper from "./URL_Mapper.js";
import displayProduct from "../Common/BookPopup.js";
import { addToCart, addToWishList } from "./UICommonFunctions.js";

// Create a book card element
const createBookCard = (book) => {
  const mainImage = book.images?.find((image) => image.isMain);
  const imageURL = mainImage?.url || URL_Mapper.ASSETS.FALLBACK_BOOK_IMAGE;

  // Create elements
  const bookElement = document.createElement("div");
  bookElement.classList.add("book-card");

  const imgElement = document.createElement("img");
  imgElement.src = imageURL;
  imgElement.alt = book.title || "Book image";
  imgElement.className = "product-image";

  const titleElement = document.createElement("h3");
  titleElement.textContent = book.title || "Untitled Book";

  const bookCardDetails = document.createElement("div");
  bookCardDetails.classList.add("book-card-details");

  // Helper function to create detail paragraphs
  const createDetailElement = (text, className = "") => {
    const el = document.createElement("p");
    if (className) el.classList.add(className);
    el.textContent = text || "Not specified";
    return el;
  };

  // Create overview for hover effect
  const overviewElement = document.createElement("div");
  overviewElement.classList.add("overview");
  overviewElement.textContent = book.overview || "No overview available";

  // Append author and genre details
  bookCardDetails.append(
    createDetailElement(book.author, "author"),
    createDetailElement(book.genre, "genre")
  );

  // Handle price display with discount if applicable
  if (book.discountedPercentage && book.discountedPercentage > 0) {
    // Calculate discounted price
    const originalPrice = book.price || 0;
    const discountAmount = (originalPrice * book.discountedPercentage) / 100;
    const discountedPrice = originalPrice - discountAmount;

    // Create price container with original and discounted prices
    const priceContainer = document.createElement("div");
    priceContainer.classList.add("price-container");

    // Original price (strikethrough)
    const originalPriceElement = document.createElement("span");
    originalPriceElement.classList.add("original-price");
    originalPriceElement.textContent = `${originalPrice} EGP`;

    // Discounted price
    const discountedPriceElement = document.createElement("span");
    discountedPriceElement.classList.add("discounted-price");
    discountedPriceElement.textContent = `${discountedPrice.toFixed(2)} EGP`;

    // Append prices to container
    priceContainer.appendChild(originalPriceElement);
    priceContainer.appendChild(discountedPriceElement);

    // Append price container to book details
    bookCardDetails.appendChild(priceContainer);

    // Add discount badge
    const discountBadge = document.createElement("div");
    discountBadge.classList.add("discount-badge");
    discountBadge.textContent = `-${book.discountedPercentage}%`;
    bookElement.appendChild(discountBadge);
  } else {
    // No discount, just show regular price
    bookCardDetails.appendChild(createDetailElement(`${book.price || 0} EGP`, "price"));
  }

  // Assemble card
  bookElement.append(imgElement, titleElement, bookCardDetails, overviewElement);
  appendStockValue(bookElement, book);

  const wishListButton = createWishListButton(book);
  bookElement.appendChild(wishListButton);

  // Add cart controls
  appendAddToCartControls(bookElement, book);

  function updateCard(updatedBook) {
    // Update main image if changed
    const newMainImage = updatedBook.images?.find((img) => img.isMain);
    const newImageURL = newMainImage?.url || URL_Mapper.ASSETS.FALLBACK_BOOK_IMAGE;
    if (newImageURL !== imgElement.src) {
      imgElement.src = newImageURL;
    }

    // Update text content
    titleElement.textContent = updatedBook.title || book.title || "Untitled Book";

    // Update overview
    overviewElement.textContent = updatedBook.overview || "No overview available";

    // Update details
    const authorElement = bookCardDetails.querySelector(".author");
    const genreElement = bookCardDetails.querySelector(".genre");

    if (authorElement) authorElement.textContent = updatedBook.author || book.author || "Not specified";
    if (genreElement) genreElement.textContent = updatedBook.genre || book.genre || "Not specified";

    // Update price display
    const existingPriceContainer = bookCardDetails.querySelector(".price-container");
    const existingPrice = bookCardDetails.querySelector(".price");

    // Remove existing price elements
    if (existingPriceContainer) existingPriceContainer.remove();
    if (existingPrice) existingPrice.remove();

    // Re-add price with potential discount
    if (updatedBook.discountedPercentage && updatedBook.discountedPercentage > 0) {
      // Calculate discounted price
      const originalPrice = updatedBook.price || 0;
      const discountAmount = (originalPrice * updatedBook.discountedPercentage) / 100;
      const discountedPrice = originalPrice - discountAmount;

      // Create price container with original and discounted prices
      const priceContainer = document.createElement("div");
      priceContainer.classList.add("price-container");

      // Original price (strikethrough)
      const originalPriceElement = document.createElement("span");
      originalPriceElement.classList.add("original-price");
      originalPriceElement.textContent = `${originalPrice} EGP`;

      // Discounted price
      const discountedPriceElement = document.createElement("span");
      discountedPriceElement.classList.add("discounted-price");
      discountedPriceElement.textContent = `${discountedPrice.toFixed(2)} EGP`;

      // Append prices to container
      priceContainer.appendChild(originalPriceElement);
      priceContainer.appendChild(discountedPriceElement);

      // Append price container to book details
      bookCardDetails.appendChild(priceContainer);
    } else {
      // No discount, just show regular price
      bookCardDetails.appendChild(createDetailElement(`${updatedBook.price || 0} EGP`, "price"));
    }

    // Update discount badge
    const existingBadge = bookElement.querySelector(".discount-badge");
    if (updatedBook.discountedPercentage && updatedBook.discountedPercentage > 0) {
      if (existingBadge) {
        existingBadge.textContent = `-${updatedBook.discountedPercentage}%`;
      } else {
        const discountBadge = document.createElement("div");
        discountBadge.classList.add("discount-badge");
        discountBadge.textContent = `-${updatedBook.discountedPercentage}%`;
        bookElement.appendChild(discountBadge);
      }
    } else if (existingBadge) {
      existingBadge.remove();
    }

    updateStockValue(bookElement, updatedBook);
    updateAddToCartControls(bookElement, updatedBook);
  }

  // Make the entire card clickable
  bookElement.addEventListener("click", (e) => {
    // Don't trigger if clicking on buttons or inputs
    if (
      e.target.tagName === "BUTTON" ||
      e.target.tagName === "INPUT" ||
      e.target.closest(".add-to-wishlist") ||
      e.target.closest(".quantity-selector") ||
      e.target.closest(".add-to-cart")
    ) {
      return;
    }

    // Debugging log to confirm click
    console.log("Book card clicked, opening popup for:", book.title);
    e.stopPropagation();
    e.preventDefault(); // Prevent any default behavior
    displayProduct(book, updateCard);
  });

  // Also make the image clickable separately
  imgElement.addEventListener("click", (e) => {
    console.log("Book image clicked, opening popup for:", book.title);
    e.stopPropagation();
    e.preventDefault();
    displayProduct(book, updateCard);
  });

  return bookElement;
};

// Create a wishlist button for a book
const createWishListButton = (book) => {
  // Create wish list button
  const wishListButton = document.createElement("button");
  wishListButton.classList.add("add-to-wishlist");
  wishListButton.title = "Add to Wish List";
  wishListButton.setAttribute("aria-label", "Add to Wish List");

  const wishListImg = document.createElement("img");
  wishListImg.src = URL_Mapper.ICONS.WISH_LIST;
  wishListImg.alt = "Add to Wish List";
  wishListButton.appendChild(wishListImg);

  // Add wish list button event listener
  wishListButton.addEventListener("click", async (e) => {
    e.stopPropagation();
    await addToWishList(book);
  });

  return wishListButton;
};

// Helper method
const createStockElement = (book) => {
  const paragraph = document.createElement("p");
  paragraph.className = "stock-status";

  if (!book.isAvailable || !book.stock) {
    paragraph.classList.add("out-stock");
    paragraph.textContent = "Book Not Available";
    return paragraph;
  }

  // Determine stock status and CSS class
  let stockText;
  let stockClass;
  if (book.stock > 20) {
    stockText = "In Stock";
    stockClass = "in-stock";
  } else if (book.stock === 0) {
    stockText = "Out of Stock";
    stockClass = "out-stock";
  } else if (book.stock === 1) {
    stockText = "Last Piece";
    stockClass = "last-piece";
  } else {
    stockText = `${book.stock} Pieces Left`;
    stockClass = "low-stock";
  }

  paragraph.classList.add(stockClass);
  paragraph.textContent = stockText;
  return paragraph;
};

const appendStockValue = (bookElement, book) => {
  const stockElement = createStockElement(book);
  bookElement.appendChild(stockElement);
};

const updateStockValue = (bookElement, book) => {
  // Find existing stock status element
  const oldStockElement = bookElement.querySelector(".stock-status");

  // Create new stock status element
  const newStockElement = createStockElement(book);

  // Replace or append the element
  if (oldStockElement) {
    oldStockElement.replaceWith(newStockElement);
  } else {
    bookElement.appendChild(newStockElement);
  }
};

// Creates and returns all the DOM elements needed for the cart controls
const createAddToCartControls = (book) => {
  // Create the quantity selector container
  const quantitySelector = document.createElement("div");
  quantitySelector.className = "quantity-selector";

  // Create the decrease button
  const decreaseButton = document.createElement("button");
  decreaseButton.className = "decrease";
  decreaseButton.textContent = "-";
  decreaseButton.setAttribute("aria-label", "Decrease quantity");

  // Create the input element
  const quantityInput = document.createElement("input");
  quantityInput.type = "number";
  quantityInput.value = "1";
  quantityInput.min = "1";
  quantityInput.max = Math.min(book.stock || 0, 20).toString();
  quantityInput.readOnly = true;
  quantityInput.setAttribute("aria-label", "Quantity");

  // Create the increase button
  const increaseButton = document.createElement("button");
  increaseButton.className = "increase";
  increaseButton.textContent = "+";
  increaseButton.setAttribute("aria-label", "Increase quantity");

  // Append the buttons and input to the quantity selector
  quantitySelector.appendChild(decreaseButton);
  quantitySelector.appendChild(quantityInput);
  quantitySelector.appendChild(increaseButton);

  // Create the add to cart button
  const addToCartButton = document.createElement("button");
  addToCartButton.className = "add-to-cart";
  addToCartButton.textContent = "Add to Cart";

  return {
    quantitySelector,
    decreaseButton,
    quantityInput,
    increaseButton,
    addToCartButton,
  };
};

// Adds the cart controls to the book element
const appendAddToCartControls = (bookElement, book) => {
  if (book.stock <= 0 || !book.isAvailable) {
    return;
  }

  // Create all the elements
  const elements = createAddToCartControls(book);

  // Add elements to the DOM
  bookElement.appendChild(elements.quantitySelector);
  bookElement.appendChild(elements.addToCartButton);

  // Set initial quantity and update button states
  updateAddToCartControls(bookElement, book);

  // Set up event listeners
  setupCartControlEventListeners(bookElement, book);
};

// Updates the cart controls based on the current state
const updateAddToCartControls = (bookElement, book) => {
  const quantitySelector = bookElement.querySelector(".quantity-selector");
  if (!quantitySelector) return;

  const decreaseButton = quantitySelector.querySelector(".decrease");
  const quantityInput = quantitySelector.querySelector("input");
  const increaseButton = quantitySelector.querySelector(".increase");

  // Get current quantity
  const quantity = Number.parseInt(quantityInput.value);

  // Update controls state
  decreaseButton.disabled = quantity === 1;
  increaseButton.disabled = quantity >= Math.min(book.stock || 0, 20);

  // Update max value in case stock changed
  quantityInput.max = Math.min(book.stock || 0, 20).toString();
};

// Sets up event listeners for the cart controls
const setupCartControlEventListeners = (bookElement, book) => {
  const quantitySelector = bookElement.querySelector(".quantity-selector");
  const decreaseButton = quantitySelector.querySelector(".decrease");
  const quantityInput = quantitySelector.querySelector("input");
  const increaseButton = quantitySelector.querySelector(".increase");
  const addToCartButton = bookElement.querySelector(".add-to-cart");

  decreaseButton.addEventListener("click", (e) => {
    e.stopPropagation();
    const value = Number.parseInt(quantityInput.value);
    if (value > 1) {
      quantityInput.value = `${value - 1}`;
      updateAddToCartControls(bookElement, book);
    }
  });

  increaseButton.addEventListener("click", (e) => {
    e.stopPropagation();
    const value = Number.parseInt(quantityInput.value);
    if (value < Math.min(book.stock || 0, 20)) {
      quantityInput.value = `${value + 1}`;
      updateAddToCartControls(bookElement, book);
    }
  });

  addToCartButton.addEventListener("click", (e) => {
    e.stopPropagation();
    let quantity;
    try {
      quantity = quantityInput.value;
      quantity = Number.parseInt(quantity);
    } catch (_) {
      console.error("Could not parse quantity to int in add to cart event handler!");
    }

    // Consume the promise to remove the warning
    addToCart(book, quantity)
      .then((_) => {})
      .catch((_) => {});
  });
};

export {
  createBookCard,
  createWishListButton,
  appendStockValue,
  updateStockValue,
  appendAddToCartControls,
  updateAddToCartControls,
};