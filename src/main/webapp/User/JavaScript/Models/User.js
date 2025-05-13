'use strict';

import DataValidator from "../Utils/DataValidator.js";

export default class User {
    #userID = -1; // Private field
    #email = '';
    #hashPassword = ''; // Stored in plaintext for this demo
    #userName = '';
    #phoneNumber = '';
    #address = '';
    #birthDate = '';
    #accountBalance = 0.0;

    constructor() {
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

    get email() {
        return this.#email;
    }

    set email(value) {
        if (!DataValidator.isEmailValid(value)) {
            throw new Error('Invalid email!');
        }
        this.#email = value;
    }

    get hashPassword() {
        return this.#hashPassword;
    }

    set hashPassword(value) {
        if (!DataValidator.isPasswordValid(value) && value !== 'Hidden') {
            throw new Error('Invalid password!');
        }
        this.#hashPassword = value;
    }

    get userName() {
        return this.#userName;
    }

    set userName(value) {
        if (typeof value !== 'string' || value.trim().length === 0) {
            throw new Error('Invalid user name!');
        }
        this.#userName = value;
    }

    get phoneNumber() {
        return this.#phoneNumber;
    }

    set phoneNumber(value) {
        if (!DataValidator.isPhoneValid(value)) {
            throw new Error('Invalid phone number!');
        }
        this.#phoneNumber = value;
    }

    get address() {
        return this.#address;
    }

    set address(value) {
        if (typeof value !== 'string' || value.trim().length === 0) {
            throw new Error('Invalid address!');
        }
        this.#address = value;
    }

    get birthDate() {
        return this.#birthDate;
    }

    set birthDate(value) {
        if (!DataValidator.isBirthDateValid(value)) {
            throw new Error('Invalid birth date!');
        }
        this.#birthDate = new Date(value).toISOString();
    }

    get accountBalance() {
        return this.#accountBalance;
    }

    set accountBalance(value) {
        if (!DataValidator.isNonNegativeFloat(value)) {
            throw new Error('Invalid account balance!');
        }
        this.#accountBalance = value;
    }

    toJSON() {
        return {
            userID: this.#userID,
            email: this.#email,
            hashPassword: this.#hashPassword,
            userName: this.#userName,
            phoneNumber: this.#phoneNumber,
            address: this.#address,
            birthDate: this.#birthDate,
            accountBalance: this.#accountBalance
        }
    }
    
    static fromJSON(json) {
        const {
            userID,
            email,
            hashPassword,
            userName,
            phoneNumber,
            address,
            birthDate,
            accountBalance
        } = json;

        const user = new User();

        if (!DataValidator.isIDValid(userID)) {
            throw new Error('Invalid userID in JSON!');
        }
        user.userID = parseInt(userID);

        if (!DataValidator.isEmailValid(email)) {
            throw new Error('Invalid email in JSON!');
        }
        user.email = email;

        if (!DataValidator.isPasswordValid(hashPassword) && hashPassword !== 'Hidden') {
            throw new Error('Invalid password in JSON!');
        }
        user.hashPassword = hashPassword;

        if (typeof userName !== 'string' || userName.trim().length === 0) {
            throw new Error('Invalid user name in JSON!');
        }
        user.userName = userName;

        if (!DataValidator.isPhoneValid(phoneNumber)) {
            throw new Error('Invalid phone number in JSON!');
        }
        user.phoneNumber = phoneNumber;

        if (typeof address !== 'string' || address.trim().length === 0) {
            throw new Error('Invalid address in JSON!');
        }
        user.address = address;

        if (!DataValidator.isDateValid(birthDate)) {
            throw new Error('Invalid birth date in JSON!');
        }
        const inputDate = new Date(birthDate);
        if (inputDate > new Date()) {
            throw new Error('Birth date cannot be in the future in JSON!');
        }
        user.birthDate = inputDate.toISOString();

        if (!DataValidator.isNonNegativeFloat(accountBalance)) {
            throw new Error('Invalid account balance in JSON!');
        }
        user.accountBalance = parseFloat(accountBalance);

        return user;
    }
}