'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const OrdersManager = {
    async getOrdersList(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetAllOrdersList, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get orders:', error);
            MessagePopup.show('Failed to load orders', true);
            return null;
        }
    },

    async getOrderDetails(userID, orderID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetOrderDetails, { userID, orderID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get order details:', error);
            MessagePopup.show('Failed to load order details', true);
            return null;
        }
    },

    async checkoutUsingAccountBalance(userID, address) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userCheckoutUsingAccountBalance,
                { userID, address });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Checkout failed:', error);
            MessagePopup.show('Checkout failed', true);
            return null;
        }
    },

    async checkoutUsingCreditCard(userID, address, creditCardDetails) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userCheckoutUsingCreditCard,
                { userID, address, creditCardDetails });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Credit card checkout failed:', error);
            MessagePopup.show('Payment processing failed', true);
            return null;
        }
    }
};

export default OrdersManager;