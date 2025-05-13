'use strict';

export class ErrorHandler {
    errorDiv;
    #timeoutID
    constructor(errorDiv) {
        this.errorDiv = errorDiv;
        this.#timeoutID = null;
    }
    
    putError(message) {
        this.errorDiv.textContent = message;
        this.#timeoutID = setTimeout(() => {
            this.errorDiv.textContent = '';
        }, 3000);
    }
    
    clearError() {
        this.errorDiv.textContent = '';
        if (this.#timeoutID !== null) {
            clearTimeout(this.#timeoutID);
            this.#timeoutID = null;
        }
    }
}