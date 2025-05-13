'use strict';

import checkForErrorMessageParameter from "./Common/checkForError.js";
import URL_Mapper from './Utils/URL_Mapper.js';
import displayProduct from "./Common/BookPopup.js";
import { addOrderDateTimeAddress } from "./Utils/UICommonFunctions.js";
import OrdersManager from "./Managers/OrdersManager.js";
import UserAuthTracker from "./Common/UserAuthTracker.js";

import Order from "./Models/Order.js";
import BooksManager from "./Managers/BooksManager.js";
import Book from "./Models/Book.js";
import MessagePopup from "./Common/MessagePopup.js";
import LoadingOverlay from "./Common/LoadingOverlay.js";

const navigateWithError = function(url, error) {
    window.location.href = url + `?errorMessage=${encodeURIComponent(error)}`;
}

document.addEventListener("DOMContentLoaded", async function () {
    checkForErrorMessageParameter();

    const userObject = UserAuthTracker.userObject;
    if (!userObject) {
        UserAuthTracker.handleUserInvalidState();
        return;
    }

    const urlParams = new URLSearchParams(window.location.search);
    let orderID = urlParams.get("orderID");
    if (!orderID) {
        navigateWithError(URL_Mapper.ORDERS, 'No order ID specified!');
        return;
    }

    try {
        orderID = JSON.parse(orderID);
    } catch (error) {
        navigateWithError(URL_Mapper.ORDERS, 'Could not read the order ID!');
        return;
    }

    const backToOrdersButton = document.getElementById('backToOrdersButton');
    if (!backToOrdersButton) {
        console.error('Could not locate the back to orders button.');
    } else {
        backToOrdersButton.addEventListener('click', () => {
            window.location.href = URL_Mapper.ORDERS;
        });
    }

    const orderTitle = document.getElementById('orderTitle');
    if (!orderTitle) {
        console.error('Could not locate the order title component.');
    }

    const orderDetails = document.getElementById('orderDetails');
    if (!orderDetails) {
        console.error('Could not locate the order details component.');
    }

    const orderItemsContainer = document.getElementById('orderItems');
    if (!orderItemsContainer) {
        console.error('Could not locate the order items container.');
        return;
    }

    const subtotalComponent = document.getElementById('subtotal');
    if (!subtotalComponent) {
        console.error('Could not locate subtotal component.');
    }

    const shippingFeeComponent = document.getElementById('shippingFee');
    if (!shippingFeeComponent) {
        console.error('Could not locate shipping fee component.');
    }

    const totalAmountComponent = document.getElementById('totalAmount');
    if (!totalAmountComponent) {
        console.error('Could not locate total amount component.');
    }

    const orderLoadingOverlay = new LoadingOverlay();
    orderLoadingOverlay.createAndDisplay('Loading Order Details...');

    const response = await OrdersManager.getOrderDetails(userObject.userID, orderID);
    orderLoadingOverlay.remove();

    if (!response) {
        navigateWithError(URL_Mapper.ORDERS, 'Unknown error, could not load the order details!');
        return;
    }

    if (!response.success) {
        navigateWithError(URL_Mapper.ORDERS, response.data);
        return;
    }

    renderOrder(response.data);

    function renderOrder(order) {
        let parsedOrder;
        try {
            parsedOrder = Order.fromJSON(order);
        } catch (_) {
            navigateWithError(URL_Mapper.ORDERS, 'Could not find an order with this order ID!')
            return;
        }

        if (orderTitle) {
            orderTitle.textContent = `Order #${parsedOrder.orderID}`;
        }

        if (orderDetails) {
            addOrderDetails(parsedOrder);
        }

        renderOrderItems(parsedOrder);
    }

    function addOrderDetails(order) {
        addOrderDateTimeAddress(orderDetails, order);

        const numberOfItemsParagraph = document.createElement('p');
        numberOfItemsParagraph.innerHTML = `<strong>Number of Items:</strong> ${order.orderItems.length}`;
        orderDetails.appendChild(numberOfItemsParagraph);

        const paymentMethod = document.createElement('p');
        paymentMethod.innerHTML = `<strong>Payment Method:</strong> ${order.paymentMethod}`;
        orderDetails.appendChild(paymentMethod);

        const orderStatus = document.createElement('p');
        orderStatus.innerHTML = `<strong>Order Status:</strong> ${order.status}`;
        orderDetails.appendChild(orderStatus);
    }

    function renderOrderItems(order) {
        orderItemsContainer.innerHTML = '';

        const booksLoadingOverlay = new LoadingOverlay();
        booksLoadingOverlay.createAndDisplay('Loading Books...');

        const promisesList = [];
        let subtotal = 0; // Initialize subtotal to 0

        order.orderItems.forEach(orderItem => {
            const loadOrderItemBook = async function(orderItem) {
                const bookElement = document.createElement('div');
                bookElement.classList.add('order-item');
                orderItemsContainer.appendChild(bookElement);

                const responsePromise = BooksManager.getBookDetails(orderItem.bookID);
                promisesList.push(responsePromise);

                const response = await responsePromise;
                if (!response) {
                    console.error('Unknown error, could not load the book with #ID: ' + orderItem.bookID);
                    return;
                }

                if (!response.success) {
                    MessagePopup.show(response.data, true);
                }

                loadBookInfoElement(bookElement, orderItem, response.data);

                // Accumulate subtotal for each order item
                const itemTotal = orderItem.priceAtPurchase * orderItem.quantity;
                subtotal += itemTotal;
                updatePriceSummarySection(subtotal);
            }

            loadOrderItemBook(orderItem);

            Promise.all(promisesList)
                .then(_ => booksLoadingOverlay.remove())
                .catch(_ => booksLoadingOverlay.remove());
        });

        if (shippingFeeComponent) {
            shippingFeeComponent.textContent = order.shippingFee.toFixed(2);
            shippingFeeComponent.parentElement.innerHTML = `<strong>Shipping Fee:</strong> <span class="currency-group"><span>${order.shippingFee.toFixed(2)}</span> EGP</span>`;
        }
    }

    function loadBookInfoElement(bookElement, orderItem, book) {
        let parsedBook;
        try {
            parsedBook = Book.fromJSON(book);
        } catch (_) {
            console.error('Could not parse a book.');
            bookElement.classList.add('no-data-found');
            bookElement.textContent = 'Could Not Find This Book.';
            return;
        }

        const mainImage = parsedBook.images?.find(image => image.isMain);
        let imagePath;
        if (mainImage) {
            imagePath = mainImage.url;
        } else {
            imagePath = URL_Mapper.ASSETS.FALLBACK_BOOK_IMAGE;
        }

        let subtotalPrice = orderItem.priceAtPurchase * orderItem.quantity;

        // Create the image element.
        const img = document.createElement('img');
        img.src = imagePath;
        img.alt = parsedBook.title;
        img.className = 'book-image';
        bookElement.appendChild(img);

        // Create the book details container.
        const bookDetails = document.createElement('div');
        bookDetails.className = 'book-details';

        // Create and append the title.
        const title = document.createElement('h2');
        title.textContent = parsedBook.title;
        bookDetails.appendChild(title);

        // Create and append the overview.
        const overview = document.createElement('p');
        overview.textContent = parsedBook.overview;
        overview.classList.add('overview');
        bookDetails.appendChild(overview);

        // Create and append the author.
        const author = document.createElement('p');
        author.innerHTML = `<strong>Author:</strong> ${parsedBook.author}`;
        bookDetails.appendChild(author);

        // Create and append the ISBN.
        const isbn = document.createElement('p');
        isbn.innerHTML = `<strong>ISBN:</strong> ${parsedBook.isbn}`;
        bookDetails.appendChild(isbn);

        // Append the book details to the main container.
        bookElement.appendChild(bookDetails);

        // Create the price section container.
        const priceSection = document.createElement('div');
        priceSection.className = 'price-section';

        const pricePerPieceParagraph = document.createElement('p');
        pricePerPieceParagraph.innerHTML = `<strong>Price:</strong> <span class="currency-group">${orderItem.priceAtPurchase.toFixed(2)} EGP</span>`;
        priceSection.appendChild(pricePerPieceParagraph);

        const quantityParagraph = document.createElement('p');
        quantityParagraph.innerHTML = `<strong>Quantity:</strong> ${orderItem.quantity}`;
        priceSection.appendChild(quantityParagraph);

        const totalItemPriceParagraph = document.createElement('p');
        totalItemPriceParagraph.innerHTML = `<strong>Total Price:</strong> <span class="currency-group">${subtotalPrice.toFixed(2)} EGP</span>`;
        priceSection.appendChild(totalItemPriceParagraph);

        bookElement.appendChild(priceSection);

        img.addEventListener('click', () => {
            displayProduct(parsedBook, (updatedBook) => {
                // This is the callback that will be called when the book data is updated
                const mainImage = updatedBook.images?.find(image => image.isMain);
                if (mainImage) {
                    img.src = mainImage.url;
                }

                title.textContent = updatedBook.title;
                overview.textContent = updatedBook.overview;
                author.innerHTML = `<strong>Author:</strong> ${updatedBook.author}`;
                isbn.innerHTML = `<strong>ISBN:</strong> ${updatedBook.isbn}`;

                parsedBook = updatedBook;
            });
        });
    }

    function updatePriceSummarySection(subtotal) {
        if (subtotalComponent) {
            subtotalComponent.textContent = subtotal.toFixed(2);
            subtotalComponent.parentElement.innerHTML = `<strong>Subtotal:</strong> <span class="currency-group"><span>${subtotal.toFixed(2)}</span> EGP</span>`;
        }

        const shippingFee = parseFloat(shippingFeeComponent.textContent) || 0;
        const total = subtotal + shippingFee;

        if (totalAmountComponent) {
            totalAmountComponent.textContent = total.toFixed(2);
            totalAmountComponent.parentElement.innerHTML = `<strong>Total Amount:</strong> <span class="currency-group"><span>${total.toFixed(2)}</span> EGP</span>`;
        }
    }
});