'use strict';

import checkForErrorMessageParameter from "./Common/checkForError.js";
import URL_Mapper from './Utils/URL_Mapper.js';
import CartManager from "./Managers/CartManager.js";
import OrdersManager from "./Managers/OrdersManager.js";
import UserAuthTracker from "./Common/UserAuthTracker.js";
import MessagePopup from "./Common/MessagePopup.js";
import DataValidator from "./Utils/DataValidator.js";
import { populateYearSelect } from "./Utils/UICommonFunctions.js";
import LoadingOverlay from "./Common/LoadingOverlay.js";

document.addEventListener("DOMContentLoaded", async function() {
    checkForErrorMessageParameter();

    // Initialize DOM elements
    // Navigation buttons
    const backToCartButton = document.getElementById('backToCart');
    const placeOrderButton = document.getElementById('placeOrderButton');

    // Order summary elements
    const subtotalComponent = document.getElementById('subtotal');
    const shippingFeeComponent = document.getElementById('shippingFee');
    const totalAmountComponent = document.getElementById('totalAmount');

    // Shipping information elements
    const addressComponent = document.getElementById('address');

    // Payment method elements
    const paymentMethodRadios = document.querySelectorAll('input[name="paymentMethod"]');
    const creditCardForm = document.getElementById('creditCardForm');
    const balanceNotice = document.getElementById('balanceNotice');
    const accountBalanceElement = document.getElementById('accountBalance');

    // Credit card form elements
    const nameOnCardInput = document.getElementById('nameOnCard');
    const cardNumberInput = document.getElementById('cardNumber');
    const expiryMonthSelect = document.getElementById('expiryMonth');
    const expiryYearSelect = document.getElementById('expiryYear');
    const cvcInput = document.getElementById('cvc');

    // Balance notice elements
    const currentBalanceDisplay = document.getElementById('currentBalanceDisplay');
    const deductionAmountDisplay = document.getElementById('deductionAmount');

    // Initialize variables
    let subtotalAmount = 0;
    let shippingFeeAmount = 0;
    let totalAmount = 0;

    // Check authentication
    const userObject = UserAuthTracker.userObject;
    if (!userObject) {
        UserAuthTracker.handleUserInvalidState();
        return;
    }

    // Setup event listeners
    if (backToCartButton) {
        backToCartButton.addEventListener('click', () => {
            window.location.href = URL_Mapper.CART;
        });
    }

    if (placeOrderButton) {
        placeOrderButton.addEventListener('click', handlePlaceOrder);
    }

    if (paymentMethodRadios) {
        paymentMethodRadios.forEach(radio => {
            radio.addEventListener('change', updatePaymentUI);
        });
    }

    // Populate expiry years (current year + next 10 years)
    if (expiryYearSelect) {
        populateYearSelect(expiryYearSelect);
    }

    // Initialize UI
    updatePaymentUI();
    loadUserData();
    await loadOrderSummary();

    function updatePaymentUI() {
        const method = document.querySelector('input[name="paymentMethod"]:checked').value;
        if (method === 'creditCard') {
            creditCardForm.style.display = 'block';
            balanceNotice.style.display = 'none';
        } else {
            creditCardForm.style.display = 'none';
            balanceNotice.style.display = 'block';
            updateBalanceDisplay();
        }
    }

    function loadUserData() {
        // Display user address
        if (addressComponent) {
            addressComponent.textContent = userObject.address || 'Unspecified Address';
        }

        // Display account balance
        if (accountBalanceElement && currentBalanceDisplay) {
            const balance = userObject.accountBalance?.toFixed(2) || '0.00';
            accountBalanceElement.textContent = balance;
            currentBalanceDisplay.textContent = balance;
        }
    }

    async function loadOrderSummary() {
        const summaryLoadingOverlay = new LoadingOverlay();
        summaryLoadingOverlay.createAndDisplay('Loading Order Summary...');

        const subtotalPromise = CartManager.getSubtotal(userObject.userID);
        const shippingFeePromise = CartManager.getShippingFee(userObject.userID);

        const subtotalResponse = await subtotalPromise;
        if (!subtotalResponse) {
            MessagePopup.show('Unknown error, could not load subtotal!', true);
        } else if (!subtotalResponse.success) {
            window.location.href = URL_Mapper.CART + '?errorMessage='
                + encodeURIComponent(subtotalResponse.data);
        } else {
            const subtotal = subtotalResponse.data;
            if (subtotalComponent) {
                subtotalComponent.textContent = subtotal.toFixed(2);
            }
            subtotalAmount = subtotal;
        }

        const shippingResponse = await shippingFeePromise;
        if (!shippingResponse) {
            MessagePopup.show('Unknown error, could not load shipping fee!', true);
        } else if (!shippingResponse.success) {
            window.location.href = URL_Mapper.CART + '?errorMessage='
                + encodeURIComponent(shippingResponse.data);
        } else {
            const shippingFee = shippingResponse.data;
            if (shippingFeeComponent) {
                shippingFeeComponent.textContent = shippingFee.toFixed(2);
            }
            shippingFeeAmount = shippingFee;
        }

        summaryLoadingOverlay.remove();
        updateTotalAmount();
    }

    function updateTotalAmount() {
        totalAmount = subtotalAmount + shippingFeeAmount;
        if (totalAmountComponent) {
            totalAmountComponent.textContent = totalAmount.toFixed(2);
        }
        if (deductionAmountDisplay) {
            deductionAmountDisplay.textContent = totalAmount.toFixed(2);
        }
    }

    async function handlePlaceOrder(event) {
        event.preventDefault();
        const method = document.querySelector('input[name="paymentMethod"]:checked').value;
        const total = subtotalAmount + shippingFeeAmount;

        if (method === 'accountBalance') {
            await handleBalancePayment(total);
        } else {
            await handleCreditCardPayment();
        }
    }

    async function handleBalancePayment(total) {
        if (userObject.accountBalance < total) {
            MessagePopup.show(`Insufficient balance. Your balance is ${userObject.accountBalance.toFixed(2)} EGP`, true);
            return;
        }
        await placeOrderWithBalance(userObject.userID, userObject.address);
    }

    async function handleCreditCardPayment() {
        const nameOnCard = nameOnCardInput.value;
        const cardNumber = cardNumberInput.value;
        const expiryMonth = expiryMonthSelect.value;
        const expiryYear = expiryYearSelect.value;
        const cvc = cvcInput.value;

        // Validate required fields
        if (!nameOnCard || !cardNumber || !expiryMonth || !expiryYear || !cvc) {
            MessagePopup.show('Please fill in all payment information.', true);
            return;
        }

        // Validate credit card details
        const creditCardDetails = {
            nameOnCard,
            cardNumber,
            expiryMonth,
            expiryYear: parseInt(expiryYear) + 2000,
            cvc
        };

        const validationError = DataValidator.isCreditCardValid(creditCardDetails);
        if (validationError) {
            MessagePopup.show(validationError, true);
            return;
        }

        await placeOrderWithCreditCard(userObject.userID, userObject.address, creditCardDetails);
    }

    function handleSuccessfulOrder(message) {
        placeOrderButton.disabled = true;
        MessagePopup.show(`
            ${message}
            You will be redirected to orders page in 5 seconds.
        `);
        setTimeout(() => {
            window.location.href = URL_Mapper.ORDERS;
        }, 5000); // 5 seconds delay.
    }

    function getCheckingOutLoadingOverlay() {
        const checkingOutLoadingOverlay = new LoadingOverlay();
        checkingOutLoadingOverlay.createAndDisplay('Checking out...');
        return checkingOutLoadingOverlay;
    }

    async function placeOrderWithCreditCard(userID, address, creditCardDetails) {
        const loadingOverlay = getCheckingOutLoadingOverlay();

        const response = await OrdersManager.checkoutUsingCreditCard(userID, address, creditCardDetails);
        loadingOverlay.remove();

        if (response && response.success) {
            handleSuccessfulOrder(response.data);
        } else {
            MessagePopup.show(response?.data || 'Unknown error, cannot place order!', true);
        }
    }

    async function placeOrderWithBalance(userID, address) {
        const loadingOverlay = getCheckingOutLoadingOverlay();

        const response = await OrdersManager.checkoutUsingAccountBalance(userID, address);
        loadingOverlay.remove();

        if (response && response.success) {
            handleSuccessfulOrder(response.data);
        } else {
            MessagePopup.show(response?.data || 'Unknown error, cannot place order!', true);
        }
    }

    function updateBalanceDisplay() {
        if (currentBalanceDisplay && deductionAmountDisplay) {
            currentBalanceDisplay.textContent = userObject.accountBalance?.toFixed(2) || '0.00';
            deductionAmountDisplay.textContent = totalAmount.toFixed(2);
        }
    }
});