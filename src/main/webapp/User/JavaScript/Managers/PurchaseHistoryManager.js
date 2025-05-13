'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const PurchaseHistoryManager = {
    async getAllPurchaseHistory(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetAllPurchaseHistoryList, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get purchase history:', error);
            MessagePopup.show('Failed to load purchase history', true);
            return null;
        }
    },

    async getPurchaseHistoryItem(userID, itemID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetPurchaseHistoryItemDetails,
                { userID, itemID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get purchase item:', error);
            MessagePopup.show('Failed to load purchase details', true);
            return null;
        }
    }
};

export default PurchaseHistoryManager;