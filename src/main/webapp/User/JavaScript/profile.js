'use strict';

import checkForErrorMessageParameter from "./Common/checkForError.js";
import URL_Mapper from "./Utils/URL_Mapper.js";
import DataValidator from "./Utils/DataValidator.js";
import ProfileManager from "./Managers/ProfileManager.js";
import UserAuthTracker from "./Common/UserAuthTracker.js";
import User from "./Models/User.js";
import MessagePopup from "./Common/MessagePopup.js";
import { populateYearSelect } from "./Utils/UICommonFunctions.js";

// Helper methods.
function validatePasswordChange(currentPassword, newPassword, confirmPassword) {
    if (!currentPassword) return { isValid: false, message: "Current password cannot be empty." };
    if (!DataValidator.isPasswordValid(currentPassword)) {
        return {
            isValid: false,
            message:
                "Invalid current password format. Must be at least 8 characters, " +
                "includes one uppercase & one lowercase letter.",
        };
    }
    if (!newPassword) return { isValid: false, message: "New password cannot be empty." };
    if (!DataValidator.isPasswordValid(newPassword)) {
        return {
            isValid: false,
            message:
                "Invalid new password format. Must be at least 8 characters, includes one uppercase & one lowercase letter.",
        };
    }
    if (newPassword !== confirmPassword) {
        return { isValid: false, message: "New password and confirm password do not match." };
    }
    if (currentPassword === newPassword) {
        return { isValid: false, message: "Current password cannot be the same as new password." };
    }
    return { isValid: true };
}

function validateEmailChange(email) {
    if (!email) return { isValid: false, message: "New email cannot be empty." };
    if (!DataValidator.isEmailValid(email)) {
        return { isValid: false, message: "Invalid email format." };
    }
    return { isValid: true };
}

function validateProfileUpdate({ userName, phoneNumber, address, birthDate }) {
    if (!userName) return { isValid: false, message: "User name cannot be empty." };
    if (!DataValidator.isUserNameValid(userName)) {
        return { isValid: false, message: "Invalid user name!" };
    }
    if (!phoneNumber) return { isValid: false, message: "Phone number cannot be empty!" };
    if (!DataValidator.isPhoneValid(phoneNumber)) {
        return {
            isValid: false,
            message: "Invalid phone number format, phone number must be a valid Egyptian mobile number!",
        };
    }
    if (!address) return { isValid: false, message: "Address cannot be empty!" };
    if (!DataValidator.isAddressValid(address)) {
        return { isValid: false, message: "Invalid address!" };
    }
    if (!birthDate) return { isValid: false, message: "Birth date cannot be empty!" };
    if (!DataValidator.isDateValid(birthDate)) {
        return { isValid: false, message: "Birth date is not a valid date!" };
    }
    if (!DataValidator.isBirthDateValid(birthDate)) {
        return { isValid: false, message: "Age must be between 10 and 120!" };
    }
    return { isValid: true };
}

document.addEventListener("DOMContentLoaded", async () => {
    checkForErrorMessageParameter();

    // DOM Elements
    const elements = {
        // Profile sections
        profileName: document.getElementById("profileName"),
        profileEmail: document.getElementById("profileEmail"),
        profileSections: document.querySelectorAll(".profile-section"),
        navItems: document.querySelectorAll(".profile-nav-item"),
        // Navigation buttons
        viewOrdersButton: document.getElementById("viewOrders"),
        viewPurchaseHistoryButton: document.getElementById("viewPurchaseHistory"),
        viewWishList: document.getElementById("viewWishList"),
        // Popup elements
        popupOverlay: document.querySelector(".popup-overlay"),
        passwordPopup: document.getElementById("passwordPopup"),
        emailPopup: document.getElementById("emailPopup"),
        rechargePopup: document.getElementById("rechargePopup"),
        popupCloseButtons: document.querySelectorAll(".popup-close"),
        // Password change elements
        openChangePasswordPopupButton: document.getElementById("openChangePasswordPopupButton"),
        passwordChangeError: document.getElementById("passwordChangeError"),
        currentPasswordInput: document.getElementById("currentPassword"),
        newPasswordInput: document.getElementById("newPassword"),
        confirmPasswordInput: document.getElementById("confirmPassword"),
        confirmPasswordChangeButton: document.getElementById("confirmPasswordChangeButton"),
        cancelPasswordChangeButton: document.getElementById("cancelPasswordChangeButton"),
        // Email change elements
        displayEmail: document.getElementById("displayEmail"),
        openChangeEmailPopupButton: document.getElementById("openChangeEmailPopupButton"),
        emailChangeError: document.getElementById("emailChangeError"),
        emailInput: document.getElementById("newEmail"),
        confirmEmailUpdate: document.getElementById("confirmEmailUpdate"),
        cancelEmailUpdate: document.getElementById("cancelEmailUpdate"),
        // Profile edit elements
        editProfileForm: document.getElementById("editProfileForm"),
        editUserName: document.getElementById("editUserName"),
        editPhone: document.getElementById("editPhone"),
        editAddress: document.getElementById("editAddress"),
        editBirthDate: document.getElementById("editBirthDate"),
        profileChangesError: document.getElementById("profileChangesError"),
        // Balance section
        balanceInput: document.getElementById("userBalance"),
        rechargeBalanceButton: document.getElementById("rechargeBalanceButton"),
        // Balance recharge form
        rechargeAmountInput: document.getElementById("rechargeAmount"),
        confirmRechargeButton: document.getElementById("confirmRecharge"),
        cancelRechargeButton: document.getElementById("cancelRecharge"),
        nameOnCardInput: document.getElementById("rechargeNameOnCard"),
        cardNumberInput: document.getElementById("rechargeCardNumber"),
        expiryMonthSelect: document.getElementById("rechargeExpiryMonth"),
        expiryYearSelect: document.getElementById("rechargeExpiryYear"),
        cvcInput: document.getElementById("rechargeCvc"),
    };

    // Check if critical DOM elements exist
    const missingElements = Object.entries(elements)
        .filter(([key, value]) => !value && key !== "popupOverlay") // Allow popupOverlay to be checked separately
        .map(([key]) => key);
    if (missingElements.length > 0) {
        console.error(`Missing DOM elements: ${missingElements.join(", ")}`);
        MessagePopup.show("Error: Page layout is broken. Some components are missing.", true);
        return;
    }

    // Check if user is authenticated
    const userObject = UserAuthTracker.userObject;
    if (!userObject) {
        UserAuthTracker.handleUserInvalidState();
        return;
    }

    // Initialize
    if (elements.expiryYearSelect) {
        populateYearSelect(elements.expiryYearSelect);
    }
    updateUserProfile(userObject);
    setupEventListeners();
    await loadUserProfile();

    // Tab navigation
    function setupTabNavigation() {
        elements.navItems.forEach((item) => {
            item.addEventListener("click", function () {
                const sectionId = this.getAttribute("data-section");

                // If it's a navigation link, handle it separately
                if (!sectionId) return;

                // Remove active class from all nav items and sections
                elements.navItems.forEach((navItem) => navItem.classList.remove("active"));
                elements.profileSections.forEach((section) => section.classList.remove("active"));

                // Add active class to clicked nav item and corresponding section
                this.classList.add("active");
                const activeSection = document.getElementById(sectionId);
                if (activeSection) {
                    activeSection.classList.add("active");
                    // Scroll to the top of the section for better UX
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }
            });
        });
    }

    async function loadUserProfile() {
        try {
            const response = await ProfileManager.getProfile(userObject.userID);
            if (!response) {
                MessagePopup.show("Unknown error: Could not refresh the profile from the server.", true);
                return;
            }

            if (!response.success) {
                MessagePopup.show(response.data || "Failed to load profile.", true);
                return;
            }

            updateUserProfile(response.data);
        } catch (error) {
            console.error("Error loading profile:", error);
            MessagePopup.show("Failed to load profile due to a network error.", true);
        }
    }

    function updateUserProfile(userData) {
        try {
            const parsedUser = User.fromJSON(userData);
            UserAuthTracker.userObject = parsedUser;

            // Update profile header
            if (elements.profileName) {
                elements.profileName.textContent = parsedUser.userName || "User";
            }

            if (elements.profileEmail) {
                elements.profileEmail.textContent = parsedUser.email || "";
            }

            // Update UI with null checks
            if (elements.displayEmail) {
                elements.displayEmail.textContent = parsedUser.email || "";
            }
            if (elements.editUserName) {
                elements.editUserName.value = parsedUser.userName || "";
            }
            if (elements.editPhone) {
                elements.editPhone.value = parsedUser.phoneNumber || "";
            }
            if (elements.editAddress) {
                elements.editAddress.value = parsedUser.address || "";
            }
            if (elements.editBirthDate && parsedUser.birthDate) {
                const birthDate = new Date(parsedUser.birthDate);
                elements.editBirthDate.value = isNaN(birthDate) ? "" : birthDate.toISOString().split("T")[0];
            }
            if (elements.balanceInput) {
                elements.balanceInput.textContent = parsedUser.accountBalance != null ? `${parsedUser.accountBalance} EGP` : "0 EGP";
            }
        } catch (error) {
            console.error("Could not parse user object:", error);
            MessagePopup.show("Error: Failed to update profile display.", true);
        }
    }

    function setupEventListeners() {
        // Tab navigation
        setupTabNavigation();

        // Navigation
        if (elements.viewOrdersButton) {
            elements.viewOrdersButton.addEventListener("click", (e) => {
                e.preventDefault();
                navigateTo(URL_Mapper.ORDERS);
            });
        }
        if (elements.viewPurchaseHistoryButton) {
            elements.viewPurchaseHistoryButton.addEventListener("click", (e) => {
                e.preventDefault();
                navigateTo(URL_Mapper.PURCHASE_HISTORY);
            });
        }
        if (elements.viewWishList) {
            elements.viewWishList.addEventListener("click", (e) => {
                e.preventDefault();
                navigateTo(URL_Mapper.WISH_LIST);
            });
        }

        // Popups
        if (elements.popupOverlay) {
            elements.popupOverlay.addEventListener("click", handleOverlayClick);
        }

        // Close popup buttons
        elements.popupCloseButtons.forEach((button) => {
            button.addEventListener("click", () => {
                const popup = button.closest(".popup");
                if (popup) {
                    const popupId = popup.id;
                    const type = popupId.replace("Popup", "").toLowerCase();
                    closePopup(type);
                }
            });
        });

        if (elements.openChangePasswordPopupButton) {
            elements.openChangePasswordPopupButton.addEventListener("click", () => openPopup("password"));
        }
        if (elements.openChangeEmailPopupButton) {
            elements.openChangeEmailPopupButton.addEventListener("click", () => openPopup("email"));
        }

        // Password change
        if (elements.confirmPasswordChangeButton) {
            elements.confirmPasswordChangeButton.addEventListener("click", handlePasswordChange);
        }
        if (elements.cancelPasswordChangeButton) {
            elements.cancelPasswordChangeButton.addEventListener("click", () => closePopup("password"));
        }

        // Email change
        if (elements.confirmEmailUpdate) {
            elements.confirmEmailUpdate.addEventListener("click", handleEmailChange);
        }
        if (elements.cancelEmailUpdate) {
            elements.cancelEmailUpdate.addEventListener("click", () => closePopup("email"));
        }

        // Profile edit
        if (elements.editProfileForm) {
            elements.editProfileForm.addEventListener("submit", handleProfileUpdate);
        }

        // Balance recharge
        if (elements.rechargeBalanceButton) {
            elements.rechargeBalanceButton.addEventListener("click", () => openPopup("recharge"));
        }
        if (elements.confirmRechargeButton) {
            elements.confirmRechargeButton.addEventListener("click", handleRechargeBalance);
        }
        if (elements.cancelRechargeButton) {
            elements.cancelRechargeButton.addEventListener("click", () => closePopup("recharge"));
        }
    }

    function navigateTo(url) {
        window.location.href = url;
    }

    function handleOverlayClick() {
        if (elements.passwordPopup && !elements.passwordPopup.classList.contains("hidden")) {
            closePopup("password");
        } else if (elements.emailPopup && !elements.emailPopup.classList.contains("hidden")) {
            closePopup("email");
        } else if (elements.rechargePopup && !elements.rechargePopup.classList.contains("hidden")) {
            closePopup("recharge");
        }
    }

    function openPopup(type) {
        if (elements[`${type}Popup`] && elements.popupOverlay) {
            elements[`${type}Popup`].classList.remove("hidden");
            elements.popupOverlay.classList.remove("hidden");
        } else {
            console.error(`Cannot open ${type} popup: Missing popup or overlay element.`);
        }
    }

    function closePopup(type) {
        const popup = elements[`${type}Popup`];
        const errorElement = elements[`${type}ChangeError`] || elements[`${type}Error`];

        if (!popup) {
            console.error(`Cannot close ${type} popup: Popup element is missing.`);
            return;
        }

        if (
            type === "password" &&
            elements.currentPasswordInput &&
            elements.newPasswordInput &&
            elements.confirmPasswordInput
        ) {
            elements.currentPasswordInput.value = "";
            elements.newPasswordInput.value = "";
            elements.confirmPasswordInput.value = "";
        } else if (type === "email" && elements.emailInput) {
            elements.emailInput.value = "";
        } else if (type === "recharge" && elements.rechargeAmountInput) {
            elements.rechargeAmountInput.value = "";
            if (elements.nameOnCardInput) elements.nameOnCardInput.value = "";
            if (elements.cardNumberInput) elements.cardNumberInput.value = "";
            if (elements.cvcInput) elements.cvcInput.value = "";
        }

        if (errorElement) {
            errorElement.textContent = "";
        }

        popup.classList.add("hidden");
        if (elements.popupOverlay) {
            elements.popupOverlay.classList.add("hidden");
        }
    }

    async function handlePasswordChange() {
        if (!elements.currentPasswordInput || !elements.newPasswordInput || !elements.confirmPasswordInput) {
            MessagePopup.show("Error: Password change form is incomplete.", true);
            return;
        }

        const currentPassword = elements.currentPasswordInput.value.trim();
        const newPassword = elements.newPasswordInput.value.trim();
        const confirmPassword = elements.confirmPasswordInput.value.trim();

        const validation = validatePasswordChange(currentPassword, newPassword, confirmPassword);
        if (!validation.isValid) {
            if (elements.passwordChangeError) {
                elements.passwordChangeError.textContent = validation.message;
            }
            return;
        }

        try {
            const response = await ProfileManager.updatePassword(userObject.userID, currentPassword, newPassword);
            if (elements.passwordChangeError) {
                elements.passwordChangeError.textContent = "";
            }

            if (!response) {
                MessagePopup.show("Unknown error: Could not update password.", true);
                return;
            }

            MessagePopup.show(response.data, !response.success);
            if (response.success) {
                closePopup("password");
            }
        } catch (error) {
            console.error("Error updating password:", error);
            MessagePopup.show("Failed to update password due to a network error.", true);
        }
    }

    async function handleEmailChange() {
        if (!elements.emailInput) {
            MessagePopup.show("Error: Email change form is incomplete.", true);
            return;
        }

        const newEmail = elements.emailInput.value.trim();

        const validation = validateEmailChange(newEmail);
        if (!validation.isValid) {
            if (elements.emailChangeError) {
                elements.emailChangeError.textContent = validation.message;
            }
            return;
        }

        try {
            const response = await ProfileManager.updateEmail(userObject.userID, newEmail);
            if (elements.emailChangeError) {
                elements.emailChangeError.textContent = "";
            }

            if (!response) {
                MessagePopup.show("Unknown error: Could not update email.", true);
                return;
            }

            MessagePopup.show(response.data, !response.success);
            if (response.success) {
                if (elements.displayEmail) {
                    elements.displayEmail.textContent = newEmail;
                }
                if (elements.profileEmail) {
                    elements.profileEmail.textContent = newEmail;
                }
                userObject.email = newEmail;
                UserAuthTracker.userObject = userObject;
                closePopup("email");
            }
        } catch (error) {
            console.error("Error updating email:", error);
            MessagePopup.show("Failed to update email due to a network error.", true);
        }
    }

    async function handleProfileUpdate(event) {
        event.preventDefault();

        if (!elements.editUserName || !elements.editPhone || !elements.editAddress || !elements.editBirthDate) {
            MessagePopup.show("Error: Profile update form is incomplete.", true);
            return;
        }

        const formData = {
            userName: elements.editUserName.value.trim(),
            phoneNumber: elements.editPhone.value.trim(),
            address: elements.editAddress.value.trim(),
            birthDate: elements.editBirthDate.value,
        };

        const validation = validateProfileUpdate(formData);
        if (!validation.isValid) {
            if (elements.profileChangesError) {
                elements.profileChangesError.textContent = validation.message;
            }
            return;
        }

        try {
            const response = await ProfileManager.updateDetails(userObject.userID, formData);
            if (!response) {
                MessagePopup.show("Unknown error: Could not update profile.", true);
                return;
            }

            if (!response.success) {
                MessagePopup.show(response.data || "Failed to update profile.", true);
                return;
            }

            MessagePopup.show("Profile details updated successfully.");
            updateUserProfile(response.data);
        } catch (error) {
            console.error("Error updating profile:", error);
            MessagePopup.show("Failed to update profile due to a network error.", true);
        }
    }

    async function handleRechargeBalance() {
        if (
            !elements.rechargeAmountInput ||
            !elements.nameOnCardInput ||
            !elements.cardNumberInput ||
            !elements.expiryMonthSelect ||
            !elements.expiryYearSelect ||
            !elements.cvcInput
        ) {
            MessagePopup.show("Error: Recharge form is incomplete.", true);
            return;
        }

        const amount = Number.parseFloat(elements.rechargeAmountInput.value);

        if (!amount || amount <= 0) {
            MessagePopup.show("Please enter a valid amount!", true);
            return;
        }

        const nameOnCard = elements.nameOnCardInput.value;
        const cardNumber = elements.cardNumberInput.value;
        const expiryMonth = elements.expiryMonthSelect.value;
        const expiryYear = elements.expiryYearSelect.value;
        const cvc = elements.cvcInput.value;

        // Validate required fields
        if (!nameOnCard || !cardNumber || !expiryMonth || !expiryYear || !cvc) {
            MessagePopup.show("Please fill in all payment information!", true);
            return;
        }

        // Validate credit card details
        const creditCardDetails = {
            nameOnCard,
            cardNumber,
            expiryMonth,
            expiryYear: Number.parseInt(expiryYear) + 2000,
            cvc,
        };

        const validationError = DataValidator.isCreditCardValid(creditCardDetails);
        if (validationError) {
            MessagePopup.show(validationError, true);
            return;
        }

        try {
            const response = await ProfileManager.rechargeAccountBalanceUsingCreditCard(
                userObject.userID,
                creditCardDetails,
                amount,
            );

            if (!response) {
                MessagePopup.show("Unknown error: Could not recharge the balance!", true);
                return;
            }

            if (!response.success) {
                MessagePopup.show(response.data || "Failed to recharge balance.", true);
                return;
            }

            MessagePopup.show(response.data);
            closePopup("recharge");

            userObject.accountBalance += amount;
            if (elements.balanceInput) {
                elements.balanceInput.textContent = `${userObject.accountBalance} EGP`;
            }
            UserAuthTracker.userObject = userObject;
        } catch (error) {
            console.error("Error recharging balance:", error);
            MessagePopup.show("Failed to recharge balance due to a network error.", true);
        }
    }
});