'use strict';

import DataValidator from "../Utils/DataValidator.js";
import CartItem from "./CartItem.js";

export default class Cart {
    #userID = -1;
    #items = [];

    constructor() {
    }
    
    get userID() {
        return this.#userID;
    }
    
    set userID(newUserID) {
        if (!DataValidator.isIDValid(newUserID)) {
            throw new Error('Invalid user ID!');
        }
        this.#userID = newUserID;
    }

    get items() {
        return this.#items;
    }

    set items(newItems) {
        if (!Array.isArray(newItems)) {
            throw new Error('Items must be an array!');
        }
        this.#items = [];
        newItems.forEach(item => {
            this.addItem(item);
        })
    }

    addItem(item) {
        if (!(item instanceof CartItem)) {
            throw new Error('Item must be an instance of CartItem');
        }
        this.#items.push(item);
    }

    removeItem(bookID) {
        const index = this.#items.findIndex(item => item.bookID === bookID);
        if (index === -1) {
            throw new Error('Item with the specified bookID not found!');
        }
        this.#items.splice(index, 1);
    }

    toJSON() {
        return {
            userID: this.#userID,
            items: this.#items.map(cartItem => cartItem.toJSON()),
        };
    }

    static fromJSON(json) {
        if (typeof json !== 'object' || json === null) {
            throw new Error('Invalid JSON object!');
        }

        const {userID, items} = json;

        if (!DataValidator.isIDValid(userID)) {
            throw new Error('Invalid user ID!');
        }
        if (!Array.isArray(items)) {
            throw new Error('Items must be an array!');
        }

        const cart = new Cart();
        cart.userID = parseInt(userID);
        items.forEach(item => {
            const parsedItem = CartItem.fromJSON(item);
            cart.addItem(parsedItem);
        });
        return cart;
    }
}