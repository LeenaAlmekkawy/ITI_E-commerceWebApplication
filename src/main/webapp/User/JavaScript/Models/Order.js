'use strict';

import PaymentMethods from "./PaymentMethods.js";
import DataValidator from "../Utils/DataValidator.js";
import OrderStatus from "./OrderStatus.js";

export default class Order {
    #orderID = -1;
    #userID = -1;
    #date = new Date();
    #address = null;
    #paymentMethod = PaymentMethods.ACCOUNT_BALANCE;
    #shippingFee;
    #orderItems = [];
    #status = OrderStatus.PENDING;

    constructor() {
    }

    get orderID() {
        return this.#orderID;
    }

    set orderID(value) {
        if (!DataValidator.isIDValid(value)) {
            throw new Error('Invalid order ID!');
        }
        this.#orderID = value;
    }

    get userID() {
        return this.#userID;
    }

    set userID(value) {
        if (!DataValidator.isIDValid(value)) {
            throw new Error('Invalid user ID!');
        }
        this.#userID = value;
    }

    get date() {
        return this.#date;
    }

    set date(value) {
        if (!DataValidator.isDateValid(value)) {
            throw new Error('Invalid date!');
        }
        this.#date = value;
    }

    get address() {
        return this.#address;
    }

    set address(value) {
        this.#address = value;
    }

    get paymentMethod() {
        return this.#paymentMethod;
    }

    set paymentMethod(value) {
        if (!Object.values(PaymentMethods).includes(value)) {
            throw new Error('Invalid payment method!');
        }
        this.#paymentMethod = value;
    }

    get shippingFee() {
        return this.#shippingFee;
    }

    set shippingFee(value) {
        if (!DataValidator.isNonNegativeFloat(value)) {
            throw new Error('Invalid shipping fee!');
        }
        this.#shippingFee = value;
    }

    get orderItems() {
        return this.#orderItems;
    }

    set orderItems(value) {
        if (!Array.isArray(value)) {
            throw new Error('Order items must be an array!');
        }
        this.#orderItems = value;
    }

    get status() {
        return this.#status;
    }

    set status(value) {
        if (!Object.values(OrderStatus).includes(value)) {
            throw new Error('Invalid status!');
        }
        this.#status = value;
    }

    toJSON() {
        return {
            orderID: this.#orderID,
            userID: this.#userID,
            date: this.#date.toISOString(),
            address: this.#address,
            paymentMethod: this.#paymentMethod,
            shippingFee: this.#shippingFee,
            orderItems: this.#orderItems,
            status: this.#status
        };
    }
    /*
        static fromJSON(json) {
            const {
                orderID,
                userID,
                date,
                address,
                paymentMethod,
                shippingFee,
                orderItems,
                status
            } = json;

            if (!DataValidator.isIDValid(orderID)) {
                throw new Error('Invalid order ID in JSON!');
            }

            if (!DataValidator.isIDValid(userID)) {
                throw new Error('Invalid user ID in JSON!');
            }

            const parsedDate = new Date(date);
            if (!DataValidator.isDateValid(parsedDate)) {
                throw new Error('Invalid date in JSON!');
            }

            if (!Object.values(PaymentMethods).includes(paymentMethod)) {
                throw new Error('Invalid payment method in JSON!');
            }

            if (!DataValidator.isNonNegativeFloat(shippingFee)) {
                throw new Error('Invalid shipping fee in JSON!');
            }

            if (!Array.isArray(orderItems)) {
                throw new Error('Order items must be an array in JSON!');
            }

            if (!Object.values(OrderStatus).includes(status)) {
                throw new Error('Invalid status in JSON!');
            }

            const newOrder = new Order();
            newOrder.orderID = parseInt(orderID);
            newOrder.userID = parseInt(userID);
            newOrder.date = parsedDate;
            newOrder.address = address;
            newOrder.paymentMethod = paymentMethod;
            newOrder.shippingFee = parseFloat(shippingFee);
            newOrder.orderItems = orderItems;
            newOrder.status = status;
            return newOrder;
        }
            */
    static fromJSON(json) {
        const {
            orderID,
            userID,
            date,
            address,
            paymentMethod,
            shippingFee,
            orderItems,
            status
        } = json;

        if (!DataValidator.isIDValid(orderID)) {
            throw new Error('Invalid order ID in JSON!');
        }

        if (!DataValidator.isIDValid(userID)) {
            throw new Error('Invalid user ID in JSON!');
        }

        // Fix date parsing
        let parsedDateString = date;
        if (typeof parsedDateString === 'string' && parsedDateString.includes('.')) {
            parsedDateString = parsedDateString.split('.')[0] + "Z";
        }
        const parsedDate = new Date(parsedDateString);

        if (isNaN(parsedDate.getTime())) {
            throw new Error('Invalid date in JSON!');
        }

        // Fix paymentMethod parsing
        let normalizedPaymentMethod = paymentMethod.toLowerCase();
        if (!Object.values(PaymentMethods).includes(normalizedPaymentMethod)) {
            throw new Error('Invalid payment method in JSON!');
        }

        if (!DataValidator.isNonNegativeFloat(shippingFee)) {
            throw new Error('Invalid shipping fee in JSON!');
        }

        if (!Array.isArray(orderItems)) {
            throw new Error('Order items must be an array in JSON!');
        }

        if (!Object.values(OrderStatus).includes(status)) {
            throw new Error('Invalid status in JSON!');
        }

        const newOrder = new Order();
        newOrder.orderID = parseInt(orderID);
        newOrder.userID = parseInt(userID);
        newOrder.date = parsedDate;
        newOrder.address = address;
        newOrder.paymentMethod = normalizedPaymentMethod;
        newOrder.shippingFee = parseFloat(shippingFee);
        newOrder.orderItems = orderItems;
        newOrder.status = status;
        return newOrder;
    }


}