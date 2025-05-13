import checkForErrorMessageParameter from "./Common/checkForError.js"
import URL_Mapper from "./Utils/URL_Mapper.js"
import MessagePopup from "./Common/MessagePopup.js"
import WishListManager from "./Managers/WishListManager.js"
import UserAuthTracker from "./Common/UserAuthTracker.js"
import Book from "./Models/Book.js"
import displayProduct from "./Common/BookPopup.js"
import LoadingOverlay from "./Common/LoadingOverlay.js"

document.addEventListener("DOMContentLoaded", async () => {
  checkForErrorMessageParameter()

  const backToProfileButton = document.getElementById("backToProfile")
  if (!backToProfileButton) {
    console.error("Could not locate back to profile button!")
  } else {
    backToProfileButton.addEventListener("click", () => {
      window.location.href = URL_Mapper.PROFILE
    })
  }

  const wishListItemsContainer = document.getElementById("wishListItems")
  if (!wishListItemsContainer) {
    console.error("Could not locate the wish list items container.")
    return
  }
  wishListItemsContainer.innerHTML = '<div class="loading-data">Loading your wish list...</div>'

  const userObject = UserAuthTracker.userObject
  if (!userObject) {
    UserAuthTracker.handleUserInvalidState()
    return
  }

  function handleEmptyWishList() {
    wishListItemsContainer.innerHTML = ""

    const emptyMsg = document.createElement("div")
    emptyMsg.className = "no-data-found"
    emptyMsg.textContent = "Your wish list is empty"
    wishListItemsContainer.appendChild(emptyMsg)
  }

  const loadingOverlay = new LoadingOverlay()
  loadingOverlay.createAndDisplay("Loading Wish List...")

  const response = await WishListManager.getAllWishListBooks(userObject.userID)
  loadingOverlay.remove()

  if (!response) {
    MessagePopup.show("Unknown error, cannot display wish list!", true)
    return
  }

  if (!response.success) {
    MessagePopup.show(response.data, true)
    return
  }

  renderWishListItems(response.data)

  function renderWishListItems(wishListItems) {
    wishListItemsContainer.innerHTML = ""

    let parsedWishListItems
    if (typeof wishListItems !== "object") {
      try {
        parsedWishListItems = JSON.parse(wishListItems)
      } catch (_) {
        console.log("Could not parse wish list items.")
        return
      }
    } else {
      parsedWishListItems = wishListItems
    }

    if (parsedWishListItems.length === 0) {
      handleEmptyWishList()
      return
    }

    // Since we now receive book objects directly, we can render them immediately
    parsedWishListItems.forEach((book) => {
      const bookDiv = document.createElement("div")
      bookDiv.classList.add("wish-list-item")
      wishListItemsContainer.appendChild(bookDiv)
      fillBookElement(bookDiv, book)
    })
  }

  function fillBookElement(bookDiv, book) {
    let parsedBook
    try {
      parsedBook = Book.fromJSON(book)
    } catch (_) {
      console.log("Could not parse a book!")
      parsedBook = null
    }

    if (parsedBook === null) {
      bookDiv.classList.add("no-data-found")
      bookDiv.textContent = "Could Not Find This Book."
      return
    }

    const mainImage = parsedBook.images?.find((image) => image.isMain)
    let imagePath
    if (mainImage) {
      imagePath = mainImage.url
    } else {
      imagePath = URL_Mapper.ASSETS.FALLBACK_BOOK_IMAGE
    }

    // Create the image element
    const img = document.createElement("img")
    img.src = imagePath
    img.alt = parsedBook.title
    img.className = "book-image"
    bookDiv.appendChild(img)

    // Create the book details container
    const bookDetails = document.createElement("div")
    bookDetails.className = "book-details"

    // Create and append the title
    const title = document.createElement("h3")
    title.textContent = parsedBook.title
    bookDetails.appendChild(title)

    // Create and append the overview
    const overview = document.createElement("p")
    overview.textContent = parsedBook.overview
    overview.classList.add("overview")
    bookDetails.appendChild(overview)

    // Create and append the author
    const author = document.createElement("p")
    author.innerHTML = `<strong>Author:</strong> ${parsedBook.author}`
    bookDetails.appendChild(author)

    // Create and append the ISBN
    const isbn = document.createElement("p")
    isbn.innerHTML = `<strong>ISBN:</strong> ${parsedBook.isbn}`
    bookDetails.appendChild(isbn)

    // Add discount badge if applicable
    if (parsedBook.discountedPercentage && parsedBook.discountedPercentage > 0) {
      const discountBadge = document.createElement("div")
      discountBadge.className = "discount-badge"
      discountBadge.textContent = `-${parsedBook.discountedPercentage}%`
      bookDiv.appendChild(discountBadge)
    }

    // Append the book details to the main container
    bookDiv.appendChild(bookDetails)

    // Create the remove button (same as cart's trash can)
    const trashCanCoveredPath = URL_Mapper.ICONS.TRASH_CAN_COVERED
    const trashCanUncoveredPath = URL_Mapper.ICONS.TRASH_CAN_UNCOVERED

    const removeItemButton = document.createElement("button")
    removeItemButton.classList.add("remove-item-button")
    removeItemButton.setAttribute("aria-label", "Remove from wishlist")

    const removeItemButtonDefaultImg = document.createElement("img")
    removeItemButtonDefaultImg.classList.add("default-img")
    removeItemButtonDefaultImg.src = `${trashCanCoveredPath}`
    removeItemButtonDefaultImg.alt = "Remove"
    removeItemButton.appendChild(removeItemButtonDefaultImg)

    const removeItemButtonHoveredImg = document.createElement("img")
    removeItemButtonHoveredImg.classList.add("hover-img")
    removeItemButtonHoveredImg.src = `${trashCanUncoveredPath}`
    removeItemButtonHoveredImg.alt = "Remove"
    removeItemButton.appendChild(removeItemButtonHoveredImg)

    removeItemButton.addEventListener("click", async () => {
      const response = await WishListManager.removeFromWishList(userObject.userID, parsedBook.bookID)
      if (!response) {
        MessagePopup.show("Unknown error, cannot remove the item", true)
        return
      }

      if (!response.success) {
        MessagePopup.show(response.data, true)
        return
      }

      MessagePopup.show(response.data)
      bookDiv.remove()

      if (wishListItemsContainer.children.length === 0) {
        handleEmptyWishList()
      }
    })

    bookDiv.appendChild(removeItemButton)

    function updateBookInfoOnDisplay(updatedAndParsedBook) {
      // Update the book image if it changed
      const mainImage = updatedAndParsedBook.images?.find((image) => image.isMain)
      if (mainImage && img.src !== mainImage.url) {
        img.src = mainImage.url || URL_Mapper.ASSETS.FALLBACK_BOOK_IMAGE
      }

      // Update the title if changed
      if (title.textContent !== updatedAndParsedBook.title) {
        title.textContent = updatedAndParsedBook.title
      }

      // Update the overview if changed
      if (overview.textContent !== updatedAndParsedBook.overview) {
        overview.textContent = updatedAndParsedBook.overview
      }

      // Update the author if changed
      const newAuthorHTML = `<strong>Author:</strong> ${updatedAndParsedBook.author}`
      if (author.innerHTML !== newAuthorHTML) {
        author.innerHTML = newAuthorHTML
      }

      // Update the ISBN if changed
      const newIsbnHTML = `<strong>ISBN:</strong> ${updatedAndParsedBook.isbn}`
      if (isbn.innerHTML !== newIsbnHTML) {
        isbn.innerHTML = newIsbnHTML
      }

      // Update discount badge if needed
      const existingBadge = bookDiv.querySelector(".discount-badge")
      if (updatedAndParsedBook.discountedPercentage && updatedAndParsedBook.discountedPercentage > 0) {
        if (existingBadge) {
          existingBadge.textContent = `-${updatedAndParsedBook.discountedPercentage}%`
        } else {
          const discountBadge = document.createElement("div")
          discountBadge.className = "discount-badge"
          discountBadge.textContent = `-${updatedAndParsedBook.discountedPercentage}%`
          bookDiv.appendChild(discountBadge)
        }
      } else if (existingBadge) {
        existingBadge.remove()
      }

      // Update the local parsedBook reference
      parsedBook = updatedAndParsedBook
    }

    img.addEventListener("click", () => {
      displayProduct(parsedBook, updateBookInfoOnDisplay, false, false)
    })
  }
})
