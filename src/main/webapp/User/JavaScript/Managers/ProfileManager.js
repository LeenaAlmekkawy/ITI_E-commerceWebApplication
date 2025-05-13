'use strict';

import MessagePopup from "../Common/MessagePopup.js";
import handleResponse from "./ManagersUtils/responseHandler.js";

import VanillaAJAX from "./AJAX/VanillaAJAX.js";
import ServerURLMapper from "./AJAX/ServerURLMapper.js";

// Create an instance of VanillaAJAX to use for all requests.
const ajaxClient = new VanillaAJAX();

const ProfileManager = {
    async login(email, password) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userLogin,
                    { email, password });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Login failed:', error);
            MessagePopup.show('Login failed', true);
            return null;
        }
    },

    async logout(userID) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userLogout,
                    { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Login failed:', error);
            MessagePopup.show('Login failed', true);
            return null;
        }
    },

    async register(userData) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userRegister,
                    { userData });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Registration failed:', error);
            MessagePopup.show('Registration failed', true);
            return null;
        }
    },

    async getProfile(userID) {
        try {
            const rawResponse
                = await ajaxClient.get(ServerURLMapper.userGetProfile, { userID });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Failed to get profile:', error);
            MessagePopup.show('Failed to load profile', true);
            return null;
        }
    },

    async updateEmail(userID, newEmail) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userUpdateEmail,
                    { userID, newEmail });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Email update failed:', error);
            MessagePopup.show('Failed to update email', true);
            return null;
        }
    },

    async updatePassword(userID, currentPassword, newPassword) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userUpdatePassword,
                    { userID, currentPassword, newPassword });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Password update failed:', error);
            MessagePopup.show('Failed to update password', true);
            return null;
        }
    },

    async updateDetails(userID, updatedDetails) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userUpdatePersonalDetails,
                    { userID, updatedDetails });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Details update failed:', error);
            MessagePopup.show('Failed to update details', true);
            return null;
        }
    },

    async rechargeAccountBalanceUsingCreditCard(userID, creditCardDetails, amount) {
        try {
            const rawResponse
                = await ajaxClient.post(ServerURLMapper.userRechargeAccountBalanceUsingCreditCard,
                    { userID, creditCardDetails, amount });
            return handleResponse(rawResponse);
        } catch (error) {
            console.error('Balance recharge failed:', error);
            MessagePopup.show('Payment processing failed', true);
            return null;
        }
    }
};

export default ProfileManager;