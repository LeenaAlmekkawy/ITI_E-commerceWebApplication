'use strict';

import DataValidator from "../Utils/DataValidator.js";

export default class HistoryItem {
    #itemID = -1;
    #userID = -1;
    #date = null;
    #totalPaid = 0.0;
    #receiptFileUrl = null;

    constructor() {
    }

    get itemID() {
        return this.#itemID;
    }

    set itemID(itemID) {
        if (!DataValidator.isIDValid(itemID)) {
            throw new Error('Invalid item ID!');
        }
        this.#itemID = itemID;
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

    get date() {
        return this.#date;
    }

    set date(date) {
        if (!DataValidator.isDateValid(date)) {
            throw new Error('Invalid date!');
        }
        this.#date = date;
    }

    get totalPaid() {
        return this.#totalPaid;
    }

    set totalPaid(totalPaid) {
        if (!DataValidator.isNonNegativeFloat(totalPaid)) {
            throw new Error('Invalid total paid!');
        }
        this.#totalPaid = totalPaid;
    }

    get receiptFileUrl() {
        return this.#receiptFileUrl;
    }

    set receiptFileUrl(receiptFileUrl) {
        this.#receiptFileUrl = receiptFileUrl;
    }

    toJSON() {
        return {
            itemID: this.#itemID,
            userID: this.#userID,
            date: this.#date.toISOString(),
            totalPaid: this.#totalPaid,
            receiptFileUrl: this.#receiptFileUrl ? encodeURI(this.#receiptFileUrl) : null
        };
    }

    static fromJSON(json) {
        const { itemID, userID, date, totalPaid, receiptFileUrl } = json;

        if (!DataValidator.isIDValid(itemID)) {
            throw new Error('Invalid item ID in JSON!');
        }
        const parsedItemID = parseInt(itemID);

        if (!DataValidator.isIDValid(userID)) {
            throw new Error('Invalid user ID in JSON!');
        }
        const parsedUserID = parseInt(userID);

        const parsedDate = new Date(date);
        if (!DataValidator.isDateValid(parsedDate)) {
            throw new Error('Invalid date in JSON!');
        }

        if (!DataValidator.isNonNegativeFloat(totalPaid)) {
            throw new Error('Invalid total paid in JSON!');
        }
        const parsedTotalPaid = parseFloat(totalPaid);

        if (receiptFileUrl !== null && (typeof receiptFileUrl !== 'string' || receiptFileUrl.trim().length === 0)) {
            throw new Error('Invalid receipt file URL in JSON!');
        }

        const item = new HistoryItem();
        item.itemID = parsedItemID;
        item.userID = parsedUserID;
        item.date = parsedDate;
        item.totalPaid = parsedTotalPaid;
        item.receiptFileUrl = receiptFileUrl ? decodeURI(receiptFileUrl) : null;
        return item;
    }
}