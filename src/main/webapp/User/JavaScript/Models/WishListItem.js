'use strict';

import DataValidator from "../Utils/DataValidator.js";

export default class WishListItem {
    #userID = -1;
    #bookID = -1;

    constructor(userID, bookID) {
        this.userID = userID;
        this.bookID = bookID;
    }

    get userID() {
        return this.#userID;
    }

    set userID(userID) {
        if (!DataValidator.isIDValid(userID)) {
            throw new Error('Invalid user ID!');
        }
        this.#userID = userID;
    }

    get bookID() {
        return this.#bookID;
    }

    set bookID(bookID) {
        if (!DataValidator.isIDValid(bookID)) {
            throw new Error('Invalid book ID!');
        }
        this.#bookID = bookID;
    }

    toJSON() {
        return {
            userID: this.#userID,
            bookID: this.#bookID
        };
    }

    static fromJSON(json) {
        const { userID, bookID } = json;

        if (!DataValidator.isIDValid(userID)) {
            throw new Error('Invalid user ID!');
        }
        const parsedUserID = parseInt(userID);
        if (!DataValidator.isIDValid(bookID)) {
            throw new Error('Invalid book ID!');
        }
        const parsedBookID = parseInt(bookID);

        return new WishListItem(parsedUserID, parsedBookID);
    }
}