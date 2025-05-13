'use strict';

import User from "../Models/User.js";
import URL_Mapper from "../Utils/URL_Mapper.js";

class UserAuthTracker {
    #isAuthenticated = false;
    #userObject = null;
    #COOKIE_NAME = 'user_auth';
    #COOKIE_EXPIRY_DAYS = 7;

    #onAuthChange = null;

    constructor() {
        let cookieData = this.#getCookie();
        if (cookieData) {
            try {
                cookieData = JSON.parse(cookieData);
                this.#userObject = User.fromJSON(cookieData);
                this.#isAuthenticated = true;
            } catch (e) {
                console.error('Failed to parse user cookie:', e);
                this.#clearCookie();
            }
        }
    }

    get isAuthenticated() {
        return this.#isAuthenticated;
    }

    get userObject() {
        return this.#userObject;
    }

    set userObject(userObject) {
        this.#userObject = userObject;
        this.#isAuthenticated = !!userObject;

        if (userObject) {
            const jsonUserObject = userObject.toJSON();
            this.#setCookie(JSON.stringify(jsonUserObject));
        } else {
            this.#clearCookie();
        }

        if (this.#onAuthChange && typeof this.#onAuthChange === 'function') {
            this.#onAuthChange();
        }
    }

    set onAuthChange(onAuthChange) {
        if (typeof onAuthChange !== 'function') {
            throw new Error('onAuthChange must be a function');
        }
        this.#onAuthChange = onAuthChange;
    }

    // Private cookie methods now use the class's cookie name
    #setCookie(value) {
        const date = new Date();
        date.setTime(date.getTime() + (this.#COOKIE_EXPIRY_DAYS * 24 * 60 * 60 * 1000));
        document.cookie = `${this.#COOKIE_NAME}=${value};expires=${date.toUTCString()};path=/`;
    }

    #getCookie() {
        const cookies = `; ${document.cookie}`;
        const parts = cookies.split(`; ${this.#COOKIE_NAME}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    }

    #clearCookie() {
        document.cookie = `${this.#COOKIE_NAME}=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;`;
    }

    handleUserInvalidState() {
        this.userObject = null;
        window.location.href = URL_Mapper.WELCOME + `?errorMessage=Not logged in!`;
    }
}

export default new UserAuthTracker();