'use strict';

export default class DataValidator {
    static emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    static passwordPattern = /^(?=.*[a-z])(?=.*[A-Z]).{8,}$/;

    // Allows letters, numbers, spaces, and basic punctuation
    // 4-30 characters, must start with a letter
    static userNamePattern = /^[a-zA-Z][a-zA-Z0-9 _-]{3,29}$/;

    static phonePattern = /^(?:\+20|0)(10|11|12|15)\d{8}$/;

    // Check for ISO 8601 format.
    static isoDatePattern = /^\d{4}-\d{2}-\d{2}(T\d{2}:\d{2}:\d{2}(\.\d+)?(Z|[+-]\d{2}:\d{2})?)?/;
    static creditCardNumberPattern = /^(\d{4}-){3}\d{4}$/;

    static isNonNegativeInteger(number) {
        return (
            typeof number === 'number' &&
            Number.isInteger(number) &&
            number >= 0
        );
    }

    static isNonNegativeFloat(number) {
        return (
            typeof number === 'number' &&
            Number.isFinite(number) &&
            number >= 0
        )
    }

    static isIDValid(ID) {
        return DataValidator.isNonNegativeInteger(ID) && ID > 0;
    }

    static isEmailValid(email) {
        return DataValidator.emailPattern.test(email);
    }

    static isPasswordValid(password) {
        return DataValidator.passwordPattern.test(password);
    }

    static isUserNameValid(userName) {
        return userName && typeof userName === 'string' && DataValidator.userNamePattern.test(userName);
    }

    static isPhoneValid(phone) {
        return DataValidator.phonePattern.test(phone);
    }

    static isAddressValid(address) {
        if (typeof address !== 'string' || address.length < 4) {
            return false;
        }

        // Cleanse and verify the address contains at least some valid characters
        const CLEANSED_ADDRESS = address
            .replace(/[<>"']/g, '') // Remove dangerous HTML/script tags
            .replace(/\s{2,}/g, ' ') // Collapse multiple spaces
            .trim();

        // Check if cleansed address still meets requirements
        return CLEANSED_ADDRESS.length >= 4 &&
            /[a-zA-Z0-9]/.test(CLEANSED_ADDRESS); // Contains at least one alphanumeric
    }

    static isDateValid(date) {
        if (!date) return false;

        // Check if it's already a valid Date object
        if (date instanceof Date) {
            return !isNaN(date.getTime());
        }

        // Validate ISO format or parseable date string
        try {
            // First, check if the date matches the pattern
            if (!DataValidator.isoDatePattern.test(date)) {
                return false;
            }

            // Then create a Date object and check if it's valid
            const parsedDate = new Date(date);
            return !isNaN(parsedDate.getTime());
        } catch {
            return false;
        }
    }

    static isBirthDateValid(birthDate, minAge = 10, maxAge = 120) {
        if (!this.isDateValid(birthDate)) return false;

        const birthDateObj = new Date(birthDate);
        const currentDate = new Date();

        // Optional: Add age range validation
        const age = currentDate.getFullYear() - birthDateObj.getFullYear();
        return birthDateObj <= currentDate &&
            age >= minAge &&
            age <= maxAge;
    }

    static isCreditCardValid(creditCardDetails) {
        if (!creditCardDetails || typeof creditCardDetails !== 'object') {
            return 'Invalid credit card details object!';
        }

        const { nameOnCard, cardNumber, expiryYear, expiryMonth, cvc } = creditCardDetails;

        if (!nameOnCard || typeof nameOnCard !== 'string' || nameOnCard.trim().length === 0) {
            return 'Name on card is required!';
        }

        if (!cardNumber || typeof cardNumber !== 'string' || cardNumber.trim().length === 0) {
            return 'Card number is required!';
        }

        if (!DataValidator.creditCardNumberPattern.test(cardNumber)) {
            return 'Card number is invalid, must be in the format XXXX-XXXX-XXXX-XXXX!';
        }

        const expiryYearInt = parseInt(expiryYear);
        if (isNaN(expiryYearInt)) {
            return 'Expiry year was not a valid year integer!'
        }

        if (expiryYearInt < new Date().getFullYear()) {
            return 'Expiry year must be in the future!'
        }

        const expiryMonthInt = parseInt(expiryMonth);
        if (isNaN(expiryMonthInt) || expiryMonthInt < 1 || expiryMonthInt > 12) {
            return 'Expiry month must be between 1 and 12!'
        }

        const currentDate = new Date();
        const expiryDate = new Date(expiryYearInt, expiryMonthInt - 1);

        if (expiryDate <= currentDate) {
            return 'Expiry date must be in the future!'
        }

        const cvcInt = parseInt(cvc);
        if (isNaN(cvcInt)) {
            return 'CVC was not a valid number!'
        }

        if (cvcInt < 100 || cvcInt > 999) {
            return 'CVC must be a 3-digit number!'
        }

        return null;
    }
}