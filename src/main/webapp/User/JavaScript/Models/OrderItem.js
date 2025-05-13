'use strict';

import DataValidator from "../Utils/DataValidator.js";

export default class OrderItem {
    #bookID = -1;
    #quantity = 0;
    #priceAtPurchase = 0.0;

    constructor(bookID, quantity, priceAtPurchase) {
        this.bookID = bookID;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    get bookID() {
        return this.#bookID;
    }

    set bookID(value) {
        if (!DataValidator.isIDValid(value)) {
            throw new Error('Invalid Book ID!');
        }
        this.#bookID = value;
    }

    get quantity() {
        return this.#quantity;
    }

    set quantity(value) {
        if (!DataValidator.isNonNegativeInteger(value)) {
            throw new Error('Quantity must be a non-negative integer!');
        }
        this.#quantity = value;
    }

    get priceAtPurchase() {
        return this.#priceAtPurchase;
    }

    set priceAtPurchase(value) {
        if (!DataValidator.isNonNegativeFloat(value)) {
            throw new Error('Price at purchase must be a non-negative float!');
        }
        this.#priceAtPurchase = value;
    }

    toJSON() {
        return {
            bookID: this.#bookID,
            quantity: this.#quantity,
            priceAtPurchase: this.#priceAtPurchase
        };
    }

    static fromJSON(json) {
        const { bookID, quantity, priceAtPurchase } = json;

        if (!DataValidator.isIDValid(bookID)) {
            throw new Error('Invalid Book ID in JSON!');
        }
        if (!DataValidator.isNonNegativeInteger(quantity)) {
            throw new Error('Quantity must be a non-negative integer in JSON!');
        }
        if (!DataValidator.isNonNegativeFloat(priceAtPurchase)) {
            throw new Error('Price at purchase must be a non-negative float in JSON!');
        }

        return new OrderItem(bookID, quantity, priceAtPurchase);
    }
}