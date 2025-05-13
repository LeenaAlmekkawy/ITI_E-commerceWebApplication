'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

/*
const userCartGetSubtotalStub = function(_) {
    return new Promise((resolve) => {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('subtotal')) {
            const subtotal = parseInt(urlParams.get('subtotal'));
            resolve(JSON.stringify({
                success: !isNaN(subtotal),
                data: isNaN(subtotal) ? 'Invalid subtotal!' : subtotal
            }));
        } else {
            resolve(JSON.stringify({
                success: false,
                data: 'Subtotal not provided!'
            }));
        }
    });
}
*/

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const CartManager = {
    async getCart(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetCart, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get cart:', error);
            MessagePopup.show('Failed to load cart', true);
            return null;
        }
    },

    async validateCart(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userValidateCart, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Cart validation failed:', error);
            MessagePopup.show('Cart validation error', true);
            return null;
        }
    },

    async getSubtotal(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetCartSubtotal, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Subtotal calculation failed:', error);
            MessagePopup.show('Subtotal error', true);
            return null;
        }
    },

    async getShippingFee(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetCartShippingFee, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Shipping fee calculation failed:', error);
            MessagePopup.show('Shipping calculation error', true);
            return null;
        }
    },

    async addItem(userID, bookID, quantity) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userAddItemToCart,
                { userID, bookID, quantity });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to add item:', error);
            MessagePopup.show('Failed to add item to cart', true);
            return null;
        }
    },

    async updateCartItem(userID, bookID, newQuantity) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userUpdateCartItemQuantity,
                { userID, bookID, newQuantity });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to update item:', error);
            MessagePopup.show('Failed to update cart item', true);
            return null;
        }
    },

    async removeCartItem(userID, bookID) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userRemoveCartItem,
                { userID, bookID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to remove item:', error);
            MessagePopup.show('Failed to remove item from cart', true);
            return null;
        }
    },

    async truncate(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userTruncateCart,
                { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to empty cart:', error);
            MessagePopup.show('Failed to empty cart', true);
            return null;
        }
    }
};

export default CartManager;