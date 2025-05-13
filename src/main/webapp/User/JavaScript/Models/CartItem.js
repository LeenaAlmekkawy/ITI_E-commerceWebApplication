'use strict';

import DataValidator from "../Utils/DataValidator.js";

export default class CartItem {
    #bookID;
    #quantity;

    constructor(bookID, quantity) {
        this.bookID = bookID;
        this.quantity = quantity;
    }

    get bookID() {
        return this.#bookID;
    }

    set bookID(value) {
        if (!DataValidator.isIDValid(value)) throw new Error('Invalid Book ID!');
        this.#bookID = parseInt(value);
    }

    get quantity() {
        return this.#quantity;
    }

    set quantity(value) {
        if (!DataValidator.isNonNegativeInteger(value)) {
            throw new Error('Quantity must be a non-negative number!');
        }
        this.#quantity = value;
    }

    toJSON() {
        return {
            bookID: this.#bookID,
            quantity: this.#quantity
        };
    }

    static fromJSON(json) {
        const {bookID, quantity} = json;

        if (!DataValidator.isIDValid(bookID)) {
            throw new Error('Invalid Book ID!');
        }
        const parsedBookID = parseInt(bookID);

        if (!quantity) {
            throw new Error('Quantity cannot be empty!');
        }
        if (!DataValidator.isNonNegativeInteger(quantity)) {
            throw new Error('Quantity must be a non-negative integer!');
        }
        const parsedQuantity = parseInt(quantity);

        return new CartItem(parsedBookID, parsedQuantity);
    }
}