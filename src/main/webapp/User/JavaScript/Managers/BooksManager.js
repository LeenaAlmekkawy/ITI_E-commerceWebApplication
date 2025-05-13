'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const BooksManager = {
    async getTopSelling() {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetTopSellingBooksList, '{}');
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Top selling books request failed:', error);
            MessagePopup.show('Unknown error', true);
            return null;
        }
    },

    async getTopSellingInGenres() {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetTopSellingBookFromEachGenreList, '{}');
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Genre books request failed:', error);
            MessagePopup.show('Unknown error', true);
            return null;
        }
    },

    async getAllBooks() {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetAllBooksList, '{}');
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('All books request failed:', error);
            MessagePopup.show('Unknown error', true);
            return null;
        }
    },

    async search(keywords) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userSearchBooks, { keywords });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Search request failed:', error);
            MessagePopup.show('Unknown error', true);
            return null;
        }
    },

    async getBookDetails(bookID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetBookDetails, { bookID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Book details request failed:', error);
            MessagePopup.show('Unknown error', true);
            return null;
        }
    }
};

export default BooksManager;