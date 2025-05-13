// Helper functions
import UserAuthTracker from "../Common/UserAuthTracker.js";
import MessagePopup from "../Common/MessagePopup.js";
import CartManager from "../Managers/CartManager.js";
import WishListManager from "../Managers/WishListManager.js";
import Book from "../Models/Book.js";

const formatDate = function(isoDateString) {
    const date = new Date(isoDateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

const formatTime = function(isoDateString) {
    const date = new Date(isoDateString);
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}

const addOrderDateTimeAddress = function(orderDetailsElement, order) {
    const dateParagraph = document.createElement('p');
    dateParagraph.innerHTML = `<strong>Date:</strong> ${formatDate(order.date)}`;
    orderDetailsElement.appendChild(dateParagraph);

    const timeParagraph = document.createElement('p');
    timeParagraph.innerHTML = `<strong>Time:</strong> ${formatTime(order.date)}`;
    orderDetailsElement.appendChild(timeParagraph);

    const addressParagraph = document.createElement('p');
    addressParagraph.innerHTML = `<strong>Address:</strong> ${order.address}`;
    orderDetailsElement.appendChild(addressParagraph);
}

const populateYearSelect = function(expiryYearSelect) {
    const currentYear = new Date().getFullYear() - 2000;
    for (let i = 0; i <= 10; i++) {
        const option = document.createElement('option');
        option.value = (currentYear + i).toString().padStart(2, '0');
        option.textContent = option.value;
        expiryYearSelect.appendChild(option);
    }
}

const parseBooksFromData = function(booksArray) {
    return booksArray.reduce((validBooks, book) => {
        try {
            const parsedBook = Book.fromJSON(book);
            validBooks.push(parsedBook);
        } catch (e) {
            console.log('Could not parse a book:', e);
        }
        return validBooks;
    }, []);
}

const addToCart = async function(book, quantity) {
    const isAuthenticated = UserAuthTracker.isAuthenticated;
    if (!isAuthenticated) {
        MessagePopup.show('You must login to add book to cart!');
        return Promise.reject('User not logged in!');
    }

    const response = await CartManager.addItem(UserAuthTracker.userObject.userID, book.bookID, quantity);
    if (response === null) {
        return Promise.reject('Response was null!');
    }

    if (!response.success) {
        MessagePopup.show(response.data, true);
        return Promise.reject(response.error);
    }

    MessagePopup.show(`Added ${quantity} of "${book.title}" to cart.`);
    return Promise.resolve('Add to cart was successful.');
}

const addToWishList = async function(book) {
    const userObject = UserAuthTracker.userObject;
    if (!userObject) {
        MessagePopup.show('You must login to add book to wish list!');
        return;
    }

    const response = await WishListManager.addWishListItem(userObject.userID, book.bookID);
    if (!response) {
        MessagePopup.show('Unknown error, cannot add to wish list!', true);
        return;
    }

    if (!response.success) {
        MessagePopup.show(response.data, true);
        return;
    }

    MessagePopup.show(`Added "${book.title}" to your wish list.`);
}

export {
    formatDate,
    formatTime,
    addOrderDateTimeAddress,
    populateYearSelect,
    parseBooksFromData,
    addToCart,
    addToWishList
};
