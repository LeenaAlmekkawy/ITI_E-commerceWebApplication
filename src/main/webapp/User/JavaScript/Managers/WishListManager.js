'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const WishListManager = {
    async getAllWishList(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetAllWishList, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get wishlist:', error);
            MessagePopup.show('Failed to load wishlist', true);
            return null;
        }
    },

    async getAllWishListBooks(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetAllWishListBooks, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get wishlist books:', error);
            MessagePopup.show('Failed to load wishlist books', true);
            return null;
        }
    },

    async addWishListItem(userID, bookID) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userAddWishListItem,
                { userID, bookID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to add to wishlist:', error);
            MessagePopup.show('Failed to add to wishlist', true);
            return null;
        }
    },

    async removeFromWishList(userID, bookID) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userRemoveFromWishList,
                { userID, bookID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to remove from wishlist:', error);
            MessagePopup.show('Failed to remove from wishlist', true);
            return null;
        }
    }
};

export default WishListManager;